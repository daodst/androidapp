

package im.vector.app.core.utils

import androidx.lifecycle.Observer

open class LiveEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set 

    
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    
    fun peekContent(): T = content
}


class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<LiveEvent<T>> {
    override fun onChanged(event: LiveEvent<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}
