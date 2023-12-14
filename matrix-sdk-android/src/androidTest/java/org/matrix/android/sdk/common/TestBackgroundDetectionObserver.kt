

package org.matrix.android.sdk.common

import org.matrix.android.sdk.internal.util.BackgroundDetectionObserver


internal class TestBackgroundDetectionObserver : BackgroundDetectionObserver {

    override val isInBackground: Boolean = false

    override fun register(listener: BackgroundDetectionObserver.Listener) = Unit

    override fun unregister(listener: BackgroundDetectionObserver.Listener) = Unit
}
