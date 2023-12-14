

package im.vector.app.features.call.webrtc

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import im.vector.app.ActiveSessionDataSource
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.services.CallService
import im.vector.app.core.utils.toast
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.plan.CallEnded
import im.vector.app.features.analytics.plan.CallStarted
import im.vector.app.features.call.CallProximityManager
import im.vector.app.features.call.VectorCallActivity
import im.vector.app.features.call.audio.CallAudioManager
import im.vector.app.features.call.lookup.CallProtocolsChecker
import im.vector.app.features.call.lookup.CallUserMapper
import im.vector.app.features.call.utils.EglUtils
import im.vector.app.features.call.vectorCallService
import im.vector.app.features.session.coroutineScope
import im.vector.app.push.fcm.FcmHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.CallListener
import org.matrix.android.sdk.api.session.call.CallState
import org.matrix.android.sdk.api.session.call.MxCall
import org.matrix.android.sdk.api.session.room.model.call.CallAnswerContent
import org.matrix.android.sdk.api.session.room.model.call.CallAssertedIdentityContent
import org.matrix.android.sdk.api.session.room.model.call.CallCandidatesContent
import org.matrix.android.sdk.api.session.room.model.call.CallHangupContent
import org.matrix.android.sdk.api.session.room.model.call.CallInviteContent
import org.matrix.android.sdk.api.session.room.model.call.CallNegotiateContent
import org.matrix.android.sdk.api.session.room.model.call.CallRejectContent
import org.matrix.android.sdk.api.session.room.model.call.CallSelectAnswerContent
import org.matrix.android.sdk.api.session.room.model.call.EndCallReason
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.PeerConnectionFactory
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton


private val loggerTag = LoggerTag("WebRtcCallManager", LoggerTag.VOIP)

