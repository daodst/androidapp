

package im.vector.app.core.resources

import android.content.Context
import android.os.Build
import androidx.annotation.NonNull
import javax.inject.Inject

class VersionCodeProvider @Inject constructor(private val context: Context) {

    
    @NonNull
    fun getVersionCode(): Long {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }
}
