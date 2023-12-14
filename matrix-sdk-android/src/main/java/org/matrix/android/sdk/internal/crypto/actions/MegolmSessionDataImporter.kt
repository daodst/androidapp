

package org.matrix.android.sdk.internal.crypto.actions

import androidx.annotation.WorkerThread
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.crypto.model.ImportRoomKeysResult
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyRequestBody
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.MegolmSessionData
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequestManager
import org.matrix.android.sdk.internal.crypto.RoomDecryptorProvider
import org.matrix.android.sdk.internal.crypto.algorithms.megolm.MXMegolmDecryption
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import timber.log.Timber
import javax.inject.Inject

internal class MegolmSessionDataImporter @Inject constructor(private val olmDevice: MXOlmDevice,
                                                             private val roomDecryptorProvider: RoomDecryptorProvider,
                                                             private val outgoingGossipingRequestManager: OutgoingGossipingRequestManager,
                                                             private val cryptoStore: IMXCryptoStore) {

    
    @WorkerThread
    fun handle(megolmSessionsData: List<MegolmSessionData>,
               fromBackup: Boolean,
               progressListener: ProgressListener?): ImportRoomKeysResult {
        val t0 = System.currentTimeMillis()

        val totalNumbersOfKeys = megolmSessionsData.size
        var lastProgress = 0
        var totalNumbersOfImportedKeys = 0

        progressListener?.onProgress(0, 100)
        val olmInboundGroupSessionWrappers = olmDevice.importInboundGroupSessions(megolmSessionsData)

        megolmSessionsData.forEachIndexed { cpt, megolmSessionData ->
            val decrypting = roomDecryptorProvider.getOrCreateRoomDecryptor(megolmSessionData.roomId, megolmSessionData.algorithm)

            if (null != decrypting) {
                try {
                    val sessionId = megolmSessionData.sessionId
                    Timber.v("## importRoomKeys retrieve senderKey " + megolmSessionData.senderKey + " sessionId " + sessionId)

                    totalNumbersOfImportedKeys++

                    
                    val roomKeyRequestBody = RoomKeyRequestBody(
                            algorithm = megolmSessionData.algorithm,
                            roomId = megolmSessionData.roomId,
                            senderKey = megolmSessionData.senderKey,
                            sessionId = megolmSessionData.sessionId
                    )

                    outgoingGossipingRequestManager.cancelRoomKeyRequest(roomKeyRequestBody)

                    
                    when (decrypting) {
                        is MXMegolmDecryption -> {
                            decrypting.onNewSession(megolmSessionData.roomId, megolmSessionData.senderKey!!, sessionId!!)
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "## importRoomKeys() : onNewSession failed")
                }
            }

            if (progressListener != null) {
                val progress = 100 * (cpt + 1) / totalNumbersOfKeys

                if (lastProgress != progress) {
                    lastProgress = progress

                    progressListener.onProgress(progress, 100)
                }
            }
        }

        
        if (fromBackup) {
            cryptoStore.markBackupDoneForInboundGroupSessions(olmInboundGroupSessionWrappers)
        }

        val t1 = System.currentTimeMillis()

        Timber.v("## importMegolmSessionsData : sessions import " + (t1 - t0) + " ms (" + megolmSessionsData.size + " sessions)")

        return ImportRoomKeysResult(totalNumbersOfKeys, totalNumbersOfImportedKeys)
    }
}
