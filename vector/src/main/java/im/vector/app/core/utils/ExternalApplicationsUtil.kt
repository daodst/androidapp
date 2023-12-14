

package im.vector.app.core.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Browser
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.features.notifications.NotificationUtils
import im.vector.app.features.themes.ThemeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.util.MimeTypes
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeAudio
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeImage
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeVideo
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun openUrlInExternalBrowser(context: Context, url: String?) {
    url?.let {
        openUrlInExternalBrowser(context, Uri.parse(it))
    }
}


fun openUrlInExternalBrowser(context: Context, uri: Uri?) {
    uri?.let {
        val browserIntent = Intent(Intent.ACTION_VIEW, it).apply {
            putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
            putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
        }

        context.safeStartActivity(browserIntent)
    }
}


fun openUrlInChromeCustomTab(context: Context,
                             session: CustomTabsSession?,
                             url: String) {
    try {
        CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(
                        CustomTabColorSchemeParams.Builder()
                                .setToolbarColor(ThemeUtils.getColor(context, android.R.attr.colorBackground))
                                .setNavigationBarColor(ThemeUtils.getColor(context, android.R.attr.colorBackground))
                                .build()
                )
                .setColorScheme(
                        when {
                            ThemeUtils.isSystemTheme(context) -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
                            ThemeUtils.isLightTheme(context)  -> CustomTabsIntent.COLOR_SCHEME_LIGHT
                            else                              -> CustomTabsIntent.COLOR_SCHEME_DARK
                        }
                )
                
                .setCloseButtonIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_back_24dp))
                .setStartAnimations(context, R.anim.enter_fade_in, R.anim.exit_fade_out)
                .setExitAnimations(context, R.anim.enter_fade_in, R.anim.exit_fade_out)
                .apply { session?.let { setSession(it) } }
                .build()
                .launchUrl(context, Uri.parse(url))
    } catch (activityNotFoundException: ActivityNotFoundException) {
        context.toast(R.string.error_no_external_application_found)
    }
}


fun openFileSelection(activity: Activity,
                      activityResultLauncher: ActivityResultLauncher<Intent>?,
                      allowMultipleSelection: Boolean,
                      requestCode: Int) {
    val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
    fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleSelection)

    fileIntent.addCategory(Intent.CATEGORY_OPENABLE)
    fileIntent.type = MimeTypes.Any

    try {
        if (activityResultLauncher != null) {
            activityResultLauncher.launch(fileIntent)
        } else {
            activity.startActivityForResult(fileIntent, requestCode)
        }
    } catch (activityNotFoundException: ActivityNotFoundException) {
        activity.toast(R.string.error_no_external_application_found)
    }
}


fun sendMailTo(address: String, subject: String? = null, message: String? = null, activity: Activity) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", address, null))
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, message)

    activity.safeStartActivity(intent)
}


fun openUri(activity: Activity, uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

    activity.safeStartActivity(intent)
}


fun openMedia(activity: Activity, savedMediaPath: String, mimeType: String) {
    val file = File(savedMediaPath)
    val uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", file)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    activity.safeStartActivity(intent)
}


fun openLocation(activity: Activity, latitude: Double, longitude: Double) {
    val locationUri = buildString {
        append("geo:")
        append(latitude)
        append(",")
        append(longitude)
        append("?q=") 
        append(latitude)
        append(",")
        append(longitude)
    }
    openUri(activity, locationUri)
}

fun shareMedia(context: Context, file: File, mediaMimeType: String?) {
    val mediaUri = try {
        FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file)
    } catch (e: Exception) {
        Timber.e(e, "onMediaAction Selected File cannot be shared")
        return
    }

    val chooserIntent = ShareCompat.IntentBuilder(context)
            .setType(mediaMimeType)
            .setStream(mediaUri)
            .setChooserTitle(R.string.action_share)
            .createChooserIntent()

    context.safeStartActivity(chooserIntent)
}

fun shareText(context: Context, text: String) {
    val chooserIntent = ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setText(text)
            .setChooserTitle(R.string.action_share)
            .createChooserIntent()

    context.safeStartActivity(chooserIntent)
}

fun Context.safeStartActivity(intent: Intent) {
    try {
        startActivity(intent)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        toast(R.string.error_no_external_application_found)
    }
}

private fun appendTimeToFilename(name: String): String {
    val dateExtension = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
    if (!name.contains(".")) return name + "_" + dateExtension

    val filename = name.substringBeforeLast(".")
    val fileExtension = name.substringAfterLast(".")

    return """${filename}_$dateExtension.$fileExtension"""
}

suspend fun saveMedia(context: Context, file: File, title: String, mediaMimeType: String?, notificationUtils: NotificationUtils) {
    withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val filename = appendTimeToFilename(title)

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, filename)
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, mediaMimeType)
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            }
            val externalContentUri = when {
                mediaMimeType?.isMimeTypeImage() == true -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                mediaMimeType?.isMimeTypeVideo() == true -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                mediaMimeType?.isMimeTypeAudio() == true -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else                                     -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }

            val uri = context.contentResolver.insert(externalContentUri, values)
            if (uri == null) {
                Toast.makeText(context, R.string.error_saving_media_file, Toast.LENGTH_LONG).show()
                throw IllegalStateException(context.getString(R.string.error_saving_media_file))
            } else {
                val source = file.inputStream().source().buffer()
                context.contentResolver.openOutputStream(uri)?.sink()?.buffer()?.let { sink ->
                    source.use { input ->
                        sink.use { output ->
                            output.writeAll(input)
                        }
                    }
                }
                notificationUtils.buildDownloadFileNotification(
                        uri,
                        filename,
                        mediaMimeType ?: MimeTypes.OctetStream
                ).let { notification ->
                    notificationUtils.showNotificationMessage("DL", uri.hashCode(), notification)
                }
            }
        } else {
            saveMediaLegacy(context, mediaMimeType, title, file)
        }
    }
}

