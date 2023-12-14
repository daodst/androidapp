

package org.matrix.android.sdk.internal.session.contentscanner.data

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.contentscanner.ScanState
import org.matrix.android.sdk.api.session.contentscanner.ScanStatusInfo
import org.matrix.android.sdk.api.util.Optional

internal interface ContentScannerStore {

    fun getScannerUrl(): String?

    fun setScannerUrl(url: String?)

    fun enableScanner(enabled: Boolean)

    fun isScanEnabled(): Boolean

    fun getScanResult(mxcUrl: String): ScanStatusInfo?
    fun getLiveScanResult(mxcUrl: String): LiveData<Optional<ScanStatusInfo>>
    fun isScanResultKnownOrInProgress(mxcUrl: String, scannerUrl: String?): Boolean

    fun updateStateForContent(mxcUrl: String, state: ScanState, scannerUrl: String?)
    fun updateScanResultForContent(mxcUrl: String, scannerUrl: String?, state: ScanState, humanReadable: String)
}
