

package im.vector.app.features.crypto.verification

import im.vector.app.core.platform.VectorViewEvents


sealed class VerificationBottomSheetViewEvents : VectorViewEvents {
    object Dismiss : VerificationBottomSheetViewEvents()
    object AccessSecretStore : VerificationBottomSheetViewEvents()
    object GoToSettings : VerificationBottomSheetViewEvents()
    data class ModalError(val errorMessage: CharSequence) : VerificationBottomSheetViewEvents()
}
