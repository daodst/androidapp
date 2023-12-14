

package org.matrix.android.sdk.api.federation

interface FederationService {
    
    suspend fun getFederationVersion(): FederationVersion
}
