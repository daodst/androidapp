
package im.vector.app.features.home.room.detail.timeline.item

import android.view.View
import android.view.ViewStub
import android.widget.RelativeLayout
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import im.vector.app.R
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.platform.CheckableView


abstract class BaseEventItem<H : BaseEventItem.BaseHolder> : VectorEpoxyModel<H>(), ItemWithEvents {

    
    @EpoxyAttribute
    var highlighted: Boolean = false

    @EpoxyAttribute
    open var leftGuideline: Int = 0

    final override fun getViewType(): Int {
        
        val pairingResult = pairingFunction(layout.toLong(), getViewStubId().toLong())
        return (pairingResult - Int.MAX_VALUE).toInt()
    }

    abstract fun getViewStubId(): Int

    
    private fun pairingFunction(a: Long, b: Long): Long {
        return if (a >= b) a * a + a + b else a + b * b
    }

    @CallSuper
    override fun bind(holder: H) {
        super.bind(holder)
        holder.leftGuideline.updateLayoutParams<RelativeLayout.LayoutParams> {
            this.marginStart = leftGuideline
        }
        holder.checkableBackground.isChecked = highlighted
    }

    abstract class BaseHolder(@IdRes val stubId: Int) : VectorEpoxyHolder() {
        val leftGuideline by bind<View>(R.id.messageStartGuideline)
        val contentContainer by bind<View>(R.id.viewStubContainer)
        val checkableBackground by bind<CheckableView>(R.id.messageSelectedBackground)

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            inflateStub()
        }

        private fun inflateStub() {
            view.findViewById<ViewStub>(stubId).inflate()
        }
    }
}
