

package im.vector.app.features.call.webrtc

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.core.content.getSystemService
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.services.CallService
import im.vector.app.core.utils.PublishDataSource
import im.vector.app.core.utils.TextUtils.formatDuration
import im.vector.app.features.call.CameraEventsHandlerAdapter
import im.vector.app.features.call.CameraProxy
import im.vector.app.features.call.CameraType
import im.vector.app.features.call.CaptureFormat
import im.vector.app.features.call.VectorCallActivity
import im.vector.app.features.call.lookup.sipNativeLookup
import im.vector.app.features.call.utils.asWebRTC
import im.vector.app.features.call.utils.awaitCreateAnswer
import im.vector.app.features.call.utils.awaitCreateOffer
import im.vector.app.features.call.utils.awaitSetLocalDescription
import im.vector.app.features.call.utils.awaitSetRemoteDescription
import im.vector.app.features.call.utils.mapToCallCandidate
import im.vector.app.features.session.coroutineScope
import im.vector.app.provide.ChatStatusProvide.insetChatLog
import im.vector.lib.core.utils.flow.chunk
import im.vector.lib.core.utils.timer.CountUpTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.CallIdGenerator
import org.matrix.android.sdk.api.session.call.CallState
import org.matrix.android.sdk.api.session.call.MxCall
import org.matrix.android.sdk.api.session.call.MxPeerConnectionState
import org.matrix.android.sdk.api.session.call.TurnServerResponse
import org.matrix.android.sdk.api.session.room.model.call.CallAnswerContent
import org.matrix.android.sdk.api.session.room.model.call.CallAssertedIdentityContent
import org.matrix.android.sdk.api.session.room.model.call.CallCandidatesContent
import org.matrix.android.sdk.api.session.room.model.call.CallHangupContent
import org.matrix.android.sdk.api.session.room.model.call.CallInviteContent
import org.matrix.android.sdk.api.session.room.model.call.CallNegotiateContent
import org.matrix.android.sdk.api.session.room.model.call.CallRejectContent
import org.matrix.android.sdk.api.session.room.model.call.CallSelectAnswerContent
import org.matrix.android.sdk.api.session.room.model.call.EndCallReason
import org.matrix.android.sdk.api.session.room.model.call.SdpType
import org.matrix.android.sdk.internal.database.model.ChatPhoneLog
import org.threeten.bp.Duration
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpTransceiver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Provider
import kotlin.coroutines.CoroutineContext

private const val STREAM_ID = "userMedia"
private const val AUDIO_TRACK_ID = "${STREAM_ID}a0"
private const val VIDEO_TRACK_ID = "${STREAM_ID}v0"
private val DEFAULT_AUDIO_CONSTRAINTS = MediaConstraints()
private const val INVITE_TIMEOUT_IN_MS = 60_000L

private val loggerTag = LoggerTag("WebRtcCall", LoggerTag.VOIP)

