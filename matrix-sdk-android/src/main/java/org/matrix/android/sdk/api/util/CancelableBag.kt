

package org.matrix.android.sdk.api.util

class CancelableBag : Cancelable, MutableList<Cancelable> by ArrayList() {
    override fun cancel() {
        forEach { it.cancel() }
        clear()
    }
}
