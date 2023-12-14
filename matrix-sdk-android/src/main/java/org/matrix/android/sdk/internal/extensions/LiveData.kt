

package org.matrix.android.sdk.internal.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

internal inline fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    this.observe(owner) { it?.run(observer) }
}
