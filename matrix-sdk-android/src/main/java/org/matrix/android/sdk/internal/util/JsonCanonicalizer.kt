

package org.matrix.android.sdk.internal.util

import androidx.annotation.VisibleForTesting
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.matrix.android.sdk.internal.di.MoshiProvider
import timber.log.Timber
import java.util.TreeSet


internal object JsonCanonicalizer {

    fun <T> getCanonicalJson(type: Class<T>, o: T): String {
        val adapter = MoshiProvider.providesMoshi().adapter<T>(type)

        
        return canonicalize(adapter.toJson(o))
                .replace("\\/", "/")
    }

    @VisibleForTesting
    fun canonicalize(jsonString: String): String {
        return try {
            val jsonObject = JSONObject(jsonString)

            canonicalizeRecursive(jsonObject)
        } catch (e: JSONException) {
            Timber.e(e, "Unable to canonicalize")
            jsonString
        }
    }

    
    private fun canonicalizeRecursive(any: Any): String {
        when (any) {
            is JSONArray  -> {
                
                return (0 until any.length()).joinToString(separator = ",", prefix = "[", postfix = "]") {
                    canonicalizeRecursive(any.get(it))
                }
            }
            is JSONObject -> {
                

                val attributes = TreeSet<String>()
                for (entry in any.keys()) {
                    attributes.add(entry)
                }

                return buildString {
                    append("{")
                    for ((index, value) in attributes.withIndex()) {
                        append("\"")
                        append(value)
                        append("\"")
                        append(":")
                        append(canonicalizeRecursive(any[value]))

                        if (index < attributes.size - 1) {
                            append(",")
                        }
                    }
                    append("}")
                }
            }
            is String     -> return JSONObject.quote(any)
            else          -> return any.toString()
        }
    }
}
