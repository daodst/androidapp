

package im.vector.app.features.discovery

import im.vector.app.core.utils.ensureProtocol
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.terms.TermsResponse
import org.matrix.android.sdk.api.session.terms.TermsService

suspend fun Session.fetchIdentityServerWithTerms(userLanguage: String): ServerAndPolicies? {
    return identityService().getCurrentIdentityServerUrl()
            ?.let { identityServerUrl ->
                val termsResponse = getTerms(TermsService.ServiceType.IdentityService, identityServerUrl.ensureProtocol())
                        .serverResponse
                buildServerAndPolicies(identityServerUrl, termsResponse, userLanguage)
            }
}

suspend fun Session.fetchHomeserverWithTerms(userLanguage: String): ServerAndPolicies {
    val homeserverUrl = sessionParams.homeServerUrl
    val terms = getHomeserverTerms(homeserverUrl.ensureProtocol())
    return buildServerAndPolicies(homeserverUrl, terms, userLanguage)
}

private fun buildServerAndPolicies(serviceUrl: String,
                                   termsResponse: TermsResponse,
                                   userLanguage: String): ServerAndPolicies {
    val terms = termsResponse.getLocalizedTerms(userLanguage)
    val policyUrls = terms.mapNotNull {
        val name = it.localizedName ?: it.policyName
        val url = it.localizedUrl
        if (name == null || url == null) {
            null
        } else {
            ServerPolicy(name = name, url = url)
        }
    }
    return ServerAndPolicies(serviceUrl, policyUrls)
}