class WebRtcCall(
        val mxCall: MxCall,
        
        
        val nativeRoomId: String,
        private val rootEglBase: EglBase?,
        private val context: Context,
        private val dispatcher: CoroutineContext,
        private val sessionProvider: Provider<Session?>,
        private val peerConnectionFactoryProvider: Provider<PeerConnectionFactory?>,
        private val onCallBecomeActive: (WebRtcCall) -> Unit,
        private val onCallEnded: (String, EndCallReason, Boolean) -> Unit
) : MxCall.StateListener {

    interface Listener : MxCall.StateListener {
        fun onCaptureStateChanged() {}
        fun onCameraChanged() {}
        fun onHoldUnhold() {}
        fun assertedIdentityChanged() {}
        fun onTick(formattedDuration: String) {}
        override fun onStateUpdate(call: MxCall) {}
    }

    private val listeners = CopyOnWriteArrayList<Listener>()

    private val sessionScope: CoroutineScope?
        get() = sessionProvider.get()?.coroutineScope

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    val callId = mxCall.callId

    
    val signalingRoomId = mxCall.roomId

    private var peerConnection: PeerConnection? = null
    private var localAudioSource: AudioSource? = null
    private var localAudioTrack: AudioTrack? = null
    private var localVideoSource: VideoSource? = null
    private var localVideoTrack: VideoTrack? = null
    private var remoteAudioTrack: AudioTrack? = null
    private var remoteVideoTrack: VideoTrack? = null

    
    private var makingOffer: Boolean = false
    private var ignoreOffer: Boolean = false

    private var videoCapturer: CameraVideoCapturer? = null

    private val availableCamera = ArrayList<CameraProxy>()
    private var cameraInUse: CameraProxy? = null
    private var currentCaptureFormat: CaptureFormat = CaptureFormat.HD
    private var cameraAvailabilityCallback: CameraManager.AvailabilityCallback? = null

    private val timer = CountUpTimer(1000L).apply {
        tickListener = object : CountUpTimer.TickListener {
            override fun onTick(milliseconds: Long) {
                val formattedDuration = formatDuration(Duration.ofMillis(milliseconds))
                listeners.forEach {
                    tryOrNull { it.onTick(formattedDuration) }
                }
            }
        }
    }

    private var inviteTimeout: Deferred<Unit>? = null

    
    var micMuted = false
        private set
    var videoMuted = false
        private set
    var isRemoteOnHold = false
        private set
    var isLocalOnHold = false
        private set

    
    private var wasLocalOnHold = false
    var remoteAssertedIdentity: CallAssertedIdentityContent.AssertedIdentity? = null
        private set

    var offerSdp: CallInviteContent.Offer? = null

    
    var in_phone: String = ""

    
    var out_phone: String = ""

    var videoCapturerIsInError = false
        set(value) {
            field = value
            listeners.forEach {
                tryOrNull { it.onCaptureStateChanged() }
            }
        }
    private var localSurfaceRenderers: MutableList<WeakReference<SurfaceViewRenderer>> = ArrayList()
    private var remoteSurfaceRenderers: MutableList<WeakReference<SurfaceViewRenderer>> = ArrayList()

    private val localIceCandidateSource = PublishDataSource<IceCandidate>()
    private var localIceCandidateJob: Job? = null

    private val remoteCandidateSource: MutableSharedFlow<IceCandidate> = MutableSharedFlow(replay = Int.MAX_VALUE)
    private var remoteIceCandidateJob: Job? = null

    init {
        setupLocalIceCanditate()
        mxCall.addListener(this)
    }

    fun IceCandidate.toNewString(): String {
        return "(#beigin#)" + this.sdpMid + "--------" + this.sdpMLineIndex + "--------" + this.sdp + "--------" + this.serverUrl + "--------" + this.adapterType.toString();
    }

    private fun setupLocalIceCanditate() {
        sessionScope?.let {
            localIceCandidateJob = localIceCandidateSource.stream()
                    .chunk(300)
                    .onEach {
                        
                        if (it.isNotEmpty()) {
                            Timber.tag(loggerTag.value).v("Sending local ice candidates to call ${it}")
                            Timber.tag(loggerTag.value).v("----candidates--------beign----------- ${System.currentTimeMillis()}")
                            it.forEach {
                                Timber.tag(loggerTag.value).v(it.toNewString())
                            }
                            Timber.tag(loggerTag.value).v("----candidates--------end----------- ${System.currentTimeMillis()}")
                            
                            mxCall.sendLocalCallCandidates(it.mapToCallCandidate())
                        }
                    }.launchIn(it)
        }
    }

    fun onIceCandidate(iceCandidate: IceCandidate) = localIceCandidateSource.post(iceCandidate)

    fun onRenegotiationNeeded(restartIce: Boolean) {
        sessionScope?.launch(dispatcher) {
            if (mxCall.state != CallState.CreateOffer && mxCall.opponentVersion == 0) {
                Timber.tag(loggerTag.value).v("Opponent does not support renegotiation: ignoring onRenegotiationNeeded event")
                return@launch
            }
            val constraints = MediaConstraints()
            if (restartIce) {
                constraints.mandatory.add(MediaConstraints.KeyValuePair("IceRestart", "true"))
            }
            val peerConnection = peerConnection ?: return@launch
            Timber.tag(loggerTag.value).v(" $restartIce   onRenegotiationNeeded  creating offer...")
            makingOffer = true
            try {
                val sessionDescription = peerConnection.awaitCreateOffer(constraints) ?: return@launch
                peerConnection.awaitSetLocalDescription(sessionDescription)
                if (peerConnection.iceGatheringState() == PeerConnection.IceGatheringState.GATHERING) {
                    
                    delay(200)
                }
                if (mxCall.state is CallState.Ended) {
                    return@launch
                }
                if (mxCall.state == CallState.CreateOffer) {
                    
                    mxCall.offerSdp(sessionDescription.description)
                    inviteTimeout = async {
                        delay(INVITE_TIMEOUT_IN_MS)
                        endCall(EndCallReason.INVITE_TIMEOUT)
                    }
                } else {
                    mxCall.negotiate(sessionDescription.description, SdpType.OFFER)
                }
            } catch (failure: Throwable) {
                
                Timber.tag(loggerTag.value).v("Failure while creating offer")
            } finally {
                makingOffer = false
            }
        }
    }

    fun durationMillis(): Int {
        return timer.elapsedTime().toInt()
    }

    fun formattedDuration(): String {
        return formatDuration(
                Duration.ofMillis(timer.elapsedTime())
        )
    }

    private fun createPeerConnection(turnServerResponse: TurnServerResponse?) {
        val peerConnectionFactory = peerConnectionFactoryProvider.get() ?: return
        val iceServers = mutableListOf<PeerConnection.IceServer>().apply {
            turnServerResponse?.let { server ->
                server.uris?.forEach { uri ->
                    add(
                            PeerConnection
                                    .IceServer
                                    .builder(uri)
                                    .setUsername(server.username)
                                    .setPassword(server.password)
                                    .createIceServer()
                    )
                }
            }
        }
        Timber.tag(loggerTag.value).v("creating peer connection...with iceServers $iceServers ")
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, PeerConnectionObserver(this))
    }

    
    fun transferToUser(targetUserId: String, targetRoomId: String?) {
        sessionScope?.launch(dispatcher) {
            mxCall.transfer(
                    targetUserId = targetUserId,
                    targetRoomId = targetRoomId,
                    createCallId = CallIdGenerator.generate(),
                    awaitCallId = null
            )
            terminate(EndCallReason.REPLACED)
        }
    }

    
    fun transferToCall(transferTargetCall: WebRtcCall) {
        sessionScope?.launch(dispatcher) {
            val newCallId = CallIdGenerator.generate()
            transferTargetCall.mxCall.transfer(
                    targetUserId = mxCall.opponentUserId,
                    targetRoomId = null,
                    createCallId = null,
                    awaitCallId = newCallId
            )
            mxCall.transfer(
                    targetUserId = transferTargetCall.mxCall.opponentUserId,
                    targetRoomId = null,
                    createCallId = newCallId,
                    awaitCallId = null
            )
            terminate(EndCallReason.REPLACED)
            transferTargetCall.terminate(EndCallReason.REPLACED)
        }
    }

    fun acceptIncomingCall() {
        sessionScope?.launch {
            Timber.tag(loggerTag.value).v("acceptIncomingCall from state ${mxCall.state}")
            if (mxCall.state == CallState.LocalRinging) {
                internalAcceptIncomingCall()
            }
        }
    }

    
    fun sendDtmfDigit(digit: String) {
        sessionScope?.launch {
            for (sender in peerConnection?.senders.orEmpty()) {
                if (sender.track()?.kind() == "audio" && sender.dtmf()?.canInsertDtmf() == true) {
                    try {
                        sender.dtmf()?.insertDtmf(digit, 100, 70)
                        return@launch
                    } catch (failure: Throwable) {
                        Timber.tag(loggerTag.value).v("Fail to send Dtmf digit")
                    }
                }
            }
        }
    }

    fun attachViewRenderers(localViewRenderer: SurfaceViewRenderer?, remoteViewRenderer: SurfaceViewRenderer, mode: String?) {
        sessionScope?.launch(dispatcher) {
            Timber.tag(loggerTag.value).v("attachViewRenderers localRenderer $localViewRenderer / $remoteViewRenderer")
            localSurfaceRenderers.addIfNeeded(localViewRenderer)
            remoteSurfaceRenderers.addIfNeeded(remoteViewRenderer)
            when (mode) {
                VectorCallActivity.INCOMING_ACCEPT  -> {
                    internalAcceptIncomingCall()
                }
                VectorCallActivity.INCOMING_RINGING -> {
                    
                    
                }
                VectorCallActivity.OUTGOING_CREATED -> {
                    setupOutgoingCall()
                }
                else                                -> {
                    
                    attachViewRenderersInternal()
                }
            }
        }
    }

    private suspend fun attachViewRenderersInternal() = withContext(dispatcher) {
        
        localSurfaceRenderers.forEach { renderer ->
            renderer.get()?.let { pipSurface ->
                pipSurface.setMirror(cameraInUse?.type == CameraType.FRONT)
                
                localVideoTrack?.addSink(pipSurface)
            }
        }

        
        remoteSurfaceRenderers.forEach { renderer ->
            renderer.get()?.let { participantSurface ->
                remoteVideoTrack?.addSink(participantSurface)
            }
        }
    }

    fun detachRenderers(renderers: List<SurfaceViewRenderer>?) {
        sessionScope?.launch(dispatcher) {
            detachRenderersInternal(renderers)
        }
    }

    private suspend fun detachRenderersInternal(renderers: List<SurfaceViewRenderer>?) = withContext(dispatcher) {
        Timber.tag(loggerTag.value).v("detachRenderers")
        if (renderers.isNullOrEmpty()) {
            
            localSurfaceRenderers.forEach {
                if (it.get() != null) localVideoTrack?.removeSink(it.get())
            }
            remoteSurfaceRenderers.forEach {
                if (it.get() != null) remoteVideoTrack?.removeSink(it.get())
            }
            localSurfaceRenderers.clear()
            remoteSurfaceRenderers.clear()
        } else {
            renderers.forEach {
                localSurfaceRenderers.removeIfNeeded(it)
                remoteSurfaceRenderers.removeIfNeeded(it)
                
                localVideoTrack?.removeSink(it)
                remoteVideoTrack?.removeSink(it)
            }
        }
    }

    private suspend fun setupOutgoingCall() = withContext(dispatcher) {
        tryOrNull {
            onCallBecomeActive(this@WebRtcCall)
        }
        val turnServer = getTurnServer()
        mxCall.state = CallState.CreateOffer
        
        createPeerConnection(turnServer)
        
        createLocalStream()
        attachViewRenderersInternal()
        Timber.tag(loggerTag.value).v("remoteCandidateSource $remoteCandidateSource")
        remoteIceCandidateJob = remoteCandidateSource
                .onEach {
                    Timber.tag(loggerTag.value).v("adding remote ice candidate $it")
                    peerConnection?.addIceCandidate(it)
                }
                .catch {
                    Timber.tag(loggerTag.value).v("failed to add remote ice candidate $it")
                }
                .launchIn(this)
        
    }

    private suspend fun internalAcceptIncomingCall() = withContext(dispatcher) {
        tryOrNull {
            onCallBecomeActive(this@WebRtcCall)
        }
        val turnServerResponse = getTurnServer()
        
        withContext(Dispatchers.Main) {
            CallService.onPendingCall(
                    context = context,
                    callId = mxCall.callId
            )
        }
        
        createPeerConnection(turnServerResponse)

        
        
        val offerSdp = offerSdp?.sdp?.let {
            SessionDescription(SessionDescription.Type.OFFER, it)
        }
        if (offerSdp == null) {
            Timber.tag(loggerTag.value).v("We don't have any offer to process")
            return@withContext
        }
        Timber.tag(loggerTag.value).v("Offer sdp for invite: ${offerSdp.description}")
        try {
            peerConnection?.awaitSetRemoteDescription(offerSdp)
        } catch (failure: Throwable) {
            Timber.tag(loggerTag.value).v("Failure putting remote description")
            endCall(reason = EndCallReason.UNKWOWN_ERROR)
            return@withContext
        }
        
        createLocalStream()
        attachViewRenderersInternal()

        
        createAnswer()?.also {
            mxCall.accept(it.description)
        }
        Timber.tag(loggerTag.value).v("remoteCandidateSource $remoteCandidateSource")
        remoteIceCandidateJob = remoteCandidateSource
                .onEach {
                    Timber.tag(loggerTag.value).v("adding remote ice candidate $it")
                    peerConnection?.addIceCandidate(it)
                }.catch {
                    Timber.tag(loggerTag.value).v("failed to add remote ice candidate $it")
                }.launchIn(this)
    }

    private suspend fun getTurnServer(): TurnServerResponse? {
        return tryOrNull {
            sessionProvider.get()?.callSignalingService()?.getTurnServer()
        }
    }

    private fun createLocalStream() {
        val peerConnectionFactory = peerConnectionFactoryProvider.get() ?: return
        Timber.tag(loggerTag.value).v("Create local stream for call ${mxCall.callId}")
        configureAudioTrack(peerConnectionFactory)
        
        if (mxCall.isVideoCall) {
            configureVideoTrack(peerConnectionFactory)
        }
        updateMuteStatus()
    }

    private fun configureAudioTrack(peerConnectionFactory: PeerConnectionFactory) {
        val audioSource = peerConnectionFactory.createAudioSource(DEFAULT_AUDIO_CONSTRAINTS)
        val audioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
        audioTrack.setEnabled(true)
        Timber.tag(loggerTag.value).v("Add audio track $AUDIO_TRACK_ID to call ${mxCall.callId}")
        peerConnection?.addTrack(audioTrack, listOf(STREAM_ID))
        localAudioSource = audioSource
        localAudioTrack = audioTrack
    }

    private fun configureVideoTrack(peerConnectionFactory: PeerConnectionFactory) {
        val cameraIterator = if (Camera2Enumerator.isSupported(context)) {
            Camera2Enumerator(context)
        } else {
            Camera1Enumerator(false)
        }
        
        val frontCamera = cameraIterator.deviceNames
                ?.firstOrNull { cameraIterator.isFrontFacing(it) }
                ?.let {
                    CameraProxy(it, CameraType.FRONT).also { availableCamera.add(it) }
                }

        val backCamera = cameraIterator.deviceNames
                ?.firstOrNull { cameraIterator.isBackFacing(it) }
                ?.let {
                    CameraProxy(it, CameraType.BACK).also { availableCamera.add(it) }
                }

        val camera = frontCamera?.also { cameraInUse = frontCamera }
                ?: backCamera?.also { cameraInUse = backCamera }
                ?: null.also { cameraInUse = null }

        listeners.forEach {
            tryOrNull { it.onCameraChanged() }
        }

        if (camera != null) {
            val videoCapturer = cameraIterator.createCapturer(camera.name, object : CameraEventsHandlerAdapter() {
                override fun onFirstFrameAvailable() {
                    super.onFirstFrameAvailable()
                    videoCapturerIsInError = false
                }

                override fun onCameraClosed() {
                    super.onCameraClosed()
                    Timber.tag(loggerTag.value).v("onCameraClosed")
                    
                    
                    videoCapturerIsInError = true
                    val cameraManager = context.getSystemService<CameraManager>()
                    cameraAvailabilityCallback = object : CameraManager.AvailabilityCallback() {
                        override fun onCameraUnavailable(cameraId: String) {
                            super.onCameraUnavailable(cameraId)
                            Timber.tag(loggerTag.value).v("On camera unavailable: $cameraId")
                        }

                        override fun onCameraAccessPrioritiesChanged() {
                            super.onCameraAccessPrioritiesChanged()
                            Timber.tag(loggerTag.value).v("onCameraAccessPrioritiesChanged")
                        }

                        override fun onCameraAvailable(cameraId: String) {
                            Timber.tag(loggerTag.value).v("On camera available: $cameraId")
                            if (cameraId == camera.name) {
                                videoCapturer?.startCapture(currentCaptureFormat.width, currentCaptureFormat.height, currentCaptureFormat.fps)
                                cameraManager?.unregisterAvailabilityCallback(this)
                            }
                        }
                    }
                    cameraManager?.registerAvailabilityCallback(cameraAvailabilityCallback!!, null)
                }
            })

            val videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
            val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase!!.eglBaseContext)
            Timber.tag(loggerTag.value).v("Local video source created")

            videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
            
            videoCapturer.startCapture(currentCaptureFormat.width, currentCaptureFormat.height, currentCaptureFormat.fps)
            this.videoCapturer = videoCapturer

            val videoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
            Timber.tag(loggerTag.value).v("Add video track $VIDEO_TRACK_ID to call ${mxCall.callId}")
            videoTrack.setEnabled(true)
            peerConnection?.addTrack(videoTrack, listOf(STREAM_ID))
            localVideoSource = videoSource
            localVideoTrack = videoTrack
        }
    }

    fun setCaptureFormat(format: CaptureFormat) {
        sessionScope?.launch(dispatcher) {
            Timber.tag(loggerTag.value).v("setCaptureFormat $format")
            videoCapturer?.changeCaptureFormat(format.width, format.height, format.fps)
            currentCaptureFormat = format
        }
    }

    private fun updateMuteStatus() {
        val micShouldBeMuted = micMuted || isRemoteOnHold
        localAudioTrack?.setEnabled(!micShouldBeMuted)
        remoteAudioTrack?.setEnabled(!isRemoteOnHold)
        val vidShouldBeMuted = videoMuted || isRemoteOnHold
        localVideoTrack?.setEnabled(!vidShouldBeMuted)
        remoteVideoTrack?.setEnabled(!isRemoteOnHold)
    }

    
    private fun computeIsLocalOnHold(): Boolean {
        if (mxCall.state !is CallState.Connected) return false
        var callOnHold = true
        
        
        for (transceiver in peerConnection?.transceivers ?: emptyList()) {
            val trackOnHold = transceiver.currentDirection == RtpTransceiver.RtpTransceiverDirection.INACTIVE ||
                    transceiver.currentDirection == RtpTransceiver.RtpTransceiverDirection.RECV_ONLY
            if (!trackOnHold) callOnHold = false
        }
        return callOnHold
    }

    fun updateRemoteOnHold(onHold: Boolean) {
        sessionScope?.launch(dispatcher) {
            if (isRemoteOnHold == onHold) return@launch
            val direction: RtpTransceiver.RtpTransceiverDirection
            if (onHold) {
                wasLocalOnHold = isLocalOnHold
                isRemoteOnHold = true
                isLocalOnHold = true
                direction = RtpTransceiver.RtpTransceiverDirection.SEND_ONLY
                timer.pause()
            } else {
                isRemoteOnHold = false
                isLocalOnHold = wasLocalOnHold
                onCallBecomeActive(this@WebRtcCall)
                direction = RtpTransceiver.RtpTransceiverDirection.SEND_RECV
                if (!isLocalOnHold) {
                    timer.resume()
                }
            }
            for (transceiver in peerConnection?.transceivers ?: emptyList()) {
                transceiver.direction = direction
            }
            updateMuteStatus()
            listeners.forEach {
                tryOrNull { it.onHoldUnhold() }
            }
        }
    }

    fun muteCall(muted: Boolean) {
        sessionScope?.launch(dispatcher) {
            micMuted = muted
            updateMuteStatus()
        }
    }

    fun enableVideo(enabled: Boolean) {
        sessionScope?.launch(dispatcher) {
            videoMuted = !enabled
            updateMuteStatus()
        }
    }

    fun canSwitchCamera(): Boolean {
        return availableCamera.size > 1
    }

    private fun getOppositeCameraIfAny(): CameraProxy? {
        val currentCamera = cameraInUse ?: return null
        return if (currentCamera.type == CameraType.FRONT) {
            availableCamera.firstOrNull { it.type == CameraType.BACK }
        } else {
            availableCamera.firstOrNull { it.type == CameraType.FRONT }
        }
    }

    fun switchCamera() {
        sessionScope?.launch(dispatcher) {
            Timber.tag(loggerTag.value).v("switchCamera")
            if (mxCall.state is CallState.Connected && mxCall.isVideoCall) {
                val oppositeCamera = getOppositeCameraIfAny() ?: return@launch
                videoCapturer?.switchCamera(
                        object : CameraVideoCapturer.CameraSwitchHandler {
                            
                            override fun onCameraSwitchDone(isFrontCamera: Boolean) {
                                Timber.tag(loggerTag.value).v("onCameraSwitchDone isFront $isFrontCamera")
                                cameraInUse = oppositeCamera
                                localSurfaceRenderers.forEach {
                                    it.get()?.setMirror(isFrontCamera)
                                }
                                listeners.forEach {
                                    tryOrNull { it.onCameraChanged() }
                                }
                            }

                            override fun onCameraSwitchError(errorDescription: String?) {
                                Timber.tag(loggerTag.value).v("onCameraSwitchError isFront $errorDescription")
                            }
                        }, oppositeCamera.name
                )
            }
        }
    }

    private suspend fun createAnswer(): SessionDescription? {
        Timber.tag(loggerTag.value).w("createAnswer")
        val peerConnection = peerConnection ?: return null
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", if (mxCall.isVideoCall) "true" else "false"))
        }
        return try {
            val localDescription = peerConnection.awaitCreateAnswer(constraints) ?: return null
            peerConnection.awaitSetLocalDescription(localDescription)
            localDescription
        } catch (failure: Throwable) {
            Timber.tag(loggerTag.value).v("Fail to create answer")
            null
        }
    }

    fun currentCameraType(): CameraType? {
        return cameraInUse?.type
    }

    fun currentCaptureFormat(): CaptureFormat {
        return currentCaptureFormat
    }

    fun startSharingScreen() {
        
    }

    fun stopSharingScreen() {
        
    }

    private suspend fun release() {
        listeners.clear()
        mxCall.removeListener(this)
        timer.stop()
        timer.tickListener = null
        detachRenderersInternal(null)
        videoCapturer?.stopCapture()
        videoCapturer?.dispose()
        videoCapturer = null
        remoteIceCandidateJob?.cancel()
        localIceCandidateJob?.cancel()
        peerConnection?.close()
        peerConnection?.dispose()
        localAudioSource?.dispose()
        localVideoSource?.dispose()
        localAudioSource = null
        localAudioTrack = null
        localVideoSource = null
        localVideoTrack = null
        remoteAudioTrack = null
        remoteVideoTrack = null
        cameraAvailabilityCallback = null
    }

    fun onAddStream(stream: MediaStream) {
        sessionScope?.launch(dispatcher) {
            
            if (stream.audioTracks.size > 1 || stream.videoTracks.size > 1) {
                Timber.tag(loggerTag.value).e("StreamObserver weird looking stream: $stream")
                
                endCall(EndCallReason.UNKWOWN_ERROR)
                return@launch
            }
            if (stream.audioTracks.size == 1) {
                val remoteAudioTrack = stream.audioTracks.first()
                remoteAudioTrack.setEnabled(true)
                this@WebRtcCall.remoteAudioTrack = remoteAudioTrack
            }
            if (stream.videoTracks.size == 1) {
                val remoteVideoTrack = stream.videoTracks.first()
                remoteVideoTrack.setEnabled(true)
                this@WebRtcCall.remoteVideoTrack = remoteVideoTrack
                
                remoteSurfaceRenderers.forEach { it.get()?.let { remoteVideoTrack.addSink(it) } }
            }
        }
    }

    fun onRemoveStream() {
        sessionScope?.launch(dispatcher) {
            remoteSurfaceRenderers
                    .mapNotNull { it.get() }
                    .forEach { remoteVideoTrack?.removeSink(it) }
            remoteVideoTrack = null
            remoteAudioTrack = null
        }
    }

    var mIsReject: Boolean? = null
    fun endCall(reason: EndCallReason = EndCallReason.USER_HANGUP, sendSignaling: Boolean = true) {
        sessionScope?.launch(dispatcher) {
            if (mxCall.state is CallState.Ended) {
                return@launch
            }
            var reject = mxCall.state is CallState.LocalRinging
            mIsReject = reject
            terminate(reason, reject)
            if (sendSignaling) {
                if (reject) {
                    mxCall.reject()
                } else {
                    mxCall.hangUp(reason)
                }
            }
        }
    }

    private suspend fun terminate(reason: EndCallReason? = null, rejected: Boolean = false) = withContext(dispatcher) {
        
        
        val opponentUserId = mxCall.opponentUserId
        
        val bestName = context.singletonEntryPoint().activeSessionHolder().getActiveSession().getUser(opponentUserId)?.displayName ?: opponentUserId
        
        val currentTimeMillis = System.currentTimeMillis()
        
        val formattedDuration = formatDuration(Duration.ofMillis(durationMillis().toLong()))

        
        val isOuting = if (mxCall.isOutgoing) 1 else 2

        
        val phone = if (mxCall.isOutgoing) out_phone else in_phone

        
        
        val connected =
                if (null == mIsReject) {
                    
                    2
                } else if (mIsReject as Boolean) {
                    
                    2
                } else {
                    1
                }
        val log = ChatPhoneLog(bestName, currentTimeMillis, formattedDuration, opponentUserId, phone, isOuting, connected)

        insetChatLog(context, log)
        localVideoTrack?.setEnabled(false)
        localVideoTrack?.setEnabled(false)
        cameraAvailabilityCallback?.let { cameraAvailabilityCallback ->
            val cameraManager = context.getSystemService<CameraManager>()!!
            cameraManager.unregisterAvailabilityCallback(cameraAvailabilityCallback)
        }
        inviteTimeout?.cancel()
        inviteTimeout = null
        mxCall.state = CallState.Ended(reason ?: EndCallReason.USER_HANGUP)
        release()
        onCallEnded(callId, reason ?: EndCallReason.USER_HANGUP, rejected)
    }

    

    fun onCallIceCandidateReceived(iceCandidatesContent: CallCandidatesContent) {
        sessionScope?.launch(dispatcher) {
            iceCandidatesContent.candidates.forEach {
                if (it.sdpMid.isNullOrEmpty() || it.candidate.isNullOrEmpty()) {
                    return@forEach
                }
                Timber.tag(loggerTag.value).v("onCallIceCandidateReceived for call ${mxCall.callId} sdp: ${it.candidate}")
                val iceCandidate = IceCandidate(it.sdpMid, it.sdpMLineIndex, it.candidate)
                remoteCandidateSource.emit(iceCandidate)
            }
        }
    }

    fun onCallAnswerReceived(callAnswerContent: CallAnswerContent) {
        inviteTimeout?.cancel()
        inviteTimeout = null
        sessionScope?.launch(dispatcher) {
            Timber.tag(loggerTag.value).v("onCallAnswerReceived ${callAnswerContent.callId}")
            val sdp = SessionDescription(SessionDescription.Type.ANSWER, callAnswerContent.answer.sdp)
            try {
                peerConnection?.awaitSetRemoteDescription(sdp)
            } catch (failure: Throwable) {
                endCall(EndCallReason.UNKWOWN_ERROR)
                return@launch
            }
            if (mxCall.opponentPartyId?.hasValue().orFalse()) {
                mxCall.selectAnswer()
            }
        }
    }

    fun onCallNegotiateReceived(callNegotiateContent: CallNegotiateContent) {

        Timber.tag(loggerTag.value).i("onCallNegotiateReceived  onCallNegotiateReceived  onCallNegotiateReceived")
        sessionScope?.launch(dispatcher) {
            val description = callNegotiateContent.description
            val type = description?.type
            val sdpText = description?.sdp
            if (type == null || sdpText == null) {
                Timber.tag(loggerTag.value).i("Ignoring invalid m.call.negotiate event")
                return@launch
            }
            val peerConnection = peerConnection ?: return@launch
            
            
            
            val polite = !mxCall.isOutgoing
            
            
            val offerCollision = description.type == SdpType.OFFER &&
                    (makingOffer || peerConnection.signalingState() != PeerConnection.SignalingState.STABLE)

            ignoreOffer = !polite && offerCollision
            if (ignoreOffer) {
                Timber.tag(loggerTag.value).i("Ignoring colliding negotiate event because we're impolite")
                return@launch
            }
            val prevOnHold = computeIsLocalOnHold()
            try {
                val sdp = SessionDescription(type.asWebRTC(), sdpText)
                peerConnection.awaitSetRemoteDescription(sdp)
                if (type == SdpType.OFFER) {
                    createAnswer()?.also {
                        mxCall.negotiate(it.description, SdpType.ANSWER)
                    }
                }
            } catch (failure: Throwable) {
                Timber.tag(loggerTag.value).e(failure, "Failed to complete negotiation")
            }
            val nowOnHold = computeIsLocalOnHold()
            wasLocalOnHold = nowOnHold
            if (prevOnHold != nowOnHold) {
                isLocalOnHold = nowOnHold
                if (nowOnHold) {
                    timer.pause()
                } else {
                    timer.resume()
                }
                listeners.forEach {
                    tryOrNull { it.onHoldUnhold() }
                }
            }
        }
    }

    fun onCallHangupReceived(callHangupContent: CallHangupContent) {
        sessionScope?.launch(dispatcher) {
            terminate(callHangupContent.reason)
        }
    }

    fun onCallRejectReceived(callRejectContent: CallRejectContent) {
        sessionScope?.launch(dispatcher) {
            terminate(callRejectContent.reason, true)
        }
    }

    fun onCallSelectedAnswerReceived(callSelectAnswerContent: CallSelectAnswerContent) {
        sessionScope?.launch(dispatcher) {
            val selectedPartyId = callSelectAnswerContent.selectedPartyId
            if (selectedPartyId != mxCall.ourPartyId) {
                Timber.i("Got select_answer for party ID $selectedPartyId: we are party ID ${mxCall.ourPartyId}.")
                
                terminate()
            }
        }
    }

    fun onCallAssertedIdentityReceived(callAssertedIdentityContent: CallAssertedIdentityContent) {
        sessionScope?.launch(dispatcher) {
            val session = sessionProvider.get() ?: return@launch
            val newAssertedIdentity = callAssertedIdentityContent.assertedIdentity ?: return@launch
            if (newAssertedIdentity.id == null && newAssertedIdentity.displayName == null) {
                Timber.tag(loggerTag.value).v("Asserted identity received with no relevant information, skip")
                return@launch
            }
            remoteAssertedIdentity = newAssertedIdentity
            if (newAssertedIdentity.id != null) {
                val nativeUserId = session.sipNativeLookup(newAssertedIdentity.id!!).firstOrNull()?.userId
                if (nativeUserId != null) {
                    val resolvedUser = tryOrNull {
                        session.resolveUser(nativeUserId)
                    }
                    if (resolvedUser != null) {
                        remoteAssertedIdentity = newAssertedIdentity.copy(
                                id = nativeUserId,
                                avatarUrl = resolvedUser.avatarUrl,
                                displayName = resolvedUser.displayName
                        )
                    } else {
                        remoteAssertedIdentity = newAssertedIdentity.copy(id = nativeUserId)
                    }
                }
            }
            listeners.forEach {
                tryOrNull { it.assertedIdentityChanged() }
            }
        }
    }

    
    private var connectionSelfTimeoutJob: Job? = null
    override fun onStateUpdate(call: MxCall) {
        val state = call.state
        if (state is CallState.Answering) {
            if (null == connectionSelfTimeoutJob) {
                connectionSelfTimeoutJob = sessionScope?.launch {
                    delay(30_000)
                    
                    endCall()
                }
            }
        }
        if (state is CallState.Connected && state.iceConnectionState == MxPeerConnectionState.CONNECTED) {
            timer.resume()
            connectionSelfTimeoutJob?.cancel()
            connectionSelfTimeoutJob = null
        } else {
            timer.pause()
        }
        listeners.forEach {
            tryOrNull { it.onStateUpdate(call) }
        }
    }
}

private fun MutableList<WeakReference<SurfaceViewRenderer>>.addIfNeeded(renderer: SurfaceViewRenderer?) {
    if (renderer == null) return
    val exists = any {
        it.get() == renderer
    }
    if (!exists) {
        add(WeakReference(renderer))
    }
}

private fun MutableList<WeakReference<SurfaceViewRenderer>>.removeIfNeeded(renderer: SurfaceViewRenderer?) {
    if (renderer == null) return
    removeAll {
        it.get() == renderer
    }
}
