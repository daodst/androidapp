

package im.vector.app.features.rageshake

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import im.vector.app.core.di.DefaultSharedPreferences
import im.vector.app.core.resources.VersionCodeProvider
import im.vector.app.features.version.VersionProvider
import org.matrix.android.sdk.api.Matrix
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VectorUncaughtExceptionHandler @Inject constructor(
        context: Context,
        private val bugReporter: BugReporter,
        private val versionProvider: VersionProvider,
        private val versionCodeProvider: VersionCodeProvider
) : Thread.UncaughtExceptionHandler {

    
    companion object {
        private const val PREFS_CRASH_KEY = "PREFS_CRASH_KEY"
    }

    private var previousHandler: Thread.UncaughtExceptionHandler? = null

    private val preferences = DefaultSharedPreferences.getInstance(context)

    
    fun activate() {
        previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Timber.v("Uncaught exception: $throwable")
        preferences.edit(commit = true) {
            putBoolean(PREFS_CRASH_KEY, true)
        }
        val b = StringBuilder()
        val appName = "Element" 

        b.append(appName + " Build : " + versionCodeProvider.getVersionCode() + "\n")
        b.append("$appName Version : ${versionProvider.getVersion(longFormat = true, useBuildNumber = true)}\n")
        b.append("SDK Version : ${Matrix.getSdkVersion()}\n")
        b.append("Phone : " + Build.MODEL.trim() + " (" + Build.VERSION.INCREMENTAL + " " + Build.VERSION.RELEASE + " " + Build.VERSION.CODENAME + ")\n")

        b.append("Memory statuses \n")

        var freeSize = 0L
        var totalSize = 0L
        var usedSize = -1L
        try {
            val info = Runtime.getRuntime()
            freeSize = info.freeMemory()
            totalSize = info.totalMemory()
            usedSize = totalSize - freeSize
        } catch (e: Exception) {
            e.printStackTrace()
        }

        b.append("usedSize   " + usedSize / 1048576L + " MB\n")
        b.append("freeSize   " + freeSize / 1048576L + " MB\n")
        b.append("totalSize   " + totalSize / 1048576L + " MB\n")

        b.append("Thread: ")
        b.append(thread.name)

        b.append(", Exception: ")

        val sw = StringWriter()
        val pw = PrintWriter(sw, true)
        throwable.printStackTrace(pw)
        b.append(sw.buffer.toString())

        val bugDescription = b.toString()
        Timber.e("FATAL EXCEPTION $bugDescription")

        bugReporter.saveCrashReport(bugDescription)

        
        previousHandler?.uncaughtException(thread, throwable)
    }

    
    fun didAppCrash(): Boolean {
        return preferences.getBoolean(PREFS_CRASH_KEY, false)
    }

    
    fun clearAppCrashStatus() {
        preferences.edit {
            remove(PREFS_CRASH_KEY)
        }
    }
}
