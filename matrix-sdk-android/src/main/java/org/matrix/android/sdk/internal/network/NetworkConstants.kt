

package org.matrix.android.sdk.internal.network

internal object NetworkConstants {
    
    private const val URI_API_PREFIX_PATH = "_matrix/client"
    const val URI_API_PREFIX_PATH_ = "$URI_API_PREFIX_PATH/"
    const val URI_API_PREFIX_PATH_R0 = "$URI_API_PREFIX_PATH/r0/"
    const val URI_API_PREFIX_PATH_V1 = "$URI_API_PREFIX_PATH/v1/"
    const val URI_API_PREFIX_PATH_UNSTABLE = "$URI_API_PREFIX_PATH/unstable/"

    
    private const val URI_API_MEDIA_PREFIX_PATH = "_matrix/media"
    const val URI_API_MEDIA_PREFIX_PATH_R0 = "$URI_API_MEDIA_PREFIX_PATH/r0/"

    
    const val URI_IDENTITY_PREFIX_PATH = "_matrix/identity/v2"
    const val URI_IDENTITY_PATH_V2 = "$URI_IDENTITY_PREFIX_PATH/"
    const val URI_IDENTITY_PATH_V1 = "_matrix/identity/api/v1/"

    
    const val URI_PUSH_GATEWAY_PREFIX_PATH = "_matrix/push/v1/"

    
    const val URI_INTEGRATION_MANAGER_PATH = "_matrix/integrations/v1/"

    
    const val URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE = "_matrix/media_proxy/unstable/"

    
    const val URI_FEDERATION_PATH = "_matrix/federation/v1/"
}
