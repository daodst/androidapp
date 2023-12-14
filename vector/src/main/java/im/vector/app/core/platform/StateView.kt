

package im.vector.app.core.platform

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import im.vector.app.R
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.updateConstraintSet
import im.vector.app.databinding.ViewStateBinding

class StateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        FrameLayout(context, attrs, defStyle) {

    sealed class State {
        object Content : State()
        object Loading : State()
        object CusEmpty : State()
        data class Empty(
                val title: CharSequence? = null,
                val image: Drawable? = null,
                val isBigImage: Boolean = false,
                val message: CharSequence? = null
        ) : State()

        data class Error(val message: CharSequence? = null) : State()
    }

    private val views: ViewStateBinding

    var eventCallback: EventCallback? = null
    var groupCallback: CreateGroupEventCallback? = null

    var contentView: View? = null

    var state: State = State.Empty()
        set(newState) {
            if (newState != state) {
                update(newState)
            }
        }

    interface CreateGroupEventCallback {
        fun onClicked()
    }

    interface EventCallback {
        fun onRetryClicked()
    }

    init {
        inflate(context, R.layout.view_state, this)
        views = ViewStateBinding.bind(this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        views.errorRetryView.setOnClickListener {
            eventCallback?.onRetryClicked()
        }
        views.roomListGroupEmptyBt.onClick {
            groupCallback?.onClicked()
        }
        state = State.Content
    }

    private fun update(newState: State) {
        views.progressBar.isVisible = newState is State.Loading
        views.errorView.isVisible = newState is State.Error
        views.emptyView.isVisible = newState is State.Empty
        views.groupEmpty.isVisible = newState is State.CusEmpty
        contentView?.isVisible = newState is State.Content

        when (newState) {
            is State.Content  -> Unit
            is State.CusEmpty -> Unit
            is State.Loading  -> Unit
            is State.Empty    -> {
                views.emptyImageView.setImageDrawable(newState.image)
                views.emptyView.updateConstraintSet {
                    it.constrainPercentHeight(R.id.emptyImageView, if (newState.isBigImage) 0.5f else 0.1f)
                }
                views.emptyMessageView.text = newState.message
                views.emptyTitleView.text = newState.title
            }
            is State.Error    -> {
                views.errorMessageView.text = newState.message
            }
        }
    }
}
