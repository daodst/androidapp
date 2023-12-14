

package org.matrix.android.sdk.internal.crypto

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.crypto.MXCryptoConfig
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.extractCurveKeyFromRecoveryKey
import org.matrix.android.sdk.api.session.crypto.keyshare.GossipingRequestListener
import org.matrix.android.sdk.api.session.crypto.model.GossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.GossipingToDeviceObject
import org.matrix.android.sdk.api.session.crypto.model.IncomingRequestCancellation
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.IncomingSecretShareRequest
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyRequestBody
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.util.toBase64NoPadding
import org.matrix.android.sdk.internal.crypto.algorithms.IMXGroupEncryption
import org.matrix.android.sdk.internal.crypto.model.rest.GossipingDefaultContent
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.createUniqueTxnId
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.worker.WorkerParamsFactory
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

@SessionScope
internal class IncomingGossipingRequestManager @Inject constructor(
        @SessionId private val sessionId: String,
        private val credentials: Credentials,
        private val cryptoStore: IMXCryptoStore,
        private val cryptoConfig: MXCryptoConfig,
        private val gossipingWorkManager: GossipingWorkManager,
        private val roomEncryptorsStore: RoomEncryptorsStore,
        private val roomDecryptorProvider: RoomDecryptorProvider,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val cryptoCoroutineScope: CoroutineScope) {

    private val executor = Executors.newSingleThreadExecutor()

    
    
    private val receivedGossipingRequests = ArrayList<IncomingShareRequestCommon>()
    private val receivedRequestCancellations = ArrayList<IncomingRequestCancellation>()

    
    private val gossipingRequestListeners: MutableSet<GossipingRequestListener> = HashSet()

    init {
        receivedGossipingRequests.addAll(cryptoStore.getPendingIncomingGossipingRequests())
    }

    fun close() {
        executor.shutdownNow()
    }

    
    private val recentlyVerifiedDevices = HashMap<String, Long>()

    
    fun onVerificationCompleteForDevice(deviceId: String) {
        
        synchronized(recentlyVerifiedDevices) {
            recentlyVerifiedDevices[deviceId] = System.currentTimeMillis()
        }
    }

    private fun hasBeenVerifiedLessThanFiveMinutesFromNow(deviceId: String): Boolean {
        val verifTimestamp: Long?
        synchronized(recentlyVerifiedDevices) {
            verifTimestamp = recentlyVerifiedDevices[deviceId]
        }
        if (verifTimestamp == null) return false

        val age = System.currentTimeMillis() - verifTimestamp

        return age < FIVE_MINUTES_IN_MILLIS
    }

    
    fun onGossipingRequestEvent(event: Event) {
        val roomKeyShare = event.getClearContent().toModel<GossipingDefaultContent>()
        Timber.i("## CRYPTO | GOSSIP onGossipingRequestEvent received type ${event.type} from user:${event.senderId}, content:$roomKeyShare")
        
        when (roomKeyShare?.action) {
            GossipingToDeviceObject.ACTION_SHARE_REQUEST      -> {
                if (event.getClearType() == EventType.REQUEST_SECRET) {
                    IncomingSecretShareRequest.fromEvent(event)?.let {
                        if (event.senderId == credentials.userId && it.deviceId == credentials.deviceId) {
                            
                            Timber.v("## GOSSIP onGossipingRequestEvent type ${event.type} ignore remote echo")
                        } else {
                            receivedGossipingRequests.add(it)
                        }
                    }
                } else if (event.getClearType() == EventType.ROOM_KEY_REQUEST) {
                    IncomingRoomKeyRequest.fromEvent(event)?.let {
                        if (event.senderId == credentials.userId && it.deviceId == credentials.deviceId) {
                            
                            Timber.v("## GOSSIP onGossipingRequestEvent type ${event.type} ignore remote echo")
                        } else {
                            receivedGossipingRequests.add(it)
                        }
                    }
                }
            }
            GossipingToDeviceObject.ACTION_SHARE_CANCELLATION -> {
                IncomingRequestCancellation.fromEvent(event)?.let {
                    receivedRequestCancellations.add(it)
                }
            }
            else                                              -> {
                Timber.e("## GOSSIP onGossipingRequestEvent() : unsupported action ${roomKeyShare?.action}")
            }
        }
    }

    
    fun processReceivedGossipingRequests() {
        val roomKeyRequestsToProcess = receivedGossipingRequests.toList()
        receivedGossipingRequests.clear()

        Timber.v("## CRYPTO | GOSSIP processReceivedGossipingRequests() : ${roomKeyRequestsToProcess.size} request to process")

        var receivedRequestCancellations: List<IncomingRequestCancellation>? = null

        synchronized(this.receivedRequestCancellations) {
            if (this.receivedRequestCancellations.isNotEmpty()) {
                receivedRequestCancellations = this.receivedRequestCancellations.toList()
                this.receivedRequestCancellations.clear()
            }
        }

        executor.execute {
            cryptoStore.storeIncomingGossipingRequests(roomKeyRequestsToProcess)
            for (request in roomKeyRequestsToProcess) {
                if (request is IncomingRoomKeyRequest) {
                    processIncomingRoomKeyRequest(request)
                } else if (request is IncomingSecretShareRequest) {
                    processIncomingSecretShareRequest(request)
                }
            }

            receivedRequestCancellations?.forEach { request ->
                Timber.v("## CRYPTO | GOSSIP processReceivedGossipingRequests() : m.room_key_request cancellation $request")
                
                
                
                if (request.userId == credentials.userId && request.deviceId == credentials.deviceId) {
                    
                    return@forEach
                }
                val matchingIncoming = cryptoStore.getIncomingRoomKeyRequest(request.userId ?: "", request.deviceId ?: "", request.requestId ?: "")
                if (matchingIncoming == null) {
                    
                    return@forEach
                } else {
                    
                    if (matchingIncoming.state != GossipingRequestState.ACCEPTED) {
                        onRoomKeyRequestCancellation(request)
                        cryptoStore.updateGossipingRequestState(request, GossipingRequestState.CANCELLED_BY_REQUESTER)
                    }
                }
            }
        }
    }

    private fun processIncomingRoomKeyRequest(request: IncomingRoomKeyRequest) {
        val userId = request.userId ?: return
        val deviceId = request.deviceId ?: return
        val body = request.requestBody ?: return
        val roomId = body.roomId ?: return
        val alg = body.algorithm ?: return

        Timber.v("## CRYPTO | GOSSIP processIncomingRoomKeyRequest from $userId:$deviceId for $roomId / ${body.sessionId} id ${request.requestId}")
        if (credentials.userId != userId) {
            handleKeyRequestFromOtherUser(body, request, alg, roomId, userId, deviceId)
            return
        }
        
        
        
        val decryptor = roomDecryptorProvider.getRoomDecryptor(roomId, alg)
        if (null == decryptor) {
            Timber.w("## CRYPTO | GOSSIP processReceivedGossipingRequests() : room key request for unknown $alg in room $roomId")
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            return
        }
        if (!decryptor.hasKeysForKeyRequest(request)) {
            Timber.w("## CRYPTO | GOSSIP processReceivedGossipingRequests() : room key request for unknown session ${body.sessionId!!}")
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            return
        }

        if (credentials.deviceId == deviceId && credentials.userId == userId) {
            Timber.v("## CRYPTO | GOSSIP processReceivedGossipingRequests() : oneself device - ignored")
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            return
        }
        request.share = Runnable {
            decryptor.shareKeysWithDevice(request)
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.ACCEPTED)
        }
        request.ignore = Runnable {
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
        }
        
        val device = cryptoStore.getUserDevice(userId, deviceId)
        if (device != null) {
            if (device.isVerified) {
                Timber.v("## CRYPTO | GOSSIP processReceivedGossipingRequests() : device is already verified: sharing keys")
                request.share?.run()
                return
            }

            if (device.isBlocked) {
                Timber.v("## CRYPTO | GOSSIP processReceivedGossipingRequests() : device is blocked -> ignored")
                cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
                return
            }
        }

        
        if (cryptoConfig.discardRoomKeyRequestsFromUntrustedDevices) {
            Timber.v("## CRYPTO | processReceivedGossipingRequests() : discardRoomKeyRequestsFromUntrustedDevices")
            
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            return
        }

        
        onRoomKeyRequest(request)
    }

    private fun handleKeyRequestFromOtherUser(body: RoomKeyRequestBody,
                                              request: IncomingRoomKeyRequest,
                                              alg: String,
                                              roomId: String,
                                              userId: String,
                                              deviceId: String) {
        Timber.w("## CRYPTO | GOSSIP processReceivedGossipingRequests() : room key request from other user")
        val senderKey = body.senderKey ?: return Unit
                .also { Timber.w("missing senderKey") }
                .also { cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED) }
        val sessionId = body.sessionId ?: return Unit
                .also { Timber.w("missing sessionId") }
                .also { cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED) }

        if (alg != MXCRYPTO_ALGORITHM_MEGOLM) {
            return Unit
                    .also { Timber.w("Only megolm is accepted here") }
                    .also { cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED) }
        }

        val roomEncryptor = roomEncryptorsStore.get(roomId) ?: return Unit
                .also { Timber.w("no room Encryptor") }
                .also { cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED) }

        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            if (roomEncryptor is IMXGroupEncryption) {
                val isSuccess = roomEncryptor.reshareKey(sessionId, userId, deviceId, senderKey)

                if (isSuccess) {
                    cryptoStore.updateGossipingRequestState(request, GossipingRequestState.ACCEPTED)
                } else {
                    cryptoStore.updateGossipingRequestState(request, GossipingRequestState.UNABLE_TO_PROCESS)
                }
            } else {
                Timber.e("## CRYPTO | handleKeyRequestFromOtherUser() from:$userId: Unable to handle IMXGroupEncryption.reshareKey for $alg")
            }
        }
        cryptoStore.updateGossipingRequestState(request, GossipingRequestState.RE_REQUESTED)
    }

    private fun processIncomingSecretShareRequest(request: IncomingSecretShareRequest) {
        val secretName = request.secretName ?: return Unit.also {
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            Timber.v("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : Missing secret name")
        }

        val userId = request.userId
        if (userId == null || credentials.userId != userId) {
            Timber.e("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : Ignoring secret share request from other users")
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            return
        }

        val deviceId = request.deviceId
                ?: return Unit.also {
                    Timber.e("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : Malformed request, no ")
                    cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
                }

        val device = cryptoStore.getUserDevice(userId, deviceId)
                ?: return Unit.also {
                    Timber.e("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : Received secret share request from unknown device ${request.deviceId}")
                    cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
                }

        if (!device.isVerified || device.isBlocked) {
            Timber.v("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : Ignoring secret share request from untrusted/blocked session $device")
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            return
        }

        val isDeviceLocallyVerified = cryptoStore.getUserDevice(userId, deviceId)?.trustLevel?.isLocallyVerified()

        when (secretName) {
            MASTER_KEY_SSSS_NAME       -> cryptoStore.getCrossSigningPrivateKeys()?.master
            SELF_SIGNING_KEY_SSSS_NAME -> cryptoStore.getCrossSigningPrivateKeys()?.selfSigned
            USER_SIGNING_KEY_SSSS_NAME -> cryptoStore.getCrossSigningPrivateKeys()?.user
            KEYBACKUP_SECRET_SSSS_NAME -> cryptoStore.getKeyBackupRecoveryKeyInfo()?.recoveryKey
                    ?.let {
                        extractCurveKeyFromRecoveryKey(it)?.toBase64NoPadding()
                    }
            else                       -> null
        }?.let { secretValue ->
            Timber.i("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : Sharing secret $secretName with $device locally trusted")
            if (isDeviceLocallyVerified == true && hasBeenVerifiedLessThanFiveMinutesFromNow(deviceId)) {
                val params = SendGossipWorker.Params(
                        sessionId = sessionId,
                        secretValue = secretValue,
                        requestUserId = request.userId,
                        requestDeviceId = request.deviceId,
                        requestId = request.requestId,
                        txnId = createUniqueTxnId()
                )

                cryptoStore.updateGossipingRequestState(request, GossipingRequestState.ACCEPTING)
                val workRequest = gossipingWorkManager.createWork<SendGossipWorker>(WorkerParamsFactory.toData(params), true)
                gossipingWorkManager.postWork(workRequest)
            } else {
                Timber.v("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : Can't share secret $secretName with $device, verification too old")
                cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
            }
            return
        }

        Timber.v("## CRYPTO | GOSSIP processIncomingSecretShareRequest() : $secretName unknown at SDK level, asking to app layer")

        request.ignore = Runnable {
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.REJECTED)
        }

        request.share = { secretValue ->
            val params = SendGossipWorker.Params(
                    sessionId = userId,
                    secretValue = secretValue,
                    requestUserId = request.userId,
                    requestDeviceId = request.deviceId,
                    requestId = request.requestId,
                    txnId = createUniqueTxnId()
            )

            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.ACCEPTING)
            val workRequest = gossipingWorkManager.createWork<SendGossipWorker>(WorkerParamsFactory.toData(params), true)
            gossipingWorkManager.postWork(workRequest)
            cryptoStore.updateGossipingRequestState(request, GossipingRequestState.ACCEPTED)
        }

        onShareRequest(request)
    }

    
    private fun onRoomKeyRequest(request: IncomingRoomKeyRequest) {
        synchronized(gossipingRequestListeners) {
            for (listener in gossipingRequestListeners) {
                try {
                    listener.onRoomKeyRequest(request)
                } catch (e: Exception) {
                    Timber.e(e, "## CRYPTO | onRoomKeyRequest() failed")
                }
            }
        }
    }

    
    private fun onShareRequest(request: IncomingSecretShareRequest) {
        synchronized(gossipingRequestListeners) {
            for (listener in gossipingRequestListeners) {
                try {
                    if (listener.onSecretShareRequest(request)) {
                        return
                    }
                } catch (e: Exception) {
                    Timber.e(e, "## CRYPTO | GOSSIP onRoomKeyRequest() failed")
                }
            }
        }
        
        request.ignore?.run()
    }

    
    private fun onRoomKeyRequestCancellation(request: IncomingRequestCancellation) {
        synchronized(gossipingRequestListeners) {
            for (listener in gossipingRequestListeners) {
                try {
                    listener.onRoomKeyRequestCancellation(request)
                } catch (e: Exception) {
                    Timber.e(e, "## CRYPTO | GOSSIP onRoomKeyRequestCancellation() failed")
                }
            }
        }
    }

    fun addRoomKeysRequestListener(listener: GossipingRequestListener) {
        synchronized(gossipingRequestListeners) {
            gossipingRequestListeners.add(listener)
        }
    }

    fun removeRoomKeysRequestListener(listener: GossipingRequestListener) {
        synchronized(gossipingRequestListeners) {
            gossipingRequestListeners.remove(listener)
        }
    }

    companion object {
        private const val FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000
    }
}
