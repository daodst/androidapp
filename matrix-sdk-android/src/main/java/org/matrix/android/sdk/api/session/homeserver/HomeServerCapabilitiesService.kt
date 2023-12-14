

package org.matrix.android.sdk.api.session.homeserver


interface HomeServerCapabilitiesService {

    
    suspend fun refreshHomeServerCapabilities()

    
    fun getHomeServerCapabilities(): HomeServerCapabilities
}
