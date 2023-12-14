

package org.matrix.android.sdk.internal.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

internal interface BackgroundDetectionObserver : DefaultLifecycleObserver {
    val isInBackground: Boolean

    fun register(listener: Listener)
    fun unregister(listener: Listener)

    interface Listener {
        fun onMoveToForeground()
        fun onMoveToBackground()
    }
}

internal class DefaultBackgroundDetectionObserver : BackgroundDetectionObserver {

    override var isInBackground: Boolean = true
        private set

    private val listeners = LinkedHashSet<BackgroundDetectionObserver.Listener>()

    override fun register(listener: BackgroundDetectionObserver.Listener) {
        listeners.add(listener)
    }

    override fun unregister(listener: BackgroundDetectionObserver.Listener) {
        listeners.remove(listener)
    }

    override fun onStart(owner: LifecycleOwner) {
        Timber.v("App returning to foreground…")
        isInBackground = false
        listeners.forEach { it.onMoveToForeground() }
    }

    override fun onStop(owner: LifecycleOwner) {
        Timber.v("App going to background…")
        isInBackground = true
        listeners.forEach { it.onMoveToBackground() }
    }
}
