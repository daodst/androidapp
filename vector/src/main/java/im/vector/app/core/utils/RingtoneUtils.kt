

package im.vector.app.core.utils

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.content.edit
import im.vector.app.core.di.DefaultSharedPreferences
import im.vector.app.features.settings.VectorPreferences




fun getCallRingtoneUri(context: Context): Uri? {
    val callRingtone: String? = DefaultSharedPreferences.getInstance(context)
            .getString(VectorPreferences.SETTINGS_CALL_RINGTONE_URI_PREFERENCE_KEY, null)

    callRingtone?.let {
        return Uri.parse(it)
    }

    return try {
        
        RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
    } catch (e: SecurityException) {
        
        null
    }
}


fun getCallRingtone(context: Context): Ringtone? {
    getCallRingtoneUri(context)?.let {
        
        return RingtoneManager.getRingtone(context, it)
    }

    return null
}


fun getCallRingtoneName(context: Context): String? {
    return getCallRingtone(context)?.getTitle(context)
}


fun setCallRingtoneUri(context: Context, ringtoneUri: Uri) {
    DefaultSharedPreferences.getInstance(context)
            .edit {
                putString(VectorPreferences.SETTINGS_CALL_RINGTONE_URI_PREFERENCE_KEY, ringtoneUri.toString())
            }
}


fun useRiotDefaultRingtone(context: Context): Boolean {
    return DefaultSharedPreferences.getInstance(context).getBoolean(VectorPreferences.SETTINGS_CALL_RINGTONE_USE_RIOT_PREFERENCE_KEY, true)
}


fun setUseRiotDefaultRingtone(context: Context, useRiotDefault: Boolean) {
    DefaultSharedPreferences.getInstance(context)
            .edit {
                putBoolean(VectorPreferences.SETTINGS_CALL_RINGTONE_USE_RIOT_PREFERENCE_KEY, useRiotDefault)
            }
}
