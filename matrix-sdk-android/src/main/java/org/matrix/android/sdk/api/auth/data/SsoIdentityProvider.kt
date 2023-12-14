

package org.matrix.android.sdk.api.auth.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class SsoIdentityProvider(
        
        @Json(name = "id") val id: String,
        
        @Json(name = "name") val name: String?,
        
        @Json(name = "icon") val iconUrl: String?,

        
        @Json(name = "brand") val brand: String?

) : Parcelable, Comparable<SsoIdentityProvider> {

    companion object {
        const val BRAND_GOOGLE = "google"
        const val BRAND_GITHUB = "github"
        const val BRAND_APPLE = "apple"
        const val BRAND_FACEBOOK = "facebook"
        const val BRAND_TWITTER = "twitter"
        const val BRAND_GITLAB = "gitlab"
    }

    override fun compareTo(other: SsoIdentityProvider): Int {
        return other.toPriority().compareTo(toPriority())
    }

    private fun toPriority(): Int {
        return when (brand) {
            
            BRAND_GOOGLE   -> 5
            
            BRAND_FACEBOOK -> 4
            
            BRAND_TWITTER  -> 3
            
            BRAND_GITHUB,
            BRAND_GITLAB   -> 2
            
            BRAND_APPLE    -> 1
            else           -> 0
        }
    }
}
