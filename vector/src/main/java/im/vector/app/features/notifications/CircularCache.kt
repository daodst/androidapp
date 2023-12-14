

package im.vector.app.features.notifications


class CircularCache<T : Any>(cacheSize: Int, factory: (Int) -> Array<T?>) {

    companion object {
        inline fun <reified T : Any> create(cacheSize: Int) = CircularCache(cacheSize) { Array<T?>(cacheSize) { null } }
    }

    private val cache = factory(cacheSize)
    private var writeIndex = 0

    fun contains(key: T): Boolean = cache.contains(key)

    fun put(key: T) {
        if (writeIndex == cache.size) {
            writeIndex = 0
        }
        cache[writeIndex] = key
        writeIndex++
    }
}
