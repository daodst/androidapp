

package im.vector.app.core.utils

import java.util.Timer
import java.util.TimerTask

const val THREE_MINUTES = 3 * 60_000L


open class TemporaryStore<T>(private val delay: Long = THREE_MINUTES) {

    private var timer: Timer? = null

    var data: T? = null
        set(value) {
            timer?.cancel()
            field = value
            if (value != null) {
                timer = Timer().also {
                    it.schedule(object : TimerTask() {
                        override fun run() {
                            field = null
                        }
                    }, delay)
                }
            }
        }
}
