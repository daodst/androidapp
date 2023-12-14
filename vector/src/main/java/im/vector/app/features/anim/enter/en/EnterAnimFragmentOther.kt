package me.fenfei.anim.enter.en

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import im.vector.app.R
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.databinding.FragmentEnterAnimOtherBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun dp2px(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
}

class EnterAnimFragmentOther : DialogFragment() {

    lateinit var animDrawable: AnimationDrawable

    lateinit var views: FragmentEnterAnimOtherBinding

    var listener: ClickListener? = null
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        views = FragmentEnterAnimOtherBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val callback = object : Drawable.Callback {
            override fun invalidateDrawable(who: Drawable) {
                views.enterAnimImg.invalidateDrawable(who)

                if (animDrawable.getFrame(animDrawable.numberOfFrames - 1) == animDrawable.current && animDrawable.isOneShot && animDrawable.isRunning && animDrawable.isVisible) {
                    val lastFrameDuration =
                            animDrawable.getDuration(animDrawable.numberOfFrames - 1)
                    
                    lifecycleScope.launch {
                        delay(lastFrameDuration.toLong())
                        nextAnim()
                    }
                }
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                views.enterAnimImg.scheduleDrawable(who, what, `when`)
            }

            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                views.enterAnimImg.unscheduleDrawable(who, what)
            }
        }

        doAnim(callback)
        views.enterAnimBt.setOnClickListener {
            listener?.invoke(it)
            dismissAllowingStateLoss()
        }
    }

    private fun doAnim(callback: Drawable.Callback) {
        lifecycleScope.launch {
            reset()
            
            delay(133)

            views.enterAnimImg.setImageResource(R.drawable.cus_join_gif_anim);
            animDrawable = views.enterAnimImg.drawable as AnimationDrawable
            animDrawable.callback = callback
            animDrawable.start()

            
            delay(1002)
            
            doAnim(views.enterAnimTips1, dp2px(40f), 200)
            doAnim(views.enterAnimTips2, dp2px(40f), 200, 67)
            doAnim(views.enterAnimStep, dp2px(40f), 200, 67 + 67)
            doAnim(views.enterAnimTips3, dp2px(40f), 200, 67 + 67 + 67)
            
            doAnim(views.enterAnimBt, 0f, 200, 200 + 67 + 67 + 67)

        }
    }

    
    private fun reset() {
        views.enterAnimTips1.alpha = 0f
        views.enterAnimTips2.alpha = 0f
        views.enterAnimStep.alpha = 0f
        views.enterAnimTips3.alpha = 0f
        views.enterAnimBt.alpha = 0f
    }

    private fun nextAnim() {
        
        views.enterAnimImg.setImageResource(R.drawable.cus_join_gif_loop_anim);
        animDrawable = views.enterAnimImg.getDrawable() as AnimationDrawable
        animDrawable.start()
    }

    fun doAnim(target: View, offset: Float, duration: Long, startDelay: Long = 0) {
        val translationY: ObjectAnimator =
                ObjectAnimator.ofFloat(target, "translationY", offset, 0f)
        
        val accelerateDecelerateInterpolator: Interpolator = AccelerateDecelerateInterpolator()
        
        translationY.interpolator = accelerateDecelerateInterpolator
        val alpha = ObjectAnimator.ofFloat(target, "alpha", 0f, 1f)
        
        val set = AnimatorSet()
        
        set.playTogether(translationY, alpha)
        set.duration = duration
        set.startDelay = startDelay
        set.start()
    }
}
