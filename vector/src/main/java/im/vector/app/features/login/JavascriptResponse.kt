

package im.vector.app.features.login

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.Credentials

@JsonClass(generateAdapter = true)
data class JavascriptResponse(
        @Json(name = "action")
        val action: String? = null,

        
        @Json(name = "response")
        val response: String? = null,

        
        @Json(name = "credentials")
        val credentials: Credentials? = null
)
