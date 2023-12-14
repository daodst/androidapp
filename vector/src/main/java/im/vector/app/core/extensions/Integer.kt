

package im.vector.app.core.extensions

fun Int.incrementByOneAndWrap(max: Int, min: Int = 0): Int {
    val incrementedValue = this + 1
    return if (incrementedValue > max) {
        min
    } else {
        incrementedValue
    }
}
