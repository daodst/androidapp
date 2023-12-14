

package org.matrix.android.sdk.internal.network

import android.content.Context
import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.internal.di.MatrixScope
import timber.log.Timber
import javax.inject.Inject

@MatrixScope
internal class UserAgentHolder @Inject constructor(private val context: Context,
                                                   matrixConfiguration: MatrixConfiguration) {

    var userAgent: String = ""
        private set

    init {
        setApplicationFlavor(matrixConfiguration.applicationFlavor)
    }

    
    private fun setApplicationFlavor(flavorDescription: String) {
        var appName = ""
        var appVersion = ""

        try {
            val appPackageName = context.applicationContext.packageName
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(appPackageName, 0)
            appName = pm.getApplicationLabel(appInfo).toString()

            val pkgInfo = pm.getPackageInfo(context.applicationContext.packageName, 0)
            appVersion = pkgInfo.versionName ?: ""

            
            if (!appName.matches("\\A\\p{ASCII}*\\z".toRegex())) {
                appName = appPackageName
            }
        } catch (e: Exception) {
            Timber.e(e, "## initUserAgent() : failed")
        }

        val systemUserAgent = System.getProperty("http.agent")

        
        if (appName.isEmpty() || appVersion.isEmpty()) {
            if (null == systemUserAgent) {
                userAgent = "Java" + System.getProperty("java.version")
            }
            return
        }

        
        if (null == systemUserAgent || systemUserAgent.lastIndexOf(")") == -1 || !systemUserAgent.contains("(")) {
            userAgent = (appName + "/" + appVersion + " ( Flavour " + flavorDescription +
                    "; MatrixAndroidSdk2 " + BuildConfig.SDK_VERSION + ")")
        } else {
            
            userAgent = appName + "/" + appVersion + " " +
                    systemUserAgent.substring(systemUserAgent.indexOf("("), systemUserAgent.lastIndexOf(")") - 1) +
                    "; Flavour " + flavorDescription +
                    "; MatrixAndroidSdk2 " + BuildConfig.SDK_VERSION + ")"
        }
    }
}
