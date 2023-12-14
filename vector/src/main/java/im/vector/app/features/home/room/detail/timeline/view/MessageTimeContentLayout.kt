

package im.vector.app.features.home.room.detail.timeline.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import im.vector.app.core.resources.LocaleProvider
import im.vector.app.core.resources.getLayoutDirectionFromCurrentLocale

class MessageTimeContentLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var messageTextView: TextView? = null

    private lateinit var timeView: View
    private var localeLayoutDirection: Int = View.LAYOUT_DIRECTION_LOCALE

    private val timeViewMeasuredWidthWithMargins: Int
        get() = timeView.measuredWidth + timeView.marginStart + timeView.marginEnd

    override fun onFinishInflate() {
        super.onFinishInflate()
        timeView = findViewWithTag("messageTimeView")
        messageTextView = findViewWithTag("messageTextView")

        localeLayoutDirection = LocaleProvider(resources).getLayoutDirectionFromCurrentLocale()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val messageTextView = this.messageTextView
        
        if (messageTextView != null) {
            val width: Int
            val height: Int
            val textLineCount = messageTextView.lineCount
            val maxContentWidth = (messageTextView.layoutParams as LayoutParams).matchConstraintMaxWidth
            val lastLineWidth = if (textLineCount != 0) messageTextView.layout.getLineWidth(textLineCount - 1) else 0f
            if (textLineCount == 1 && messageTextView.measuredWidth + timeViewMeasuredWidthWithMargins < maxContentWidth) {
                width = messageTextView.measuredWidth + timeViewMeasuredWidthWithMargins
                height = messageTextView.measuredHeight
            } else if (textLineCount > 1 && lastLineWidth + timeViewMeasuredWidthWithMargins
                    < messageTextView.measuredWidth - messageTextView.paddingEnd) {
                width = messageTextView.measuredWidth
                height = messageTextView.measuredHeight
            } else {
                width = messageTextView.measuredWidth
                height = messageTextView.measuredHeight + timeView.measuredHeight
            }
            setMeasuredDimension(width, height)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        
        if (messageTextView != null) {
            val textView = messageTextView!!
            val parentLeft: Int = paddingLeft
            val parentRight: Int = right - left - paddingRight
            val parentTop: Int = paddingTop
            val parentBottom: Int = bottom - top - paddingBottom
            if (localeLayoutDirection == LAYOUT_DIRECTION_RTL) {
                val contentLeft = parentRight - textView.measuredWidth - textView.marginEnd
                textView.layout(
                        contentLeft,
                        parentTop + textView.marginTop,
                        parentRight - textView.marginEnd,
                        parentTop + textView.marginTop + textView.measuredHeight
                )
                timeView.layout(
                        parentLeft + timeView.marginEnd,
                        parentBottom - timeView.measuredHeight - timeView.marginBottom,
                        parentLeft + timeView.measuredWidth + timeView.marginEnd,
                        parentBottom - timeView.marginBottom
                )
            } else {
                textView.layout(
                        parentLeft,
                        parentTop,
                        parentLeft + textView.measuredWidth,
                        parentTop + textView.measuredHeight
                )
                timeView.layout(
                        parentRight - timeView.measuredWidth - timeView.marginEnd,
                        parentBottom - timeView.measuredHeight - timeView.marginBottom,
                        parentRight - timeView.marginEnd,
                        parentBottom - timeView.marginBottom
                )
            }
        } else {
            super.onLayout(changed, left, top, right, bottom)
        }
    }
}
