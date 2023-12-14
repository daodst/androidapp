

package im.vector.app.features.login

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LoginConfig(
        val homeServerUrl: String? = null,
        private val identityServerUrl: String?= null,
         val isLogOut: Boolean = false
) : Parcelable {

    companion object {
        const val CONFIG_HS_PARAMETER = "hs_url"
        private const val CONFIG_IS_PARAMETER = "is_url"

        fun parse(from: Uri): LoginConfig {
            return LoginConfig(
                    homeServerUrl = from.getQueryParameter(CONFIG_HS_PARAMETER),
                    identityServerUrl = from.getQueryParameter(CONFIG_IS_PARAMETER)
            )
        }
    }
}
