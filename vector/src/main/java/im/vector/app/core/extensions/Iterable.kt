

package im.vector.app.core.extensions


inline fun <T, R : Comparable<R>> Iterable<T>.lastMinBy(selector: (T) -> R): T? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var minElem = iterator.next()
    var minValue = selector(minElem)
    while (iterator.hasNext()) {
        val e = iterator.next()
        val v = selector(e)
        if (minValue >= v) {
            minElem = e
            minValue = v
        }
    }
    return minElem
}


inline fun <T> Collection<T>.join(each: (Int, T) -> Unit, between: (Int, T) -> Unit) {
    val lastIndex = size - 1
    forEachIndexed { idx, t ->
        each(idx, t)

        if (idx != lastIndex) {
            between(idx, t)
        }
    }
}
