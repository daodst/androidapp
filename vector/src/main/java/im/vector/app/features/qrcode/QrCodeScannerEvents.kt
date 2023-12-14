

package im.vector.app.features.qrcode

import im.vector.app.core.platform.VectorViewEvents

sealed class QrCodeScannerEvents : VectorViewEvents {
    data class CodeParsed(val result: String, val isQrCode: Boolean) : QrCodeScannerEvents()
    object ParseFailed : QrCodeScannerEvents()
    object SwitchMode : QrCodeScannerEvents()
}
