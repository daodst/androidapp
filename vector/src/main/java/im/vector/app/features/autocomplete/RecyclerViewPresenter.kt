
package im.vector.app.features.autocomplete

import android.content.Context
import android.database.DataSetObserver
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otaliastudios.autocomplete.AutocompletePresenter

abstract class RecyclerViewPresenter<T>(context: Context?) : AutocompletePresenter<T>(context) {

    private var recyclerView: RecyclerView? = null
    private var clicks: ClickProvider<T>? = null
    private var observer: RecyclerView.AdapterDataObserver? = null

    override fun registerClickProvider(provider: ClickProvider<T>) {
        clicks = provider
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        this.observer = Observer(observer)
    }

    @CallSuper
    override fun getView(): ViewGroup {
        val adapter = instantiateAdapter()
        observer?.also {
            adapter.registerAdapterDataObserver(it)
        }
        return RecyclerView(context).apply {
            this.adapter = adapter
            this.layoutManager = instantiateLayoutManager()
            this.itemAnimator = null
        }
    }

    override fun onViewShown() {}

    @CallSuper
    override fun onViewHidden() {
        observer?.also {
            recyclerView?.adapter?.unregisterAdapterDataObserver(it)
        }
        recyclerView = null
        observer = null
    }

    
    protected fun dispatchClick(item: T) {
        if (clicks != null) clicks?.click(item)
    }

    
    protected fun dispatchLayoutChange() {
        if (observer != null) observer!!.onChanged()
    }

    
    protected abstract fun instantiateAdapter(): RecyclerView.Adapter<*>

    
    protected fun instantiateLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private class Observer constructor(private val root: DataSetObserver) : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            root.onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            root.onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            root.onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            root.onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            root.onChanged()
        }
    }
}
