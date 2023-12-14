

package im.vector.app.core.epoxy

import androidx.annotation.CallSuper
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.VisibilityState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren


abstract class VectorEpoxyModel<H : VectorEpoxyHolder> : EpoxyModelWithHolder<H>() {

    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var onModelVisibilityStateChangedListener: OnVisibilityStateChangedListener? = null

    @CallSuper
    override fun bind(holder: H) {
        super.bind(holder)
    }

    @CallSuper
    override fun unbind(holder: H) {
        coroutineScope.coroutineContext.cancelChildren()
        super.unbind(holder)
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: H) {
        onModelVisibilityStateChangedListener?.onVisibilityStateChanged(visibilityState)
        super.onVisibilityStateChanged(visibilityState, view)
    }

    fun setOnVisibilityStateChanged(listener: OnVisibilityStateChangedListener): VectorEpoxyModel<H> {
        this.onModelVisibilityStateChangedListener = listener
        return this
    }

    interface OnVisibilityStateChangedListener {
        fun onVisibilityStateChanged(@VisibilityState.Visibility visibilityState: Int)
    }
}
