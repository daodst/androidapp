

package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict


@JsonClass(generateAdapter = true)
internal data class RegistrationCustomParams(
        
        @Json(name = "auth")
        val auth: JsonDict? = null,
)
