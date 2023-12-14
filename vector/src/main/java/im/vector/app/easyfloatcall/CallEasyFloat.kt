package im.vector.app.easyfloatcall

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import im.vector.app.easyfloatcall.floatingview.EnFloatingView
import im.vector.app.easyfloatcall.floatingview.FloatingMagnetView
import im.vector.app.easyfloatcall.floatingview.FloatingView
import im.vector.app.easyfloatcall.floatingview.MagnetViewListener

object CallEasyFloat : Application.ActivityLifecycleCallbacks {
    private var mLayoutParams = getFloatingLayoutParams()
    private val blackList = mutableListOf<Class<*>>()
    private var mLayout: Int = 0

    
    private var onRemoveListener: ((FloatingMagnetView) -> Unit)? = null
    private var onClickListener: ((FloatingMagnetView) -> Unit)? = null
    private var dragEnable = true
    private var autoMoveToEdge = true

    fun layout(layout: Int): CallEasyFloat {
        mLayout = layout
        return this
    }

    fun layoutParams(layoutParams: FrameLayout.LayoutParams): CallEasyFloat {
        mLayoutParams = layoutParams
        return this
    }

    fun blackList(blackList: MutableList<Class<*>>): CallEasyFloat {
        CallEasyFloat.blackList.addAll(blackList)
        return this
    }

    fun listener(
            onRemoveListener: ((View?) -> Unit)? = null,
            onClickListener: ((View) -> Unit)? = null
    ): CallEasyFloat {
        this.onRemoveListener = onRemoveListener
        this.onClickListener = onClickListener
        return this
    }

    
    fun dragEnable(dragEnable: Boolean): CallEasyFloat {
        this.dragEnable = dragEnable
        FloatingView.get().view?.updateDragState(dragEnable)
        return this
    }

    fun isDragEnable(): Boolean {
        return dragEnable
    }

    
    fun setAutoMoveToEdge(autoMoveToEdge: Boolean): CallEasyFloat {
        this.autoMoveToEdge = autoMoveToEdge
        FloatingView.get().view?.setAutoMoveToEdge(autoMoveToEdge)
        return this
    }

    fun isAutoMoveToEdge(): Boolean {
        return autoMoveToEdge
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (isActivityInValid(activity)) {
            return
        }
        initShow(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        if (isActivityInValid(activity)) {
            return
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        if (isActivityInValid(activity)) {
            return
        }
        FloatingView.get().detach(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    private fun isActivityInValid(activity: Activity): Boolean {
        return blackList.contains(activity::class.java)
    }

    private fun getFloatingLayoutParams(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.START
        params.setMargins(0, params.topMargin, params.rightMargin, 500)
        return params
    }

    private fun initShow(activity: Activity) {
        activity.let {
            if (FloatingView.get().view == null) {
                FloatingView.get().customView(
                        EnFloatingView(activity, mLayout)
                )
            }
            FloatingView.get().run {
                layoutParams(mLayoutParams)
                attach(it)
                dragEnable(dragEnable)
                this.listener(object : MagnetViewListener {
                    override fun onRemove(magnetView: FloatingMagnetView) {
                        onRemoveListener?.invoke(magnetView)
                    }

                    override fun onClick(magnetView: FloatingMagnetView) {
                        onClickListener?.invoke(magnetView)
                    }
                })
            }
        }
    }

    fun setIsVisible(isVisible: Boolean) {
        FloatingView.get().view?.isVisible = isVisible
    }

    fun show(activity: Activity) {
        initShow(activity)
        activity.application.registerActivityLifecycleCallbacks(this)
    }

    fun dismiss(activity: Activity) {
        FloatingView.get().remove()
        FloatingView.get().detach(activity)
        activity.application.unregisterActivityLifecycleCallbacks(this)
    }
}