@Singleton
class WebRtcCallManager @Inject constructor(
        private val context: Context,
        val proximityManager: CallProximityManager,
        private val activeSessionDataSource: ActiveSessionDataSource,
        private val analyticsTracker: AnalyticsTracker
) : CallListener,
        DefaultLifecycleObserver {

    private val currentSession: Session?
        get() = activeSessionDataSource.currentValue?.orNull()

    private val protocolsChecker: CallProtocolsChecker?
        get() = currentSession?.vectorCallService?.protocolChecker

    private val callUserMapper: CallUserMapper?
        get() = currentSession?.vectorCallService?.userMapper

    private val sessionScope: CoroutineScope?
        get() = currentSession?.coroutineScope

    interface Listener {
        fun onCallEnded(callId: String) = Unit
        fun onCurrentCallChange(call: WebRtcCall?) = Unit
        fun onAudioDevicesChange() = Unit
    }

    val supportedPSTNProtocol: String?
        get() = protocolsChecker?.supportedPSTNProtocol

    val supportsPSTNProtocol: Boolean
        get() = supportedPSTNProtocol != null

    val supportsVirtualRooms: Boolean
        get() = protocolsChecker?.supportVirtualRooms.orFalse()

    fun addProtocolsCheckerListener(listener: CallProtocolsChecker.Listener) {
        protocolsChecker?.addListener(listener)
    }

    fun removeProtocolsCheckerListener(listener: CallProtocolsChecker.Listener) {
        protocolsChecker?.removeListener(listener)
    }

    private val currentCallsListeners = CopyOnWriteArrayList<Listener>()

    fun addListener(listener: Listener) {
        currentCallsListeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        currentCallsListeners.remove(listener)
    }

    val audioManager = CallAudioManager(context) {
        currentCallsListeners.forEach {
            tryOrNull { it.onAudioDevicesChange() }
        }
    }.apply {
        setMode(CallAudioManager.Mode.DEFAULT)
    }

    private var peerConnectionFactory: PeerConnectionFactory? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val dispatcher = executor.asCoroutineDispatcher()

    private val rootEglBase by lazy { EglUtils.rootEglBase }

    private var isInBackground: Boolean = true

    override fun onResume(owner: LifecycleOwner) {
        isInBackground = false
    }

    override fun onPause(owner: LifecycleOwner) {
        isInBackground = true
    }

    
    var currentCall: AtomicReference<WebRtcCall?> = AtomicReference(null)
    private fun AtomicReference<WebRtcCall?>.setAndNotify(newValue: WebRtcCall?) {
        set(newValue)
        currentCallsListeners.forEach {
            tryOrNull { it.onCurrentCallChange(newValue) }
        }
    }

    private val advertisedCalls = HashSet<String>()
    private val callsByCallId = ConcurrentHashMap<String, WebRtcCall>()
    private val callsByRoomId = ConcurrentHashMap<String, MutableList<WebRtcCall>>()

    
    
    
    private val transferees = ConcurrentHashMap<String, WebRtcCall>()

    fun getCallById(callId: String): WebRtcCall? {
        return callsByCallId[callId]
    }

    fun getCallsByRoomId(roomId: String): List<WebRtcCall> {
        return callsByRoomId[roomId] ?: emptyList()
    }

    fun getTransfereeForCallId(callId: String): WebRtcCall? {
        return transferees[callId]
    }

    fun getCurrentCall(): WebRtcCall? {
        return currentCall.get()
    }

    fun getCalls(): List<WebRtcCall> {
        return callsByCallId.values.toList()
    }

    fun checkForProtocolsSupportIfNeeded() {
        protocolsChecker?.checkProtocols()
    }

    
    fun getAdvertisedCalls() = advertisedCalls

    fun headSetButtonTapped() {
        Timber.tag(loggerTag.value).v("headSetButtonTapped")
        val call = getCurrentCall() ?: return
        if (call.mxCall.state is CallState.LocalRinging) {
            call.acceptIncomingCall()
        }
        if (call.mxCall.state is CallState.Connected) {
            
            call.endCall()
        }
    }

    private fun createPeerConnectionFactoryIfNeeded() {
        if (peerConnectionFactory != null) return
        Timber.tag(loggerTag.value).v("createPeerConnectionFactory")
        val eglBaseContext = rootEglBase?.eglBaseContext ?: return Unit.also {
            Timber.tag(loggerTag.value).e("No EGL BASE")
        }

        Timber.tag(loggerTag.value).v("PeerConnectionFactory.initialize")
        PeerConnectionFactory.initialize(
                PeerConnectionFactory
                        .InitializationOptions.builder(context.applicationContext)
                        .createInitializationOptions()
        )

        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(
                eglBaseContext,
                
                true,
                
                true
        )
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)
        Timber.tag(loggerTag.value).v("PeerConnectionFactory.createPeerConnectionFactory ...")
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory()
    }

    private fun onCallActive(call: WebRtcCall) {
        Timber.tag(loggerTag.value).v("WebRtcPeerConnectionManager onCall active: ${call.mxCall.callId}")
        val currentCall = getCurrentCall().takeIf { it != call }
        currentCall?.updateRemoteOnHold(onHold = true)
        audioManager.setMode(if (call.mxCall.isVideoCall) CallAudioManager.Mode.VIDEO_CALL else CallAudioManager.Mode.AUDIO_CALL)
        call.trackCallStarted()
        this.currentCall.setAndNotify(call)
    }

    private fun onCallEnded(callId: String, endCallReason: EndCallReason, rejected: Boolean) {
        Timber.tag(loggerTag.value).v("onCall ended: $callId")
        val webRtcCall = callsByCallId.remove(callId) ?: return Unit.also {
            Timber.tag(loggerTag.value).v("On call ended for unknown call $callId")
        }
        webRtcCall.trackCallEnded()
        CallService.onCallTerminated(context, callId, endCallReason, rejected)
        callsByRoomId[webRtcCall.signalingRoomId]?.remove(webRtcCall)
        callsByRoomId[webRtcCall.nativeRoomId]?.remove(webRtcCall)
        transferees.remove(callId)
        if (currentCall.get()?.callId == callId) {
            val otherCall = getCalls().lastOrNull()
            currentCall.setAndNotify(otherCall)
        }
        tryOrNull {
            currentCallsListeners.forEach { it.onCallEnded(callId) }
        }
        
        if (getCurrentCall() == null) {
            Timber.tag(loggerTag.value).v("Dispose peerConnectionFactory as there is no need to keep one")
            peerConnectionFactory?.dispose()
            peerConnectionFactory = null
            audioManager.setMode(CallAudioManager.Mode.DEFAULT)
            
            if (isInBackground) {
                if (FcmHelper.isPushSupported()) {
                    currentSession?.stopAnyBackgroundSync()
                } else {
                    
                    
                }
            }
        }
        proximityManager.stop()
    }

    suspend fun startOutgoingCall(nativeRoomId: String, otherUserId: String, isVideoCall: Boolean, transferee: WebRtcCall? = null, callPhone: String = "") {
        val signalingRoomId = callUserMapper?.getOrCreateVirtualRoomForRoom(nativeRoomId, otherUserId) ?: nativeRoomId
        Timber.tag(loggerTag.value).v("startOutgoingCall in room $signalingRoomId to $otherUserId isVideo $isVideoCall")
        if (getCallsByRoomId(nativeRoomId).isNotEmpty()) {
            getCallsByRoomId(nativeRoomId)
            Timber.tag(loggerTag.value).w("startOutgoingCall you already have a call in this room")
            return
        }
        if (getCurrentCall() != null && getCurrentCall()?.mxCall?.state !is CallState.Connected || getCalls().size >= 2) {
            Timber.tag(loggerTag.value).w("cannot start outgoing call")
            
            return
        }

        var callPhoneInner = callPhone
        try {
            if (callPhoneInner.isBlank()) {
                val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
                val user = activeSession.resolveUserOnline(otherUserId)
                if ((user.tel_numbers?.size ?: 0) > 0) {
                    val get = user.tel_numbers?.get(0)
                    callPhoneInner = get?.toBigDecimal()?.toPlainString() ?: ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        executor.execute {
            createPeerConnectionFactoryIfNeeded()
        }
        getCurrentCall()?.updateRemoteOnHold(onHold = true)
        val mxCall = currentSession?.callSignalingService()?.createOutgoingCall(signalingRoomId, otherUserId, isVideoCall) ?: return
        val webRtcCall = createWebRtcCall(mxCall, nativeRoomId).apply {
            out_phone = callPhoneInner
        }
        currentCall.setAndNotify(webRtcCall)
        if (transferee != null) {
            transferees[webRtcCall.callId] = transferee
        }
        CallService.onOutgoingCallRinging(
                context = context.applicationContext,
                callId = mxCall.callId
        )

        
        context.startActivity(VectorCallActivity.newIntent(context, webRtcCall, VectorCallActivity.OUTGOING_CREATED))
    }

    override fun onCallIceCandidateReceived(mxCall: MxCall, iceCandidatesContent: CallCandidatesContent) {
        Timber.tag(loggerTag.value).v("onCallIceCandidateReceived for call ${mxCall.callId}")
        val call = callsByCallId[iceCandidatesContent.callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallIceCandidateReceived for non active call? ${iceCandidatesContent.callId}")
                }
        call.onCallIceCandidateReceived(iceCandidatesContent)
    }

    private fun createWebRtcCall(mxCall: MxCall, nativeRoomId: String): WebRtcCall {
        val webRtcCall = WebRtcCall(
                mxCall = mxCall,
                nativeRoomId = nativeRoomId,
                rootEglBase = rootEglBase,
                context = context,
                dispatcher = dispatcher,
                peerConnectionFactoryProvider = {
                    createPeerConnectionFactoryIfNeeded()
                    peerConnectionFactory
                },
                sessionProvider = { currentSession },
                onCallBecomeActive = this::onCallActive,
                onCallEnded = this::onCallEnded
        )
        advertisedCalls.add(mxCall.callId)
        callsByCallId[mxCall.callId] = webRtcCall
        callsByRoomId.getOrPut(nativeRoomId) { ArrayList(1) }
                .add(webRtcCall)
        callsByRoomId.getOrPut(mxCall.roomId) { ArrayList(1) }
                .add(webRtcCall)
        if (getCurrentCall() == null) {
            currentCall.setAndNotify(webRtcCall)
        }
        return webRtcCall
    }

    fun endCallForRoom(roomId: String) {
        callsByRoomId[roomId]?.firstOrNull()?.endCall()
    }

    override fun onCallInviteReceived(mxCall: MxCall, callInviteContent: CallInviteContent) {
        Timber.tag(loggerTag.value).v("onCallInviteReceived callId ${mxCall.callId}")
        val nativeRoomId = callUserMapper?.nativeRoomForVirtualRoom(mxCall.roomId) ?: mxCall.roomId
        if (getCallsByRoomId(nativeRoomId).isNotEmpty()) {
            val rtcCalls = getCallsByRoomId(nativeRoomId)
            rtcCalls.forEach {
                it.endCall(EndCallReason.USER_HANGUP)
                context.toast(R.string.user_busy_hangup)
            }
            Timber.tag(loggerTag.value).w("onCallInviteReceived  you already have a call in this room")
            return
        }
        if ((getCurrentCall() != null && getCurrentCall()?.mxCall?.state !is CallState.Connected) || getCalls().size >= 2) {
            Timber.tag(loggerTag.value).w("receiving incoming call but cannot handle it")
            
            return
        }
        createWebRtcCall(mxCall, nativeRoomId).apply {
            offerSdp = callInviteContent.offer
            in_phone = callInviteContent.current_phone
        }
        
        CallService.onIncomingCallRinging(
                context = context,
                callId = mxCall.callId,
                isInBackground = isInBackground
        )
        
        
        
        if (isInBackground) {
            if (FcmHelper.isPushSupported()) {
                
                currentSession?.startAutomaticBackgroundSync(30, 0)
            } else {
                
            }
        }
    }

    override fun onCallAnswerReceived(callAnswerContent: CallAnswerContent) {
        val call = callsByCallId[callAnswerContent.callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallAnswerReceived for non active call? ${callAnswerContent.callId}")
                }
        val mxCall = call.mxCall
        
        CallService.onPendingCall(
                context = context,
                callId = mxCall.callId
        )
        call.onCallAnswerReceived(callAnswerContent)
    }

    override fun onCallHangupReceived(callHangupContent: CallHangupContent) {
        val call = callsByCallId[callHangupContent.callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallHangupReceived for non active call? ${callHangupContent.callId}")
                }
        call.onCallHangupReceived(callHangupContent)
    }

    override fun onCallRejectReceived(callRejectContent: CallRejectContent) {
        val call = callsByCallId[callRejectContent.callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallRejectReceived for non active call? ${callRejectContent.callId}")
                }
        call.onCallRejectReceived(callRejectContent)
    }

    override fun onCallSelectAnswerReceived(callSelectAnswerContent: CallSelectAnswerContent) {
        val call = callsByCallId[callSelectAnswerContent.callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallSelectAnswerReceived for non active call? ${callSelectAnswerContent.callId}")
                }
        call.onCallSelectedAnswerReceived(callSelectAnswerContent)
    }

    override fun onCallNegotiateReceived(callNegotiateContent: CallNegotiateContent) {
        val call = callsByCallId[callNegotiateContent.callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallNegotiateReceived for non active call? ${callNegotiateContent.callId}")
                }
        call.onCallNegotiateReceived(callNegotiateContent)
    }

    override fun onCallManagedByOtherSession(callId: String) {
        Timber.tag(loggerTag.value).v("onCallManagedByOtherSession: $callId")
        val call = callsByCallId[callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallManagedByOtherSession for non active call? $callId")
                }
        call.endCall(EndCallReason.ANSWERED_ELSEWHERE, sendSignaling = false)
    }

    override fun onCallAssertedIdentityReceived(callAssertedIdentityContent: CallAssertedIdentityContent) {
        if (!BuildConfig.handleCallAssertedIdentityEvents) {
            return
        }
        val call = callsByCallId[callAssertedIdentityContent.callId]
                ?: return Unit.also {
                    Timber.tag(loggerTag.value).w("onCallAssertedIdentityReceived for non active call? ${callAssertedIdentityContent.callId}")
                }
        call.onCallAssertedIdentityReceived(callAssertedIdentityContent)
    }

    
    private fun WebRtcCall.trackCallStarted() {
        analyticsTracker.capture(
                CallStarted(
                        isVideo = mxCall.isVideoCall,
                        numParticipants = 2,
                        placed = mxCall.isOutgoing
                )
        )
    }

    private fun WebRtcCall.trackCallEnded() {
        analyticsTracker.capture(
                CallEnded(
                        durationMs = durationMillis(),
                        isVideo = mxCall.isVideoCall,
                        numParticipants = 2,
                        placed = mxCall.isOutgoing
                )
        )
    }
}
