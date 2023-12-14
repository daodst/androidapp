
package im.vector.app.features.reactions.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.EmojiSpanify
import im.vector.app.R
import im.vector.app.core.utils.TextUtils
import im.vector.app.databinding.ReactionButtonBinding
import javax.inject.Inject


@AndroidEntryPoint
class ReactionButton @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0,
                                               defStyleRes: Int = R.style.TimelineReactionView) :
        LinearLayout(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener, View.OnLongClickListener {

    @Inject lateinit var emojiSpanify: EmojiSpanify

    private val views: ReactionButtonBinding

    var reactedListener: ReactedListener? = null

    var reactionCount = 11
        set(value) {
            field = value
            views.reactionCount.text = TextUtils.formatCountToShortDecimal(value)
        }

    var reactionString = "😀"
        set(value) {
            field = value
            
            val emojiSpanned = emojiSpanify.spanify(value)
            views.reactionText.text = emojiSpanned
        }

    private var isChecked: Boolean = false
    private var onDrawable: Drawable? = null
    private var offDrawable: Drawable? = null

    init {
        inflate(context, R.layout.reaction_button, this)
        orientation = HORIZONTAL
        layoutDirection = View.LAYOUT_DIRECTION_LOCALE
        views = ReactionButtonBinding.bind(this)
        views.reactionCount.text = TextUtils.formatCountToShortDecimal(reactionCount)
        context.withStyledAttributes(attrs, R.styleable.ReactionButton, defStyleAttr) {
            onDrawable = ContextCompat.getDrawable(context, R.drawable.reaction_rounded_rect_shape)
            offDrawable = ContextCompat.getDrawable(context, R.drawable.reaction_rounded_rect_shape_off)
            getString(R.styleable.ReactionButton_emoji)?.let {
                reactionString = it
            }
            reactionCount = getInt(R.styleable.ReactionButton_reaction_count, 0)
            val status = getBoolean(R.styleable.ReactionButton_toggled, false)
            setChecked(status)
        }

        setOnClickListener(this)
        setOnLongClickListener(this)
    }

    
    override fun onClick(v: View) {
        if (!isEnabled) {
            return
        }
        isChecked = !isChecked
        
        background = if (isChecked) onDrawable else offDrawable

        if (isChecked) {
            reactedListener?.onReacted(this)
            views.reactionText.animate().cancel()
            views.reactionText.scaleX = 0f
            views.reactionText.scaleY = 0f
        } else {
            reactedListener?.onUnReacted(this)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        reactedListener?.onLongClick(this)
        return reactedListener != null
    }

    
    fun setChecked(status: Boolean?) {
        if (status!!) {
            isChecked = true
            background = onDrawable
        } else {
            isChecked = false
            background = offDrawable
        }
    }

    interface ReactedListener {
        fun onReacted(reactionButton: ReactionButton)
        fun onUnReacted(reactionButton: ReactionButton)
        fun onLongClick(reactionButton: ReactionButton)
    }
}
