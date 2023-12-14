

package im.vector.app.core.platform

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import im.vector.app.R
import javax.inject.Inject

class ScreenOrientationLocker @Inject constructor(
        private val resources: Resources
) {

    
    @SuppressLint("SourceLockedOrientationActivity")
    fun lockPhonesToPortrait(activity: AppCompatActivity) {
        when (resources.getBoolean(R.bool.is_tablet)) {
            true  -> {
                
            }
            false -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}
