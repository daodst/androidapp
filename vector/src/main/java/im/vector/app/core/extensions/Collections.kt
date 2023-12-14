

package im.vector.app.core.extensions

inline fun <reified T> List<T>.nextOrNull(index: Int) = getOrNull(index + 1)
inline fun <reified T> List<T>.prevOrNull(index: Int) = getOrNull(index - 1)

fun <T> List<T>.containsAllItems(vararg items: T) = this.containsAll(items.toList())
