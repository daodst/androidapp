

package im.vector.lib.multipicker

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher


abstract class Picker<T> {

    protected var single = false

    
    abstract fun getSelectedFiles(context: Context, data: Intent?): List<T>

    
    fun getIncomingFiles(context: Context, data: Intent?): List<T> {
        if (data == null) return emptyList()

        val uriList = mutableListOf<Uri>()
        if (data.action == Intent.ACTION_SEND) {
            (data.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri)?.let { uriList.add(it) }
        } else if (data.action == Intent.ACTION_SEND_MULTIPLE) {
            val extraUriList: List<Uri>? = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
            extraUriList?.let { uriList.addAll(it) }
        }

        val resInfoList: List<ResolveInfo> = context.packageManager.queryIntentActivities(data, PackageManager.MATCH_DEFAULT_ONLY)
        uriList.forEach {
            for (resolveInfo in resInfoList) {
                val packageName: String = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        return getSelectedFiles(context, data)
    }

    
    fun single(): Picker<T> {
        single = true
        return this
    }

    abstract fun createIntent(): Intent

    
    fun startWith(activityResultLauncher: ActivityResultLauncher<Intent>) {
        activityResultLauncher.launch(createIntent().apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) })
    }

    protected fun getSelectedUriList(data: Intent?): List<Uri> {
        val selectedUriList = mutableListOf<Uri>()
        val dataUri = data?.data
        val clipData = data?.clipData

        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                selectedUriList.add(clipData.getItemAt(i).uri)
            }
        } else if (dataUri != null) {
            selectedUriList.add(dataUri)
        } else {
            data?.extras?.get(Intent.EXTRA_STREAM)?.let {
                (it as? List<*>)?.filterIsInstance<Uri>()?.let { uriList ->
                    selectedUriList.addAll(uriList)
                }
                if (it is Uri) {
                    selectedUriList.add(it)
                }
            }
        }
        return selectedUriList
    }
}
