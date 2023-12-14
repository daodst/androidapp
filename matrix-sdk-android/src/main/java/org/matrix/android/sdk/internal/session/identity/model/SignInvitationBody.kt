

package org.matrix.android.sdk.internal.session.identity.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SignInvitationBody(
        
        val mxid: String,
        
        val token: String,
        
        val private_key: String
)
