

package org.matrix.android.sdk.internal.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes

@JsonClass(generateAdapter = true)
internal data class TokenLoginParams(
        @Json(name = "type") override val type: String = LoginFlowTypes.TOKEN,
        @Json(name = "token") val token: String
) : LoginParams
