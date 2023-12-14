
package im.vector.app.push.fcm

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.core.content.edit
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.di.DefaultSharedPreferences
import im.vector.app.core.pushers.PushersManager
import im.vector.app.features.settings.VectorPreferences
import timber.log.Timber


object FcmHelper {
    private val PREFS_KEY_FCM_TOKEN = "FCM_TOKEN"

    fun isPushSupported(): Boolean = true

    
    fun getFcmToken(context: Context): String? {
        return DefaultSharedPreferences.getInstance(context).getString(PREFS_KEY_FCM_TOKEN, null)
    }

    
    fun storeFcmToken(context: Context,
                      token: String?) {
        DefaultSharedPreferences.getInstance(context).edit {
            putString(PREFS_KEY_FCM_TOKEN, token)
        }
    }

    
    fun ensureFcmTokenIsRetrieved(activity: Activity, pushersManager: PushersManager, registerPusher: Boolean) {
        
        
        if (checkPlayServices(activity)) {
            try {
                FirebaseMessaging.getInstance().token
                        .addOnSuccessListener { token ->
                            storeFcmToken(activity, token)
                            if (registerPusher) {
                                pushersManager.enqueueRegisterPusherWithFcmKey(token)
                            }
                        }
                        .addOnFailureListener { e ->
                            Timber.e(e, "## ensureFcmTokenIsRetrieved() : failed")
                        }
            } catch (e: Throwable) {
                Timber.e(e, "## ensureFcmTokenIsRetrieved() : failed")
            }
        } else {
            Timber.e("No valid Google Play Services found. Cannot use FCM.")
        }
    }

    
    private fun checkPlayServices(activity: Activity): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        return resultCode == ConnectionResult.SUCCESS
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEnterForeground(context: Context, activeSessionHolder: ActiveSessionHolder) {
        
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEnterBackground(context: Context, vectorPreferences: VectorPreferences, activeSessionHolder: ActiveSessionHolder) {
        
    }
}
