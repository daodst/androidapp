

package im.vector.app.features.qrcode

import im.vector.app.core.platform.VectorViewModelAction

sealed class QrCodeScannerAction : VectorViewModelAction {
    data class CodeDecoded(
            val result: String,
            val isQrCode: Boolean
    ) : QrCodeScannerAction()

    object ScanFailed : QrCodeScannerAction()

    object SwitchMode : QrCodeScannerAction()
}
