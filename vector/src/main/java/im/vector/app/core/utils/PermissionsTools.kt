

package im.vector.app.core.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseActivity

val PERMISSIONS_EMPTY = emptyList<String>()
val PERMISSIONS_FOR_AUDIO_IP_CALL = listOf(Manifest.permission.RECORD_AUDIO)
val PERMISSIONS_FOR_VIDEO_IP_CALL = listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
val PERMISSIONS_FOR_VOICE_MESSAGE = listOf(Manifest.permission.RECORD_AUDIO)
val PERMISSIONS_FOR_TAKING_PHOTO = listOf(Manifest.permission.CAMERA)

val PERMISSIONS_FOR_MEMBERS_SEARCH = listOf(Manifest.permission.READ_CONTACTS)
val PERMISSIONS_FOR_ROOM_AVATAR = listOf(Manifest.permission.CAMERA)
val PERMISSIONS_FOR_WRITING_FILES = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
val PERMISSIONS_FOR_TAKING_PHOTO_STORE = listOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
val PERMISSIONS_FOR_PICKING_CONTACT = listOf(Manifest.permission.READ_CONTACTS)
val PERMISSIONS_FOR_FOREGROUND_LOCATION_SHARING = listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
val PERMISSIONS_FOR_BACKGROUND_LOCATION_SHARING = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
} else {
    PERMISSIONS_EMPTY
}

private var permissionDialogDisplayed = false


fun ComponentActivity.registerForPermissionsResult(lambda: (allGranted: Boolean, deniedPermanently: Boolean) -> Unit): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        onPermissionResult(result, lambda)
    }
}

fun Fragment.registerForPermissionsResult(lambda: (allGranted: Boolean, deniedPermanently: Boolean) -> Unit): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        onPermissionResult(result, lambda)
    }
}

private fun onPermissionResult(result: Map<String, Boolean>, lambda: (allGranted: Boolean, deniedPermanently: Boolean) -> Unit) {
    if (result.keys.all { result[it] == true }) {
        lambda(true,  false)
    } else {
        if (permissionDialogDisplayed) {
            
            
            lambda(false, false)
        } else {
            
            lambda(false, true)
        }
    }
    
    permissionDialogDisplayed = false
}


fun checkPermissions(permissionsToBeGranted: List<String>,
                     activity: Activity,
                     activityResultLauncher: ActivityResultLauncher<Array<String>>,
                     @StringRes rationaleMessage: Int = 0): Boolean {
    
    val missingPermissions = permissionsToBeGranted.filter { permission ->
        ContextCompat.checkSelfPermission(activity.applicationContext, permission) == PackageManager.PERMISSION_DENIED
    }

    return if (missingPermissions.isNotEmpty()) {
        permissionDialogDisplayed = !permissionsDeniedPermanently(missingPermissions, activity)

        if (rationaleMessage != 0 && permissionDialogDisplayed) {
            
            
            MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.permissions_rationale_popup_title)
                    .setMessage(rationaleMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        activityResultLauncher.launch(missingPermissions.toTypedArray())
                    }
                    .setNegativeButton(R.string.action_not_now, null)
                    .show()
        } else {
            
            activityResultLauncher.launch(missingPermissions.toTypedArray())
        }
        false
    } else {
        
        true
    }
}


private fun permissionsDeniedPermanently(permissionsToBeGranted: List<String>,
                                         activity: Activity): Boolean {
    return permissionsToBeGranted
            .filter { permission ->
                ContextCompat.checkSelfPermission(activity.applicationContext, permission) == PackageManager.PERMISSION_DENIED
            }
            .any { permission ->
                
                
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission).not()
            }
}

fun VectorBaseActivity<*>.onPermissionDeniedSnackbar(@StringRes rationaleMessage: Int) {
    showSnackbar(getString(rationaleMessage), R.string.settings) {
        openAppSettingsPage(this)
    }
}

fun FragmentActivity.onPermissionDeniedDialog(@StringRes rationaleMessage: Int) {
    MaterialAlertDialogBuilder(this)
            .setTitle(R.string.missing_permissions_title)
            .setMessage(rationaleMessage)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                openAppSettingsPage(this)
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
}