@Suppress("DEPRECATION")
private fun saveMediaLegacy(context: Context,
                            mediaMimeType: String?,
                            title: String,
                            file: File) {
    val state = Environment.getExternalStorageState()
    if (Environment.MEDIA_MOUNTED != state) {
        context.toast(context.getString(R.string.error_saving_media_file))
        throw IllegalStateException(context.getString(R.string.error_saving_media_file))
    }

    val dest = when {
        mediaMimeType?.isMimeTypeImage() == true -> Environment.DIRECTORY_PICTURES
        mediaMimeType?.isMimeTypeVideo() == true -> Environment.DIRECTORY_MOVIES
        mediaMimeType?.isMimeTypeAudio() == true -> Environment.DIRECTORY_MUSIC
        else                                     -> Environment.DIRECTORY_DOWNLOADS
    }
    val downloadDir = Environment.getExternalStoragePublicDirectory(dest)
    try {
        val outputFilename = if (title.substringAfterLast('.', "").isEmpty()) {
            val extension = mediaMimeType?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            "$title.$extension"
        } else {
            title
        }
        val savedFile = saveFileIntoLegacy(file, downloadDir, outputFilename)
        if (savedFile != null) {
            val downloadManager = context.getSystemService<DownloadManager>()
            downloadManager?.addCompletedDownload(
                    savedFile.name,
                    title,
                    true,
                    mediaMimeType ?: MimeTypes.OctetStream,
                    savedFile.absolutePath,
                    savedFile.length(),
                    true)
            addToGallery(savedFile, mediaMimeType, context)
        }
    } catch (error: Throwable) {
        context.toast(context.getString(R.string.error_saving_media_file))
        throw error
    }
}

private fun addToGallery(savedFile: File, mediaMimeType: String?, context: Context) {
    
    var mediaConnection: MediaScannerConnection? = null
    val mediaScannerConnectionClient: MediaScannerConnection.MediaScannerConnectionClient = object : MediaScannerConnection.MediaScannerConnectionClient {
        override fun onMediaScannerConnected() {
            mediaConnection?.scanFile(savedFile.path, mediaMimeType)
        }

        override fun onScanCompleted(path: String, uri: Uri?) {
            if (path == savedFile.path) mediaConnection?.disconnect()
        }
    }
    mediaConnection = MediaScannerConnection(context, mediaScannerConnectionClient).apply { connect() }
}


fun openPlayStore(activity: Activity, appId: String = BuildConfig.APPLICATION_ID) {
    try {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")))
    } catch (activityNotFoundException: ActivityNotFoundException) {
        activity.safeStartActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appId")))
    }
}

fun openAppSettingsPage(activity: Activity) {
    activity.safeStartActivity(
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.fromParts("package", activity.packageName, null)
            }
    )
}


fun selectTxtFileToWrite(
        activity: Activity,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        defaultFileName: String,
        chooserHint: String
) {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TITLE, defaultFileName)
    val chooserIntent = Intent.createChooser(intent, chooserHint)
    try {
        activityResultLauncher.launch(chooserIntent)
    } catch (activityNotFoundException: ActivityNotFoundException) {
        activity.toast(R.string.error_no_external_application_found)
    }
}


@Suppress("DEPRECATION")
fun saveFileIntoLegacy(sourceFile: File, dstDirPath: File, outputFilename: String?): File? {
    
    var dstFileName: String

    
    if (null == outputFilename) {
        
        val dotPos = sourceFile.name.lastIndexOf(".")
        var fileExt = ""
        if (dotPos > 0) {
            fileExt = sourceFile.name.substring(dotPos)
        }
        dstFileName = "vector_" + System.currentTimeMillis() + fileExt
    } else {
        dstFileName = outputFilename
    }

    
    dstFileName = dstFileName.replace(Regex("""[/\\]"""), "_")

    var dstFile = File(dstDirPath, dstFileName)

    
    if (dstFile.exists()) {
        var baseFileName = dstFileName
        var fileExt = ""
        val lastDotPos = dstFileName.lastIndexOf(".")
        if (lastDotPos > 0) {
            baseFileName = dstFileName.substring(0, lastDotPos)
            fileExt = dstFileName.substring(lastDotPos)
        }
        var counter = 1
        while (dstFile.exists()) {
            dstFile = File(dstDirPath, "$baseFileName($counter)$fileExt")
            counter++
        }
    }

    
    var inputStream: FileInputStream? = null
    var outputStream: FileOutputStream? = null
    try {
        dstFile.createNewFile()
        inputStream = sourceFile.inputStream()
        outputStream = dstFile.outputStream()
        val buffer = ByteArray(1024 * 10)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            outputStream.write(buffer, 0, len)
        }
        return dstFile
    } catch (failure: Throwable) {
        return null
    } finally {
        
        tryOrNull { inputStream?.close() }
        tryOrNull { outputStream?.close() }
    }
}
