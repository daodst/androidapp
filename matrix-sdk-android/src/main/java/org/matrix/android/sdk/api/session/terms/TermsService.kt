

package org.matrix.android.sdk.api.session.terms

interface TermsService {
    enum class ServiceType {
        IntegrationManager,
        IdentityService
    }

    suspend fun getTerms(serviceType: ServiceType, baseUrl: String): GetTermsResponse

    suspend fun agreeToTerms(serviceType: ServiceType,
                             baseUrl: String,
                             agreedUrls: List<String>,
                             token: String?)

    
    suspend fun getHomeserverTerms(baseUrl: String): TermsResponse
}
