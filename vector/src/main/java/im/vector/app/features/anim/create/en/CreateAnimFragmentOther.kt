package me.fenfei.anim.create.en

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import im.vector.app.databinding.FragmentCreateAnimOtherBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.fenfei.anim.enter.en.dp2px

class CreateAnimFragmentOther : DialogFragment() {


    lateinit var animDrawable: AnimationDrawable

    lateinit var views: FragmentCreateAnimOtherBinding

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
        views = FragmentCreateAnimOtherBinding.inflate(inflater, container, false)

        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val callback = object : Drawable.Callback {
            override fun invalidateDrawable(who: Drawable) {
                views.createAnimImg.invalidateDrawable(who)

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
                views.createAnimImg.scheduleDrawable(who, what, `when`)
            }

            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                views.createAnimImg.unscheduleDrawable(who, what)
            }
        }
        
        doAnim(callback)
        
        views.createAnimBt.setOnClickListener {
            listener?.invoke(it)
            dismissAllowingStateLoss()
        }

    }

    private fun doAnim(callback: Drawable.Callback) {
        lifecycleScope.launch {
            reset()
            
            delay(133)

            views.createAnimImg.setImageResource(R.drawable.cus_create_gif_anim);
            animDrawable = views.createAnimImg.drawable as AnimationDrawable
            animDrawable.callback = callback
            animDrawable.start()

            
            delay(980)
            
            doAnim(views.createAnimTips1,dp2px(40f), 200)
            doAnim(views.createAnimTips2, dp2px(40f), 200, 67)
            doAnim(views.createAnimBottom, dp2px(40f), 200, 67 + 67)
            doAnim(views.createAnimBt, 0f, 200, 200 + 67 + 67)
        }

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

    private fun reset() {
        views.createAnimTips1.alpha = 0f
        views.createAnimTips2.alpha = 0f
        views.createAnimBottom.alpha = 0f
        views.createAnimBt.alpha = 0f
    }

    private fun nextAnim() {
        
        views.createAnimImg.setImageResource(R.drawable.cus_create_gif_loop_anim);
        animDrawable = views.createAnimImg.drawable as AnimationDrawable
        animDrawable.start()
    }

}
