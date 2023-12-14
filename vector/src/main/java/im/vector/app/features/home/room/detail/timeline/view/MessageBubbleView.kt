

package im.vector.app.features.home.room.detail.timeline.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.shape.MaterialShapeDrawable
import im.vector.app.R
import im.vector.app.core.resources.LocaleProvider
import im.vector.app.core.resources.getLayoutDirectionFromCurrentLocale
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.databinding.ViewMessageBubbleBinding
import im.vector.app.features.home.room.detail.timeline.style.TimelineMessageLayout
import im.vector.app.features.home.room.detail.timeline.style.shapeAppearanceModel
import im.vector.app.features.themes.ThemeUtils
import timber.log.Timber

class MessageBubbleView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr), TimelineMessageLayoutRenderer {

    private var isIncoming: Boolean = false

    private val horizontalStubPadding = DimensionConverter(resources).dpToPx(12)
    private val verticalStubPadding = DimensionConverter(resources).dpToPx(8)

    private val cusHorizontalStubPadding = DimensionConverter(resources).dpToPx(15)
    private val cusVerticalStubPadding = DimensionConverter(resources).dpToPx(20)

    private lateinit var views: ViewMessageBubbleBinding
    private lateinit var bubbleDrawable: MaterialShapeDrawable
    private lateinit var rippleMaskDrawable: MaterialShapeDrawable

