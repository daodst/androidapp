

package im.vector.app.core.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import im.vector.app.R
import im.vector.app.features.notifications.NotificationUtils


fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            context.getSystemService<PowerManager>()?.isIgnoringBatteryOptimizations(context.packageName) == true
}

fun isAirplaneModeOn(context: Context): Boolean {
    return Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
}

fun isAnimationDisabled(context: Context): Boolean {
    return Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
}


@TargetApi(Build.VERSION_CODES.M)
fun requestDisablingBatteryOptimization(activity: Activity, activityResultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent()
    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    intent.data = Uri.parse("package:" + activity.packageName)
    activityResultLauncher.launch(intent)
}



fun copyToClipboard(context: Context, text: CharSequence, showToast: Boolean = true, @StringRes toastMessage: Int = R.string.copied_to_clipboard) {
    val clipboard = context.getSystemService<ClipboardManager>()!!
    clipboard.setPrimaryClip(ClipData.newPlainText("", text))
    if (showToast) {
        context.toast(toastMessage)
    }
}


fun startNotificationSettingsIntent(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    } else {
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra("app_package", context.packageName)
        intent.putExtra("app_uid", context.applicationInfo?.uid)
    }
    activityResultLauncher.launch(intent)
}


@TargetApi(Build.VERSION_CODES.O)
fun startNotificationChannelSettingsIntent(fragment: Fragment, channelID: String) {
    if (!NotificationUtils.supportNotificationChannels()) return
    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, fragment.context?.packageName)
        putExtra(Settings.EXTRA_CHANNEL_ID, channelID)
    }
    fragment.startActivity(intent)
}

fun startAddGoogleAccountIntent(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
    intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
    try {
        activityResultLauncher.launch(intent)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        context.toast(R.string.error_no_external_application_found)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun startInstallFromSourceIntent(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            .setData(Uri.parse(String.format("package:%s", context.packageName)))
    try {
        activityResultLauncher.launch(intent)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        context.toast(R.string.error_no_external_application_found)
    }
}

fun startSharePlainTextIntent(fragment: Fragment,
                              activityResultLauncher: ActivityResultLauncher<Intent>?,
                              chooserTitle: String?,
                              text: String,
                              subject: String? = null,
                              extraTitle: String? = null) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    
    share.putExtra(Intent.EXTRA_SUBJECT, subject)
    share.putExtra(Intent.EXTRA_TEXT, text)

    extraTitle?.let {
        share.putExtra(Intent.EXTRA_TITLE, it)
    }

    val intent = Intent.createChooser(share, chooserTitle)
    try {
        if (activityResultLauncher != null) {
            activityResultLauncher.launch(intent)
        } else {
            fragment.startActivity(intent)
        }
    } catch (activityNotFoundException: ActivityNotFoundException) {
        fragment.activity?.toast(R.string.error_no_external_application_found)
    }
}


fun startSharePlainTextIntent(activity: Activity,
                              chooserTitle: String?,
                              text: String,
                              subject: String? = null,
                              extraTitle: String? = null) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    
    share.putExtra(Intent.EXTRA_SUBJECT, subject)
    share.putExtra(Intent.EXTRA_TEXT, text)

    extraTitle?.let {
        share.putExtra(Intent.EXTRA_TITLE, it)
    }

    val intent = Intent.createChooser(share, chooserTitle)
    try {
        activity.startActivity(intent)
    } catch (activityNotFoundException: ActivityNotFoundException) {
       activity.toast(R.string.error_no_external_application_found)
    }
}

fun startImportTextFromFileIntent(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "text/plain"
    }
    try {
        activityResultLauncher.launch(intent)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        context.toast(R.string.error_no_external_application_found)
    }
}

fun Context.toast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
