

package im.vector.app.core.extensions

fun <T> Set<T>.toggle(element: T, singleElement: Boolean = false): Set<T> {
    return if (contains(element)) {
        if (singleElement) {
            emptySet()
        } else {
            minus(element)
        }
    } else {
        if (singleElement) {
            setOf(element)
        } else {
            plus(element)
        }
    }
}
