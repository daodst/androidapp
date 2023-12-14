

package org.matrix.android.sdk.api.session.identity.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignInvitationResult(
        
        val mxid: String,
        
        val sender: String,
        
        val signatures: Map<String, *>,
        
        val token: String
)
