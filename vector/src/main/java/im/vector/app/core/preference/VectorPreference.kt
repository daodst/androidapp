

package im.vector.app.core.preference

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.widget.ImageViewCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import im.vector.app.R
import im.vector.app.features.themes.ThemeUtils
import timber.log.Timber


open class VectorPreference : Preference {

    var mTypeface = Typeface.NORMAL

    
    var onPreferenceLongClickListener: OnPreferenceLongClickListener? = null

    
    interface OnPreferenceLongClickListener {
        
        fun onPreferenceLongClick(preference: Preference): Boolean
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        
        isIconSpaceReserved = true
    }

    var isHighlighted = false
        set(value) {
            field = value
            notifyChanged()
        }

    var tintIcon = false
        set(value) {
            field = value
            notifyChanged()
        }

    var currentHighlightAnimator: Animator? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        val itemView = holder.itemView
        addClickListeners(itemView)

        
        try {
            val title = holder.findViewById(android.R.id.title) as? TextView
            val summary = holder.findViewById(android.R.id.summary) as? TextView
            if (title != null) {
                title.isSingleLine = false
                title.setTypeface(null, mTypeface)
            }

            summary?.setTypeface(null, mTypeface)

            if (tintIcon) {
                
                val icon = holder.findViewById(android.R.id.icon) as? ImageView

                icon?.let {
                    val color = ThemeUtils.getColor(context, R.attr.vctr_content_secondary)
                    ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(color))
                }
            }

            
            currentHighlightAnimator?.cancel()
            if (isHighlighted) {
                val colorFrom = Color.TRANSPARENT
                val colorTo = ThemeUtils.getColor(itemView.context, R.attr.colorPrimary)
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
        } catch (e: Exception) {
            Timber.e(e, "onBindView")
        }

        super.onBindViewHolder(holder)
    }

    
    private fun addClickListeners(view: View) {
        view.setOnLongClickListener {
            if (null != onPreferenceLongClickListener) {
                onPreferenceLongClickListener!!.onPreferenceLongClick(this@VectorPreference)
            } else false
        }

        view.setOnClickListener {
            
            onPreferenceClickListener?.onPreferenceClick(this@VectorPreference)
        }
    }
}
