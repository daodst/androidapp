

package org.matrix.android.sdk.internal.session.terms

import dagger.Lazy
import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.failure.toRegistrationFlowResponse
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataTypes
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.terms.GetTermsResponse
import org.matrix.android.sdk.api.session.terms.TermsResponse
import org.matrix.android.sdk.api.session.terms.TermsService
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.di.UnauthenticatedWithCertificate
import org.matrix.android.sdk.internal.network.NetworkConstants
import org.matrix.android.sdk.internal.network.RetrofitFactory
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.identity.IdentityAuthAPI
import org.matrix.android.sdk.internal.session.identity.IdentityRegisterTask
import org.matrix.android.sdk.internal.session.openid.GetOpenIdTokenTask
import org.matrix.android.sdk.internal.session.sync.model.accountdata.AcceptedTermsContent
import org.matrix.android.sdk.internal.session.user.accountdata.UpdateUserAccountDataTask
import org.matrix.android.sdk.internal.session.user.accountdata.UserAccountDataDataSource
import org.matrix.android.sdk.internal.util.ensureTrailingSlash
import timber.log.Timber
import javax.inject.Inject

internal class DefaultTermsService @Inject constructor(
        @UnauthenticatedWithCertificate
        private val unauthenticatedOkHttpClient: Lazy<OkHttpClient>,
        private val accountDataDataSource: UserAccountDataDataSource,
        private val termsAPI: TermsAPI,
        private val retrofitFactory: RetrofitFactory,
        private val getOpenIdTokenTask: GetOpenIdTokenTask,
        private val identityRegisterTask: IdentityRegisterTask,
        private val updateUserAccountDataTask: UpdateUserAccountDataTask
) : TermsService {

    override suspend fun getTerms(serviceType: TermsService.ServiceType,
                                  baseUrl: String): GetTermsResponse {
        val url = buildUrl(baseUrl, serviceType)
        val termsResponse = executeRequest(null) {
            termsAPI.getTerms("${url}terms")
        }
        return GetTermsResponse(termsResponse, getAlreadyAcceptedTermUrlsFromAccountData())
    }

    
    override suspend fun getHomeserverTerms(baseUrl: String): TermsResponse {
        return try {
            val request = baseUrl.ensureTrailingSlash() + NetworkConstants.URI_API_PREFIX_PATH_R0 + "register"
            executeRequest(null) {
                termsAPI.register(request)
            }
            
            Timber.w("Request $request succeeded, it should never happen")
            TermsResponse()
        } catch (throwable: Throwable) {
            val registrationFlowResponse = throwable.toRegistrationFlowResponse()
            if (registrationFlowResponse != null) {
                @Suppress("UNCHECKED_CAST")
                TermsResponse(
                        policies = (registrationFlowResponse
                                .params
                                ?.get(LoginFlowTypes.TERMS) as? JsonDict)
                                ?.get("policies") as? JsonDict
                )
            } else {
                
                Timber.e(throwable, "Error while getting homeserver terms")
                throw throwable
            }
        }
    }

    override suspend fun agreeToTerms(serviceType: TermsService.ServiceType,
                                      baseUrl: String,
                                      agreedUrls: List<String>,
                                      token: String?) {
        val url = buildUrl(baseUrl, serviceType)
        val tokenToUse = token?.takeIf { it.isNotEmpty() } ?: getToken(baseUrl)

        executeRequest(null) {
            termsAPI.agreeToTerms("${url}terms", AcceptTermsBody(agreedUrls), "Bearer $tokenToUse")
        }

        
        
        
        val listOfAcceptedTerms = getAlreadyAcceptedTermUrlsFromAccountData()

        val newList = listOfAcceptedTerms.toMutableSet().apply { addAll(agreedUrls) }.toList()

        updateUserAccountDataTask.execute(UpdateUserAccountDataTask.AcceptedTermsParams(
                acceptedTermsContent = AcceptedTermsContent(newList)
        ))
    }

    private suspend fun getToken(url: String): String {
        
        val api = retrofitFactory.create(unauthenticatedOkHttpClient, url).create(IdentityAuthAPI::class.java)

        val openIdToken = getOpenIdTokenTask.execute(Unit)
        val token = identityRegisterTask.execute(IdentityRegisterTask.Params(api, openIdToken))

        return token.token
    }

    private fun buildUrl(baseUrl: String, serviceType: TermsService.ServiceType): String {
        val servicePath = when (serviceType) {
            TermsService.ServiceType.IntegrationManager -> NetworkConstants.URI_INTEGRATION_MANAGER_PATH
            TermsService.ServiceType.IdentityService    -> NetworkConstants.URI_IDENTITY_PATH_V2
        }
        return "${baseUrl.ensureTrailingSlash()}$servicePath"
    }

    private fun getAlreadyAcceptedTermUrlsFromAccountData(): Set<String> {
        return accountDataDataSource.getAccountDataEvent(UserAccountDataTypes.TYPE_ACCEPTED_TERMS)
                ?.content
                ?.toModel<AcceptedTermsContent>()
                ?.acceptedTerms
                ?.toSet()
                .orEmpty()
    }
}
