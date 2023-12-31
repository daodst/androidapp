

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.identity.FoundThreePid
import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.identity.toMedium
import org.matrix.android.sdk.internal.crypto.tools.withOlmUtility
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.identity.data.IdentityStore
import org.matrix.android.sdk.internal.session.identity.model.IdentityHashDetailResponse
import org.matrix.android.sdk.internal.session.identity.model.IdentityLookUpParams
import org.matrix.android.sdk.internal.session.identity.model.IdentityLookUpResponse
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.base64ToBase64Url
import java.util.Locale
import javax.inject.Inject

internal interface IdentityBulkLookupTask : Task<IdentityBulkLookupTask.Params, List<FoundThreePid>> {
    data class Params(
            val threePids: List<ThreePid>
    )
}

internal class DefaultIdentityBulkLookupTask @Inject constructor(
        private val identityApiProvider: IdentityApiProvider,
        private val identityStore: IdentityStore,
        @UserId private val userId: String
) : IdentityBulkLookupTask {

    override suspend fun execute(params: IdentityBulkLookupTask.Params): List<FoundThreePid> {
        val identityAPI = getIdentityApiAndEnsureTerms(identityApiProvider, userId)
        val identityData = identityStore.getIdentityData() ?: throw IdentityServiceError.NoIdentityServerConfigured
        val pepper = identityData.hashLookupPepper
        val hashDetailResponse = if (pepper == null) {
            
            fetchHashDetails(identityAPI)
                    .also { identityStore.setHashDetails(it) }
        } else {
            IdentityHashDetailResponse(pepper, identityData.hashLookupAlgorithm)
        }

        if (hashDetailResponse.algorithms.contains(IdentityHashDetailResponse.ALGORITHM_SHA256).not()) {
            
            
            throw IdentityServiceError.BulkLookupSha256NotSupported
        }

        val lookUpData = lookUpInternal(identityAPI, params.threePids, hashDetailResponse, true)

        
        return handleSuccess(params.threePids, lookUpData)
    }

    data class LookUpData(
            val hashedAddresses: List<String>,
            val identityLookUpResponse: IdentityLookUpResponse
    )

    private suspend fun lookUpInternal(identityAPI: IdentityAPI,
                                       threePids: List<ThreePid>,
                                       hashDetailResponse: IdentityHashDetailResponse,
                                       canRetry: Boolean): LookUpData {
        val hashedAddresses = getHashedAddresses(threePids, hashDetailResponse.pepper)
        return try {
            LookUpData(hashedAddresses,
                    executeRequest(null) {
                        identityAPI.lookup(IdentityLookUpParams(
                                hashedAddresses,
                                IdentityHashDetailResponse.ALGORITHM_SHA256,
                                hashDetailResponse.pepper
                        ))
                    })
        } catch (failure: Throwable) {
            
            if (canRetry && failure is Failure.ServerError && failure.error.code == MatrixError.M_INVALID_PEPPER) {
                
                val newHashDetailResponse = if (!failure.error.newLookupPepper.isNullOrEmpty()) {
                    
                    hashDetailResponse.copy(pepper = failure.error.newLookupPepper)
                } else {
                    
                    fetchHashDetails(identityAPI)
                }
                        .also { identityStore.setHashDetails(it) }
                if (newHashDetailResponse.algorithms.contains(IdentityHashDetailResponse.ALGORITHM_SHA256).not()) {
                    
                    throw IdentityServiceError.BulkLookupSha256NotSupported
                }
                lookUpInternal(identityAPI, threePids, newHashDetailResponse, false )
            } else {
                
                throw failure
            }
        }
    }

    private fun getHashedAddresses(threePids: List<ThreePid>, pepper: String): List<String> {
        return withOlmUtility { olmUtility ->
            threePids.map { threePid ->
                base64ToBase64Url(
                        olmUtility.sha256(threePid.value.lowercase(Locale.ROOT) +
                                " " + threePid.toMedium() + " " + pepper)
                )
            }
        }
    }

    private suspend fun fetchHashDetails(identityAPI: IdentityAPI): IdentityHashDetailResponse {
        return executeRequest(null) {
            identityAPI.hashDetails()
        }
    }

    private fun handleSuccess(threePids: List<ThreePid>, lookupData: LookUpData): List<FoundThreePid> {
        return lookupData.identityLookUpResponse.mappings.keys.map { hashedAddress ->
            FoundThreePid(
                    threePids[lookupData.hashedAddresses.indexOf(hashedAddress)],
                    lookupData.identityLookUpResponse.mappings[hashedAddress] ?: error("")
            )
        }
    }
}
