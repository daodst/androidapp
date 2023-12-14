

package org.matrix.android.sdk.internal.session.widgets

import android.content.Context
import timber.log.Timber
import javax.inject.Inject

internal class WidgetPostMessageAPIProvider @Inject constructor(private val context: Context) {

    private var postMessageAPIString: String? = null

    fun get(): String? {
        if (postMessageAPIString == null) {
            postMessageAPIString = readFromAsset(context)
        }
        return postMessageAPIString
    }

    private fun readFromAsset(context: Context): String? {
        return try {
            context.assets.open("postMessageAPI.js").bufferedReader().use {
                it.readText()
            }
        } catch (failure: Throwable) {
            Timber.e(failure, "Reading postMessageAPI.js asset failed")
            null
        }
    }
}
