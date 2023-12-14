

package im.vector.app.core.platform

import androidx.recyclerview.widget.ListUpdateCallback

interface DefaultListUpdateCallback : ListUpdateCallback {

    override fun onChanged(position: Int, count: Int, tag: Any?) {
        
    }

    override fun onMoved(position: Int, count: Int) {
        
    }

    override fun onInserted(position: Int, count: Int) {
        
    }

    override fun onRemoved(position: Int, count: Int) {
        
    }
}
