

package org.matrix.android.sdk.api.session.terms

data class GetTermsResponse(
        val serverResponse: TermsResponse,
        val alreadyAcceptedTermUrls: Set<String>
)
