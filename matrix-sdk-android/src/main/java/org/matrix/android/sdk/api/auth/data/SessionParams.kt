

package org.matrix.android.sdk.api.auth.data


data class SessionParams(
        
        val credentials: Credentials,

        
        val homeServerConnectionConfig: HomeServerConnectionConfig,

        
        val isTokenValid: Boolean
) {
    

    
    val userId = credentials.userId

    
    val deviceId = credentials.deviceId

    
    val homeServerUrl = homeServerConnectionConfig.homeServerUri.toString()

    
    val homeServerUrlBase = homeServerConnectionConfig.homeServerUriBase.toString()

    
    val homeServerHost = homeServerConnectionConfig.homeServerUri.host

    
    val defaultIdentityServerUrl = homeServerConnectionConfig.identityServerUri?.toString()
}
