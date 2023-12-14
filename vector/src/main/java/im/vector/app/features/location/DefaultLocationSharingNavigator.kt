

package im.vector.app.features.location

import android.app.Activity
import im.vector.app.core.utils.openAppSettingsPage

class DefaultLocationSharingNavigator constructor(val activity: Activity?) : LocationSharingNavigator {

    override var goingToAppSettings: Boolean = false

    override fun quit() {
        activity?.finish()
    }

    override fun goToAppSettings() {
        activity?.let {
            goingToAppSettings = true
            openAppSettingsPage(it)
        }
    }
}
