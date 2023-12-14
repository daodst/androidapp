

package org.matrix.android.sdk.internal.network.interceptors

import androidx.annotation.NonNull
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

internal class FormattedJsonHttpLogger : HttpLoggingInterceptor.Logger {

    companion object {
        private const val INDENT_SPACE = 2
    }

    
    @Synchronized
    override fun log(@NonNull message: String) {
        Timber.v(message)

        if (message.startsWith("{")) {
            
            try {
                val o = JSONObject(message)
                logJson(o.toString(INDENT_SPACE))
            } catch (e: JSONException) {
                
                Timber.e(e)
            }
        } else if (message.startsWith("[")) {
            
            try {
                val o = JSONArray(message)
                logJson(o.toString(INDENT_SPACE))
            } catch (e: JSONException) {
                
                Timber.e(e)
            }
        }
        
    }

    private fun logJson(formattedJson: String) {
        formattedJson
                .lines()
                .dropLastWhile { it.isEmpty() }
                .forEach { Timber.v(it) }
    }
}
