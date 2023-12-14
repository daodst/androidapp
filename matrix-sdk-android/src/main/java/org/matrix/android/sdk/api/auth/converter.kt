

package org.matrix.android.sdk.api.auth

import org.matrix.android.sdk.api.auth.data.LocalizedFlowDataLoginTerms
import org.matrix.android.sdk.api.auth.registration.TermPolicies


fun TermPolicies.toLocalizedLoginTerms(userLanguage: String,
                                       defaultLanguage: String = "en"): List<LocalizedFlowDataLoginTerms> {
    val result = ArrayList<LocalizedFlowDataLoginTerms>()

    val policies = get("policies")
    if (policies is Map<*, *>) {
        policies.keys.forEach { policyName ->
            val localizedFlowDataLoginTermsPolicyName = policyName as String
            var localizedFlowDataLoginTermsVersion: String? = null
            var localizedFlowDataLoginTermsLocalizedUrl: String? = null
            var localizedFlowDataLoginTermsLocalizedName: String? = null

            val policy = policies[policyName]

            
            if (policy is Map<*, *>) {
                
                localizedFlowDataLoginTermsVersion = policy["version"] as String?

                var userLanguageUrlAndName: UrlAndName? = null
                var defaultLanguageUrlAndName: UrlAndName? = null
                var firstUrlAndName: UrlAndName? = null

                
                policy.keys.forEach { policyKey ->
                    when (policyKey) {
                        "version"       -> Unit 
                        userLanguage    -> {
                            
                            userLanguageUrlAndName = extractUrlAndName(policy[policyKey])
                        }
                        defaultLanguage -> {
                            
                            defaultLanguageUrlAndName = extractUrlAndName(policy[policyKey])
                        }
                        else            -> {
                            if (firstUrlAndName == null) {
                                
                                firstUrlAndName = extractUrlAndName(policy[policyKey])
                            }
                        }
                    }
                }

                
                when {
                    userLanguageUrlAndName != null    -> {
                        localizedFlowDataLoginTermsLocalizedUrl = userLanguageUrlAndName!!.url
                        localizedFlowDataLoginTermsLocalizedName = userLanguageUrlAndName!!.name
                    }
                    defaultLanguageUrlAndName != null -> {
                        localizedFlowDataLoginTermsLocalizedUrl = defaultLanguageUrlAndName!!.url
                        localizedFlowDataLoginTermsLocalizedName = defaultLanguageUrlAndName!!.name
                    }
                    firstUrlAndName != null           -> {
                        localizedFlowDataLoginTermsLocalizedUrl = firstUrlAndName!!.url
                        localizedFlowDataLoginTermsLocalizedName = firstUrlAndName!!.name
                    }
                }
            }

            result.add(LocalizedFlowDataLoginTerms(
                    policyName = localizedFlowDataLoginTermsPolicyName,
                    version = localizedFlowDataLoginTermsVersion,
                    localizedUrl = localizedFlowDataLoginTermsLocalizedUrl,
                    localizedName = localizedFlowDataLoginTermsLocalizedName
            ))
        }
    }

    return result
}

private fun extractUrlAndName(policyData: Any?): UrlAndName? {
    if (policyData is Map<*, *>) {
        val url = policyData["url"] as String?
        val name = policyData["name"] as String?

        if (url != null && name != null) {
            return UrlAndName(url, name)
        }
    }
    return null
}
