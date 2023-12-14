

package org.matrix.android.sdk.internal.crypto

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.session.crypto.NewSessionListener
import org.matrix.android.sdk.internal.crypto.algorithms.IMXDecrypting
import org.matrix.android.sdk.internal.crypto.algorithms.megolm.MXMegolmDecryptionFactory
import org.matrix.android.sdk.internal.crypto.algorithms.olm.MXOlmDecryptionFactory
import org.matrix.android.sdk.internal.session.SessionScope
import timber.log.Timber
import javax.inject.Inject

@SessionScope
internal class RoomDecryptorProvider @Inject constructor(
        private val olmDecryptionFactory: MXOlmDecryptionFactory,
        private val megolmDecryptionFactory: MXMegolmDecryptionFactory
) {

    
    private val roomDecryptors: MutableMap<String , MutableMap<String , IMXDecrypting>> = HashMap()

    private val newSessionListeners = ArrayList<NewSessionListener>()

    fun addNewSessionListener(listener: NewSessionListener) {
        if (!newSessionListeners.contains(listener)) newSessionListeners.add(listener)
    }

    fun removeSessionListener(listener: NewSessionListener) {
        newSessionListeners.remove(listener)
    }

    
    fun getOrCreateRoomDecryptor(roomId: String?, algorithm: String?): IMXDecrypting? {
        
        if (algorithm.isNullOrEmpty()) {
            Timber.e("## getRoomDecryptor() : null algorithm")
            return null
        }
        if (roomId != null && roomId.isNotEmpty()) {
            synchronized(roomDecryptors) {
                val decryptors = roomDecryptors.getOrPut(roomId) { mutableMapOf() }
                val alg = decryptors[algorithm]
                if (alg != null) {
                    return alg
                }
            }
        }
        val decryptingClass = MXCryptoAlgorithms.hasDecryptorClassForAlgorithm(algorithm)
        if (decryptingClass) {
            val alg = when (algorithm) {
                MXCRYPTO_ALGORITHM_MEGOLM -> megolmDecryptionFactory.create().apply {
                    this.newSessionListener = object : NewSessionListener {
                        override fun onNewSession(roomId: String?, senderKey: String, sessionId: String) {
                            
                            newSessionListeners.toList().forEach {
                                try {
                                    it.onNewSession(roomId, senderKey, sessionId)
                                } catch (e: Throwable) {
                                }
                            }
                        }
                    }
                }
                else                      -> olmDecryptionFactory.create()
            }
            if (!roomId.isNullOrEmpty()) {
                synchronized(roomDecryptors) {
                    roomDecryptors[roomId]?.put(algorithm, alg)
                }
            }
            return alg
        }
        return null
    }

    fun getRoomDecryptor(roomId: String?, algorithm: String?): IMXDecrypting? {
        if (roomId == null || algorithm == null) {
            return null
        }
        return roomDecryptors[roomId]?.get(algorithm)
    }
}
