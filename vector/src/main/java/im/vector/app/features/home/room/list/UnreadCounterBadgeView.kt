
package im.vector.app.features.home.room.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.textview.MaterialTextView
import im.vector.app.R

class UnreadCounterBadgeView : MaterialTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun render(state: State) {
        if (state.count == 0) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            val bgRes = if (state.highlighted) {
                R.drawable.bg_unread_highlight
            } else {
                R.drawable.bg_unread_notification
            }
            setBackgroundResource(bgRes)
            text = RoomSummaryFormatter.formatUnreadMessagesCounter(state.count)
        }
    }

    data class State(
            val count: Int,
            val highlighted: Boolean
    )
}
