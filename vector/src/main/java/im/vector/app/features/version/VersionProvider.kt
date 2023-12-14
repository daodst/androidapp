

package im.vector.app.features.version

import im.vector.app.BuildConfig
import im.vector.app.core.resources.VersionCodeProvider
import javax.inject.Inject

class VersionProvider @Inject constructor(private val versionCodeProvider: VersionCodeProvider) {

    fun getVersion(longFormat: Boolean, useBuildNumber: Boolean): String {
        var result = "${BuildConfig.VERSION_NAME} [${versionCodeProvider.getVersionCode()}]"

        var flavor = BuildConfig.SHORT_FLAVOR_DESCRIPTION

        if (flavor.isNotBlank()) {
            flavor += "-"
        }

        var gitVersion = BuildConfig.GIT_REVISION
        val gitRevisionDate = BuildConfig.GIT_REVISION_DATE
        val buildNumber = BuildConfig.BUILD_NUMBER

        var useLongFormat = longFormat

        if (useBuildNumber && buildNumber != "0") {
            
            gitVersion = "b$buildNumber"
            useLongFormat = false
        }

        result += if (useLongFormat) {
            " ($flavor$gitVersion-$gitRevisionDate)"
        } else {
            " ($flavor$gitVersion)"
        }

        return result
    }
}
