

package im.vector.app.features.crypto.verification

import im.vector.app.core.hardware.HardwareInfo
import org.matrix.android.sdk.api.session.crypto.verification.VerificationMethod
import timber.log.Timber
import javax.inject.Inject

class SupportedVerificationMethodsProvider @Inject constructor(
        private val hardwareInfo: HardwareInfo
) {
    
    fun provide(): List<VerificationMethod> {
        return mutableListOf(
                
                VerificationMethod.SAS,
                
                VerificationMethod.QR_CODE_SHOW)
                .apply {
                    if (hardwareInfo.hasBackCamera()) {
                        
                        add(VerificationMethod.QR_CODE_SCAN)
                    } else {
                        
                        Timber.w("No back Camera detected")
                    }
                }
    }
}
