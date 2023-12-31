

package org.matrix.android.sdk.internal.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

internal fun createBackgroundHandler(name: String): Handler = Handler(
        HandlerThread(name).apply { start() }.looper
)

internal fun createUIHandler(): Handler = Handler(
        Looper.getMainLooper()
)
