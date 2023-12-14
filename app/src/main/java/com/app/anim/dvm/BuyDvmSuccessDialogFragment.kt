package me.fenfei.anim.dvm

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
import com.app.R
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.onClick
import im.vector.app.databinding.FragmentBuyDvmSuccessDialogBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.fenfei.anim.enter.en.dp2px

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyDvmSuccessDialogFragment : DialogFragment() {

    lateinit var animDrawable: AnimationDrawable
    lateinit var views: FragmentBuyDvmSuccessDialogBinding

    private var param1: String? = null
    private var param2: String? = null

    var listener: ClickListener? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BuyDvmSuccessDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        views.buyDvmSuccessAnimTips3.text =
                "${getString(R.string.buy_dvm_success_anim_tips3)}${param1 ?: ""}"
        views.buyDvmSuccessAnimTips4.text =
                "${getString(R.string.buy_dvm_success_anim_tips4)}${param2 ?: ""}"
        
        val callback = object : Drawable.Callback {
            override fun invalidateDrawable(who: Drawable) {
                views.buyDvmSuccessAnimImg.invalidateDrawable(who)

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
                views.buyDvmSuccessAnimImg.scheduleDrawable(who, what, `when`)
            }

            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                views.buyDvmSuccessAnimImg.unscheduleDrawable(who, what)
            }
        }
        doAnim(callback)
        views.buyDvmSuccessAnimBt.onClick {
            listener?.invoke(it)
            dismissAllowingStateLoss()
        }
    }

    private fun doAnim(callback: Drawable.Callback) {
        lifecycleScope.launch {
            reset()
            
            delay(133)

            
            views.buyDvmSuccessAnimImg.setImageResource(R.drawable.cus_buy_dvm_success_gif_anim);
            animDrawable = views.buyDvmSuccessAnimImg.drawable as AnimationDrawable
            animDrawable.callback = callback
            animDrawable.start()

            
            delay(980)
            
            doAnim(views.buyDvmSuccessAnimTips1, dp2px(40f), 200)
            doAnim(views.buyDvmSuccessAnimTips2, dp2px(40f), 200, 67)
            doAnim(views.buyDvmSuccessAnimTips3, dp2px(40f), 200, 67 + 67)
            doAnim(views.buyDvmSuccessAnimTips4, dp2px(40f), 200, 67 + 67 + 67)
            doAnim(views.buyDvmSuccessAnimBt, 0f, 200, 200 + 67 + 67 + 67)
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
        views.buyDvmSuccessAnimTips1.alpha = 0f
        views.buyDvmSuccessAnimTips2.alpha = 0f
        views.buyDvmSuccessAnimTips3.alpha = 0f
        views.buyDvmSuccessAnimTips4.alpha = 0f
        views.buyDvmSuccessAnimBt.alpha = 0f
    }

    private fun nextAnim() {
        
        views.buyDvmSuccessAnimImg.setImageResource(R.drawable.cus_buy_dvm_success_loop_gif_anim);
        animDrawable = views.buyDvmSuccessAnimImg.drawable as AnimationDrawable
        animDrawable.start()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        views = FragmentBuyDvmSuccessDialogBinding.inflate(inflater, container, false)
        return views.root
    }
}