    init {
        inflate(context, R.layout.view_message_bubble, this)
        context.withStyledAttributes(attrs, R.styleable.MessageBubble) {
            isIncoming = getBoolean(R.styleable.MessageBubble_incoming_style, false)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        views = ViewMessageBubbleBinding.bind(this)
        val currentLayoutDirection = LocaleProvider(resources).getLayoutDirectionFromCurrentLocale()

        val layoutDirectionToSet = if (isIncoming) {
            currentLayoutDirection
        } else {
            if (currentLayoutDirection == View.LAYOUT_DIRECTION_LTR) {
                View.LAYOUT_DIRECTION_RTL
            } else {
                View.LAYOUT_DIRECTION_LTR
            }
        }
        views.informationBottom.layoutDirection = layoutDirectionToSet
        views.messageThreadSummaryContainer.layoutDirection = layoutDirectionToSet
        views.bubbleWrapper.layoutDirection = layoutDirectionToSet
        views.bubbleView.layoutDirection = currentLayoutDirection

        bubbleDrawable = MaterialShapeDrawable()
        rippleMaskDrawable = MaterialShapeDrawable()
        DrawableCompat.setTint(rippleMaskDrawable, Color.WHITE)
        views.bubbleView.apply {
            outlineProvider = ViewOutlineProvider.BACKGROUND
            clipToOutline = true
            background = RippleDrawable(
                    ContextCompat.getColorStateList(context, R.color.mtrl_btn_ripple_color) ?: ColorStateList.valueOf(Color.TRANSPARENT),
                    bubbleDrawable,
                    rippleMaskDrawable
            )
        }
    }

    override fun renderMessageLayout(messageLayout: TimelineMessageLayout) {
        (messageLayout as? TimelineMessageLayout.Bubble)
                ?.updateDrawables()
                ?.updateCusDrawables()
                ?.setConstraints()
                ?.toggleMessageOverlay()
                ?.setPadding()
                ?.setSelfPadding()
                ?.setMargins()
                ?.setAdditionalTopSpace()
                ?: Timber.v("Can't render messageLayout $messageLayout")
    }

    private fun TimelineMessageLayout.Bubble.updateCusDrawables() = apply {
        if (!isCus) {
            return@apply
        }
        val shapeAppearanceModel = cornersRadius.shapeAppearanceModel()
        bubbleDrawable.apply {
            this.shapeAppearanceModel = shapeAppearanceModel
            this.fillColor = if (isPseudoBubble) {
                ColorStateList.valueOf(Color.TRANSPARENT)
            } else {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            }
        }
        rippleMaskDrawable.shapeAppearanceModel = shapeAppearanceModel
    }

    private fun TimelineMessageLayout.Bubble.updateDrawables() = apply {
        if (isCus) {
            return@apply
        }
        val shapeAppearanceModel = cornersRadius.shapeAppearanceModel()
        bubbleDrawable.apply {
            this.shapeAppearanceModel = shapeAppearanceModel
            this.fillColor = if (isPseudoBubble) {
                ColorStateList.valueOf(Color.TRANSPARENT)
            } else {
                if (izGroup) {
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.timeline_item_bg))
                } else {
                    val backgroundColorAttr = if (isIncoming) R.attr.vctr_message_bubble_inbound else R.attr.vctr_message_bubble_outbound
                    val backgroundColor = ThemeUtils.getColor(context, backgroundColorAttr)
                    ColorStateList.valueOf(backgroundColor)
                }
            }
        }
        rippleMaskDrawable.shapeAppearanceModel = shapeAppearanceModel
    }

    private fun TimelineMessageLayout.Bubble.setConstraints() = apply {
        ConstraintSet().apply {
            clone(views.bubbleView)
            clear(R.id.viewStubContainer, ConstraintSet.END)
            if (timestampInsideMessage) {
                connect(R.id.viewStubContainer, ConstraintSet.END, R.id.parent, ConstraintSet.END, 0)
            } else {
                connect(R.id.viewStubContainer, ConstraintSet.END, R.id.messageTimeView, ConstraintSet.START, 0)
            }
            applyTo(views.bubbleView)
        }
    }

    private fun TimelineMessageLayout.Bubble.toggleMessageOverlay() = apply {
        if (addMessageOverlay) {
            val timeColor = ContextCompat.getColor(context, R.color.palette_white)
            views.messageTimeView.setTextColor(timeColor)
            views.messageOverlayView.isVisible = true
            (views.messageOverlayView.background as? GradientDrawable)?.cornerRadii = cornersRadius.toFloatArray()
        } else {
            val timeColor = ThemeUtils.getColor(context, R.attr.vctr_content_tertiary)
            views.messageTimeView.setTextColor(timeColor)
            views.messageOverlayView.isVisible = false
        }
    }

    private fun TimelineMessageLayout.Bubble.setPadding() = apply {
        if (isPseudoBubble && timestampInsideMessage) {
            views.viewStubContainer.root.setPadding(0, 0, 0, 0)
        } else if (isCus) {
            views.viewStubContainer.root.setPadding(cusHorizontalStubPadding, cusVerticalStubPadding, cusHorizontalStubPadding, verticalStubPadding)
        } else {
            views.viewStubContainer.root.setPadding(horizontalStubPadding, verticalStubPadding, horizontalStubPadding, verticalStubPadding)
        }

        if (isCus) {
            setPadding(0, resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_top), 0, 0)
        } else {
            setPadding(0, 0, 0, 0)
        }
    }

    
    private fun TimelineMessageLayout.Bubble.setSelfPadding() = apply {
        if (isCus) {
            setPadding(0, resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_top), 0, 0)
        } else {
            setPadding(0, 0, 0, 0)
        }
    }

    private fun TimelineMessageLayout.Bubble.setMargins() = apply {
        if (isIncoming) {
            views.messageEndGuideline.updateLayoutParams<LayoutParams> {
                if (isCus) {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_start)
                } else {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_end)
                }
            }
            views.messageStartGuideline.updateLayoutParams<LayoutParams> {
                marginStart = resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_start)
            }
        } else {
            views.messageEndGuideline.updateLayoutParams<LayoutParams> {
                if (isCus) {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_start)
                } else {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_end)
                }
            }
            views.messageStartGuideline.updateLayoutParams<LayoutParams> {
                marginStart = resources.getDimensionPixelSize(R.dimen.chat_bubble_margin_start)
            }
        }
    }

    private fun TimelineMessageLayout.Bubble.setAdditionalTopSpace() = apply {
        views.additionalTopSpace.isVisible = addTopMargin
    }

    private fun TimelineMessageLayout.Bubble.CornersRadius.toFloatArray(): FloatArray {
        return floatArrayOf(topStartRadius, topStartRadius, topEndRadius, topEndRadius, bottomEndRadius, bottomEndRadius, bottomStartRadius, bottomStartRadius)
    }
}
