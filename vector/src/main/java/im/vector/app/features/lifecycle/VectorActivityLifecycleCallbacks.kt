

package im.vector.app.features.lifecycle

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.getSystemService
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.vector.app.features.popup.PopupAlertManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class VectorActivityLifecycleCallbacks constructor(private val popupAlertManager: PopupAlertManager) : Application.ActivityLifecycleCallbacks {
    
    private var activitiesInfo: Array<ActivityInfo> = emptyArray()

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        popupAlertManager.onNewActivityDisplayed(activity)
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        
        coroutineScope.launch {
            val isTaskCorrupted = try {
                isTaskCorrupted(activity)
            } catch (failure: Throwable) {
                when (failure) {
                    
                    is IllegalArgumentException             -> {
                        Timber.e("The task was not found: ${failure.localizedMessage}")
                        false
                    }
                    is PackageManager.NameNotFoundException -> {
                        Timber.e("Package manager error: ${failure.localizedMessage}")
                        true
                    }
                    else                                    -> throw failure
                }
            }

            if (isTaskCorrupted) {
                Timber.e("ActivityLifecyc Application is potentially corrupted by an unknown activity")
                MainActivity.restartApp(activity, MainActivityArgs())
                return@launch
            }
        }
    }

    
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private suspend fun isTaskCorrupted(activity: Activity): Boolean = withContext(Dispatchers.Default) {
        val context = activity.applicationContext
        val packageManager: PackageManager = context.packageManager

        
        if (activitiesInfo.isEmpty()) {
            activitiesInfo = packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES).activities
        }

        
        
        val manager = context.getSystemService<ActivityManager>() ?: return@withContext false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            
            
            
            
            
            manager.appTasks.any { appTask ->
                appTask.taskInfo.topActivity?.let { isPotentialMaliciousActivity(it) } ?: false
            }
        } else {
            
            
            
            
            manager.getRunningTasks(10).any { runningTaskInfo ->
                runningTaskInfo.topActivity?.let {
                    
                    
                    if (packageManager.getActivityInfo(it, 0).taskAffinity == context.applicationInfo.taskAffinity) {
                        isPotentialMaliciousActivity(it)
                    } else false
                } ?: false
            }
        }
    }

    
    private fun isPotentialMaliciousActivity(activity: ComponentName): Boolean = activitiesInfo.none {
        it.name == activity.className }
}
