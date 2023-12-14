

package org.matrix.android.sdk.api.session.crypto.verification

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.LocalEcho


interface VerificationService {

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    
    fun markedLocallyAsManuallyVerified(userId: String, deviceID: String)

    fun getExistingTransaction(otherUserId: String, tid: String): VerificationTransaction?

    fun getExistingVerificationRequests(otherUserId: String): List<PendingVerificationRequest>

    fun getExistingVerificationRequest(otherUserId: String, tid: String?): PendingVerificationRequest?

    fun getExistingVerificationRequestInRoom(roomId: String, tid: String?): PendingVerificationRequest?

    fun beginKeyVerification(method: VerificationMethod,
                             otherUserId: String,
                             otherDeviceId: String,
                             transactionId: String?): String?

    
    fun requestKeyVerificationInDMs(methods: List<VerificationMethod>,
                                    otherUserId: String,
                                    roomId: String,
                                    localId: String? = LocalEcho.createLocalEchoId()): PendingVerificationRequest

    fun cancelVerificationRequest(request: PendingVerificationRequest)

    
    fun requestKeyVerification(methods: List<VerificationMethod>,
                               otherUserId: String,
                               otherDevices: List<String>?): PendingVerificationRequest

    fun declineVerificationRequestInDMs(otherUserId: String,
                                        transactionId: String,
                                        roomId: String)

    
    
    fun beginKeyVerificationInDMs(method: VerificationMethod,
                                  transactionId: String,
                                  roomId: String,
                                  otherUserId: String,
                                  otherDeviceId: String): String

    
    fun readyPendingVerificationInDMs(methods: List<VerificationMethod>,
                                      otherUserId: String,
                                      roomId: String,
                                      transactionId: String): Boolean

    
    fun readyPendingVerification(methods: List<VerificationMethod>,
                                 otherUserId: String,
                                 transactionId: String): Boolean

    interface Listener {
        
        fun verificationRequestCreated(pr: PendingVerificationRequest) {}

        
        fun verificationRequestUpdated(pr: PendingVerificationRequest) {}

        
        fun transactionCreated(tx: VerificationTransaction) {}

        
        fun transactionUpdated(tx: VerificationTransaction) {}

        
        fun markedAsManuallyVerified(userId: String, deviceId: String) {}
    }

    companion object {

        private const val TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000
        private const val FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000

        fun isValidRequest(age: Long?): Boolean {
            if (age == null) return false
            val now = System.currentTimeMillis()
            val tooInThePast = now - TEN_MINUTES_IN_MILLIS
            val tooInTheFuture = now + FIVE_MINUTES_IN_MILLIS
            return age in tooInThePast..tooInTheFuture
        }
    }

    fun onPotentiallyInterestingEventRoomFailToDecrypt(event: Event)
}
