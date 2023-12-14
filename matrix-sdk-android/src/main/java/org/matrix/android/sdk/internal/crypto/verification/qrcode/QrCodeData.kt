

package org.matrix.android.sdk.internal.crypto.verification.qrcode


internal sealed class QrCodeData(
        
        open val transactionId: String,
        
        val firstKey: String,
        
        val secondKey: String,
        
        open val sharedSecret: String
) {
    
    data class VerifyingAnotherUser(
            override val transactionId: String,
            
            val userMasterCrossSigningPublicKey: String,
            
            val otherUserMasterCrossSigningPublicKey: String,
            override val sharedSecret: String
    ) : QrCodeData(
            transactionId,
            userMasterCrossSigningPublicKey,
            otherUserMasterCrossSigningPublicKey,
            sharedSecret)

    
    data class SelfVerifyingMasterKeyTrusted(
            override val transactionId: String,
            
            val userMasterCrossSigningPublicKey: String,
            
            val otherDeviceKey: String,
            override val sharedSecret: String
    ) : QrCodeData(
            transactionId,
            userMasterCrossSigningPublicKey,
            otherDeviceKey,
            sharedSecret)

    
    data class SelfVerifyingMasterKeyNotTrusted(
            override val transactionId: String,
            
            val deviceKey: String,
            
            val userMasterCrossSigningPublicKey: String,
            override val sharedSecret: String
    ) : QrCodeData(
            transactionId,
            deviceKey,
            userMasterCrossSigningPublicKey,
            sharedSecret)
}
