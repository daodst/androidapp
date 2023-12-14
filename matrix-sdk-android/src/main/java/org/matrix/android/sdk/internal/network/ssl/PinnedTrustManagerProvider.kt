

package org.matrix.android.sdk.internal.network.ssl

import android.os.Build
import org.matrix.android.sdk.api.network.ssl.Fingerprint
import javax.net.ssl.X509ExtendedTrustManager
import javax.net.ssl.X509TrustManager

internal object PinnedTrustManagerProvider {
    
    private const val USE_DEFAULT_TRUST_MANAGER = true

    fun provide(fingerprints: List<Fingerprint>?,
                defaultTrustManager: X509TrustManager?): X509TrustManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && defaultTrustManager is X509ExtendedTrustManager) {
            PinnedTrustManagerApi24(
                    fingerprints.orEmpty(),
                    defaultTrustManager.takeIf { USE_DEFAULT_TRUST_MANAGER }
            )
        } else {
            PinnedTrustManager(
                    fingerprints.orEmpty(),
                    defaultTrustManager.takeIf { USE_DEFAULT_TRUST_MANAGER }
            )
        }
    }
}
