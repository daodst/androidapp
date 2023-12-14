

package org.matrix.android.sdk.internal.util

import android.os.Handler

internal class Debouncer(private val handler: Handler) {

    private val runnables = HashMap<String, Runnable>()

    fun debounce(identifier: String, r: Runnable, millis: Long): Boolean {
        
        runnables[identifier]?.let { runnable -> handler.removeCallbacks(runnable) }

        insertRunnable(identifier, r, millis)
        return true
    }

    private fun insertRunnable(identifier: String, r: Runnable, millis: Long) {
        val chained = Runnable {
            handler.post(r)
            runnables.remove(identifier)
        }
        runnables[identifier] = chained
        handler.postDelayed(chained, millis)
    }
}
