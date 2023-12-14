

package org.matrix.android.sdk.api.session.terms

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.LocalizedFlowDataLoginTerms
import org.matrix.android.sdk.api.util.JsonDict


@JsonClass(generateAdapter = true)
data class TermsResponse(
        @Json(name = "policies")
        val policies: JsonDict? = null
) {

    fun getLocalizedTerms(userLanguage: String,
                          defaultLanguage: String = "en"): List<LocalizedFlowDataLoginTerms> {
        return policies?.map {
            val tos = policies[it.key] as? Map<*, *> ?: return@map null
            ((tos[userLanguage] ?: tos[defaultLanguage]) as? Map<*, *>)?.let { termsMap ->
                val name = termsMap[NAME] as? String
                val url = termsMap[URL] as? String
                LocalizedFlowDataLoginTerms(
                        policyName = it.key,
                        localizedUrl = url,
                        localizedName = name,
                        version = tos[VERSION] as? String
                )
            }
        }?.filterNotNull().orEmpty()
    }

    private companion object {
        const val VERSION = "version"
        const val NAME = "name"
        const val URL = "url"
    }
}
