

package org.matrix.android.sdk.api.session.crypto.verification

interface QrCodeVerificationTransaction : VerificationTransaction {

    
    val qrCodeText: String?

    
    fun userHasScannedOtherQrCode(otherQrCodeText: String)

    
    fun otherUserScannedMyQrCode()

    
    fun otherUserDidNotScannedMyQrCode()
}
