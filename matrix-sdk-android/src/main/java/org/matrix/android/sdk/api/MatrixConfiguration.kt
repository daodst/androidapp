

package org.matrix.android.sdk.api

import okhttp3.ConnectionSpec
import org.matrix.android.sdk.api.crypto.MXCryptoConfig
import java.net.Proxy

data class MatrixConfiguration(
        val applicationFlavor: String = "Default-application-flavor",
        val cryptoConfig: MXCryptoConfig = MXCryptoConfig(),
        val integrationUIUrl: String = "https://scalar.vector.im/",
        val integrationRestUrl: String = "https://scalar.vector.im/api",
        val integrationWidgetUrls: List<String> = listOf(
                "https://scalar.vector.im/_matrix/integrations/v1",
                "https://scalar.vector.im/api",
                "https://scalar-staging.vector.im/_matrix/integrations/v1",
                "https://scalar-staging.vector.im/api",
                "https://scalar-staging.riot.im/scalar/api"
        ),
        
        val clientPermalinkBaseUrl: String? = null,
        
        val proxy: Proxy? = null,
        
        val connectionSpec: ConnectionSpec = ConnectionSpec.RESTRICTED_TLS,
        
        val supportsCallTransfer: Boolean = false,
        
        val matrixItemDisplayNameFallbackProvider: MatrixItemDisplayNameFallbackProvider? = null,
        
        val roomDisplayNameFallbackProvider: RoomDisplayNameFallbackProvider,
        
        val threadMessagesEnabledDefault: Boolean = false,
) {

    
    @Deprecated("Use Matrix.createInstance and manage the instance manually instead of Matrix.getInstance")
    interface Provider {
        fun providesMatrixConfiguration(): MatrixConfiguration
    }
}
