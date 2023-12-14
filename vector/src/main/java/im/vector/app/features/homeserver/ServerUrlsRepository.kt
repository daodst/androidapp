

package im.vector.app.features.homeserver

import android.content.Context
import androidx.core.content.edit
import im.vector.app.R
import im.vector.app.core.di.DefaultSharedPreferences


object ServerUrlsRepository {

    
    private const val DEFAULT_REFERRER_HOME_SERVER_URL_PREF = "default_referrer_home_server_url"
    private const val DEFAULT_REFERRER_IDENTITY_SERVER_URL_PREF = "default_referrer_identity_server_url"

    
    const val HOME_SERVER_URL_PREF = "home_server_url"
    const val IDENTITY_SERVER_URL_PREF = "identity_server_url"

    
    fun setDefaultUrlsFromReferrer(context: Context, homeServerUrl: String, identityServerUrl: String) {
        DefaultSharedPreferences.getInstance(context)
                .edit {
                    if (homeServerUrl.isNotEmpty()) {
                        putString(DEFAULT_REFERRER_HOME_SERVER_URL_PREF, homeServerUrl)
                    }

                    if (identityServerUrl.isNotEmpty()) {
                        putString(DEFAULT_REFERRER_IDENTITY_SERVER_URL_PREF, identityServerUrl)
                    }
                }
    }

    
    fun saveServerUrls(context: Context, homeServerUrl: String, identityServerUrl: String) {
        DefaultSharedPreferences.getInstance(context)
                .edit {
                    putString(HOME_SERVER_URL_PREF, homeServerUrl)
                    putString(IDENTITY_SERVER_URL_PREF, identityServerUrl)
                }
    }

    
    fun getLastHomeServerUrl(context: Context): String {
        val prefs = DefaultSharedPreferences.getInstance(context)

        return prefs.getString(HOME_SERVER_URL_PREF,
                prefs.getString(DEFAULT_REFERRER_HOME_SERVER_URL_PREF,
                        getDefaultHomeServerUrl(context))!!)!!
    }

    
    fun isDefaultHomeServerUrl(context: Context, url: String) = url == getDefaultHomeServerUrl(context)

    
    fun getDefaultHomeServerUrl(context: Context): String = context.getString(R.string.matrix_org_server_url)
}
