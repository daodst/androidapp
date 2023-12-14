

package im.vector.app.core.epoxy

import android.view.View
import android.widget.TextView
import im.vector.app.core.utils.DebouncedClickListener


typealias ClickListener = (View) -> Unit

fun View.onClick(listener: ClickListener?) {
    if (listener == null) {
        setOnClickListener(null)
    } else {
        setOnClickListener(DebouncedClickListener(listener))
    }
}

fun TextView.onLongClickIgnoringLinks(listener: View.OnLongClickListener?) {
    if (listener == null) {
        setOnLongClickListener(null)
    } else {
        setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                if (hasLongPressedLink()) {
                    return false
                }
                return listener.onLongClick(v)
            }

            
            private fun hasLongPressedLink() = selectionStart != -1 || selectionEnd != -1
        })
    }
}


typealias TextListener = (String) -> Unit
