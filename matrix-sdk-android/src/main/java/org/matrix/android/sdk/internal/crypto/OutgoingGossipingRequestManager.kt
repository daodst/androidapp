

package org.matrix.android.sdk.internal.crypto

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.session.crypto.model.OutgoingGossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.OutgoingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyRequestBody
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.createUniqueTxnId
import org.matrix.android.sdk.internal.crypto.util.RequestIdHelper
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.worker.WorkerParamsFactory
import timber.log.Timber
import javax.inject.Inject

@SessionScope
internal class OutgoingGossipingRequestManager @Inject constructor(
        @SessionId private val sessionId: String,
        private val cryptoStore: IMXCryptoStore,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val cryptoCoroutineScope: CoroutineScope,
        private val gossipingWorkManager: GossipingWorkManager) {

    
    fun sendRoomKeyRequest(requestBody: RoomKeyRequestBody, recipients: Map<String, List<String>>) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            cryptoStore.getOrAddOutgoingRoomKeyRequest(requestBody, recipients)?.let {
                
                if (it.state == OutgoingGossipingRequestState.SENDING || it.state == OutgoingGossipingRequestState.SENT) {
                    Timber.v("## CRYPTO - GOSSIP sendOutgoingRoomKeyRequest() : we already request for that session: $it")
                    return@launch
                }

                sendOutgoingGossipingRequest(it)
            }
        }
    }

    fun sendSecretShareRequest(secretName: String, recipients: Map<String, List<String>>) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            
            
            delay(1500)
            cryptoStore.getOrAddOutgoingSecretShareRequest(secretName, recipients)?.let {
                
                if (it.state == OutgoingGossipingRequestState.SENDING
                
                ) {
                    Timber.v("## CRYPTO - GOSSIP sendSecretShareRequest() : we are already sending for that session: $it")
                    return@launch
                }

                sendOutgoingGossipingRequest(it)
            }
        }
    }

    
    fun cancelRoomKeyRequest(requestBody: RoomKeyRequestBody) {
        cryptoCoroutineScope.launch(coroutineDispatchers.computation) {
            cancelRoomKeyRequest(requestBody, false)
        }
    }

    
    fun resendRoomKeyRequest(requestBody: RoomKeyRequestBody) {
        cryptoCoroutineScope.launch(coroutineDispatchers.computation) {
            cancelRoomKeyRequest(requestBody, true)
        }
    }

    
    private fun cancelRoomKeyRequest(requestBody: RoomKeyRequestBody, andResend: Boolean) {
        val req = cryptoStore.getOutgoingRoomKeyRequest(requestBody) 
                ?: return Unit.also {
                    Timber.v("## CRYPTO - GOSSIP cancelRoomKeyRequest() Unknown request $requestBody")
                }

        sendOutgoingRoomKeyRequestCancellation(req, andResend)
    }

    
    private fun sendOutgoingGossipingRequest(request: OutgoingGossipingRequest) {
        Timber.v("## CRYPTO - GOSSIP sendOutgoingGossipingRequest() : Requesting keys $request")

        val params = SendGossipRequestWorker.Params(
                sessionId = sessionId,
                keyShareRequest = request as? OutgoingRoomKeyRequest,
                secretShareRequest = request as? OutgoingSecretRequest,
                txnId = createUniqueTxnId()
        )
        cryptoStore.updateOutgoingGossipingRequestState(request.requestId, OutgoingGossipingRequestState.SENDING)
        val workRequest = gossipingWorkManager.createWork<SendGossipRequestWorker>(WorkerParamsFactory.toData(params), true)
        gossipingWorkManager.postWork(workRequest)
    }

    
    private fun sendOutgoingRoomKeyRequestCancellation(request: OutgoingRoomKeyRequest, resend: Boolean = false) {
        Timber.v("## CRYPTO - sendOutgoingRoomKeyRequestCancellation $request")
        val params = CancelGossipRequestWorker.Params.fromRequest(sessionId, request)
        cryptoStore.updateOutgoingGossipingRequestState(request.requestId, OutgoingGossipingRequestState.CANCELLING)

        val workRequest = gossipingWorkManager.createWork<CancelGossipRequestWorker>(WorkerParamsFactory.toData(params), true)
        gossipingWorkManager.postWork(workRequest)

        if (resend) {
            val reSendParams = SendGossipRequestWorker.Params(
                    sessionId = sessionId,
                    keyShareRequest = request.copy(requestId = RequestIdHelper.createUniqueRequestId()),
                    txnId = createUniqueTxnId()
            )
            val reSendWorkRequest = gossipingWorkManager.createWork<SendGossipRequestWorker>(WorkerParamsFactory.toData(reSendParams), true)
            gossipingWorkManager.postWork(reSendWorkRequest)
        }
    }
}
