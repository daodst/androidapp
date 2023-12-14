

package org.matrix.android.sdk.api.session.contentscanner

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt
import org.matrix.android.sdk.api.util.Optional

interface ContentScannerService {

    val serverPublicKey: String?

    fun getContentScannerServer(): String?
    fun setScannerUrl(url: String?)
    fun enableScanner(enabled: Boolean)
    fun isScannerEnabled(): Boolean
    fun getLiveStatusForFile(mxcUrl: String, fetchIfNeeded: Boolean = true, fileInfo: ElementToDecrypt? = null): LiveData<Optional<ScanStatusInfo>>
    fun getCachedScanResultForFile(mxcUrl: String): ScanStatusInfo?

    
    suspend fun getServerPublicKey(forceDownload: Boolean = false): String?
    suspend fun getScanResultForAttachment(mxcUrl: String, fileInfo: ElementToDecrypt? = null): ScanStatusInfo
}
