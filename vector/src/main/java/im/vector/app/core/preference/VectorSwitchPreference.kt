

package im.vector.app.core.preference

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import im.vector.app.R
import im.vector.app.features.themes.ThemeUtils


class VectorSwitchPreference : SwitchPreference {

    
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    init {
        
        isIconSpaceReserved = true
    }

    var isHighlighted = false
        set(value) {
            field = value
            notifyChanged()
        }

    var currentHighlightAnimator: Animator? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        
        (holder.findViewById(android.R.id.title) as? TextView)?.isSingleLine = false

        
        currentHighlightAnimator?.cancel()

        val itemView = holder.itemView
        if (isHighlighted) {
            val colorFrom = Color.TRANSPARENT
            val colorTo = ThemeUtils.getColor(itemView.context, R.attr.colorControlHighlight)
            currentHighlightAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
                duration = 250 
                addUpdateListener { animator ->
                    itemView.setBackgroundColor(animator.animatedValue as Int)
                }
                doOnEnd {
                    currentHighlightAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom).apply {
                        duration = 250 
                        addUpdateListener { animator ->
                            itemView.setBackgroundColor(animator.animatedValue as Int)
                        }
                        doOnEnd {
                            isHighlighted = false
                        }
                        start()
                    }
                }
                startDelay = 200
                start()
            }
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        super.onBindViewHolder(holder)
    }
}
