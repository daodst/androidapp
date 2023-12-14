

package im.vector.lib.core.utils.compat

import android.os.Build

fun <E> MutableCollection<E>.removeIfCompat(predicate: (E) -> Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        removeIf(predicate)
    } else {
        removeAll(filter(predicate).toSet())
    }
}
