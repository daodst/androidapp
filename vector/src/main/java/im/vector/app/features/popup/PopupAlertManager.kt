
package im.vector.app.features.popup

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import com.tapadoo.alerter.Alerter
import im.vector.app.R
import im.vector.app.core.utils.isAnimationDisabled
import im.vector.app.features.analytics.ui.consent.AnalyticsOptInActivity
import im.vector.app.features.pin.PinActivity
import im.vector.app.features.signout.hard.SignedOutActivity
import im.vector.app.features.themes.ThemeUtils
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PopupAlertManager @Inject constructor() {

    companion object {
        const val INCOMING_CALL_PRIORITY = Int.MAX_VALUE
    }

    private var weakCurrentActivity: WeakReference<Activity>? = null
    private var currentAlerter: VectorAlert? = null

    private val alertQueue = mutableListOf<VectorAlert>()

    fun hasAlertsToShow(): Boolean {
        return currentAlerter != null || alertQueue.isNotEmpty()
    }

    fun postVectorAlert(alert: VectorAlert) {
        synchronized(alertQueue) {
            alertQueue.add(alert)
        }
        weakCurrentActivity?.get()?.runOnUiThread {
            displayNextIfPossible()
        }
    }

    fun cancelAlert(uid: String) {
        synchronized(alertQueue) {
            alertQueue.listIterator().apply {
                while (this.hasNext()) {
                    val next = this.next()
                    if (next.uid == uid) {
                        this.remove()
                    }
                }
            }
        }

        
        if (currentAlerter?.uid == uid) {
            weakCurrentActivity?.get()?.runOnUiThread {
                Alerter.hide()
                currentIsDismissed()
            }
        }
    }

    
    fun cancelAll() {
        synchronized(alertQueue) {
            alertQueue.clear()
        }

        
        weakCurrentActivity?.get()?.runOnUiThread {
            Alerter.hide()
            currentIsDismissed()
        }
    }

    fun onNewActivityDisplayed(activity: Activity) {
        
        if (currentAlerter != null) {
            weakCurrentActivity?.get()?.let {
                Alerter.clearCurrent(it, null, null)
                if (currentAlerter?.isLight == false) {
                    setLightStatusBar()
                }
            }
        }
        weakCurrentActivity = WeakReference(activity)
        if (!shouldBeDisplayedIn(currentAlerter, activity)) {
            return
        }
        if (currentAlerter != null) {
            if (currentAlerter!!.expirationTimestamp != null && System.currentTimeMillis() > currentAlerter!!.expirationTimestamp!!) {
                
                
                try {
                    currentAlerter?.dismissedAction?.run()
                } catch (e: Exception) {
                    Timber.e("## failed to perform action")
                }
                currentAlerter = null
                Handler(Looper.getMainLooper()).postDelayed({
                    displayNextIfPossible()
                }, 2000)
            } else {
                showAlert(currentAlerter!!, activity, animate = false)
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                displayNextIfPossible()
            }, 2000)
        }
    }

    private fun displayNextIfPossible() {
        val currentActivity = weakCurrentActivity?.get()
        if (Alerter.isShowing || currentActivity == null || currentActivity.isDestroyed) {
            
            return
        }
        val next: VectorAlert?
        synchronized(alertQueue) {
            next = alertQueue.maxByOrNull { it.priority }
            
            
            if (next != null && next.priority > currentAlerter?.priority ?: Int.MIN_VALUE) {
                alertQueue.remove(next)
                currentAlerter?.also {
                    alertQueue.add(0, it)
                }
            } else {
                
                return
            }
        }
        currentAlerter = next
        next?.let {
            if (!shouldBeDisplayedIn(next, currentActivity)) return
            val currentTime = System.currentTimeMillis()
            if (next.expirationTimestamp != null && currentTime > next.expirationTimestamp!!) {
                
                try {
                    next.dismissedAction?.run()
                } catch (e: java.lang.Exception) {
                    Timber.e("## failed to perform action")
                }
                displayNextIfPossible()
            } else {
                showAlert(it, currentActivity)
            }
        }
    }

    private fun clearLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            weakCurrentActivity?.get()
                    
                    ?.takeIf { ThemeUtils.isLightTheme(it) }
                    ?.window?.decorView
                    ?.let { view ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            view.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
                        } else {
                            @Suppress("DEPRECATION")
                            view.systemUiVisibility = view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                        }
                    }
        }
    }

    private fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            weakCurrentActivity?.get()
                    
                    ?.takeIf { ThemeUtils.isLightTheme(it) }
                    ?.window?.decorView
                    ?.let { view ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            view.windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
                        } else {
                            @Suppress("DEPRECATION")
                            view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        }
                    }
        }
    }

    private fun showAlert(alert: VectorAlert, activity: Activity, animate: Boolean = true) {
        if (!alert.isLight) {
            clearLightStatusBar()
        }
        val noAnimation = !animate || isAnimationDisabled(activity)

        alert.weakCurrentActivity = WeakReference(activity)
        val alerter = Alerter.create(activity, alert.layoutRes)

        alerter.setTitle(alert.title)
                .setText(alert.description)
                .also { al ->
                    al.getLayoutContainer()?.also {
                        alert.viewBinder?.bind(it)
                    }
                }
                .apply {
                    if (noAnimation) {
                        setEnterAnimation(R.anim.anim_alerter_no_anim)
                    }

                    alert.iconId?.let {
                        setIcon(it)
                    }
                    alert.actions.forEach { action ->
                        addButton(action.title, R.style.Widget_Vector_Button_Text_Alerter) {
                            if (action.autoClose) {
                                currentIsDismissed()
                                Alerter.hide()
                            }
                            try {
                                action.action.run()
                            } catch (e: java.lang.Exception) {
                                Timber.e("## failed to perform action")
                            }
                        }
                    }
                    setOnClickListener { _ ->
                        alert.contentAction?.let {
                            if (alert.dismissOnClick) {
                                currentIsDismissed()
                                Alerter.hide()
                            }
                            try {
                                it.run()
                            } catch (e: java.lang.Exception) {
                                Timber.e("## failed to perform action")
                            }
                        }
                    }
                }
                .setOnHideListener {
                    
                    try {
                        alert.dismissedAction?.run()
                    } catch (e: java.lang.Exception) {
                        Timber.e("## failed to perform action")
                    }
                    currentIsDismissed()
                }
                .enableSwipeToDismiss()
                .enableInfiniteDuration(true)
                .apply {
                    if (alert.colorInt != null) {
                        setBackgroundColorInt(alert.colorInt!!)
                    } else if (alert.colorAttribute != null) {
                        setBackgroundColorInt(ThemeUtils.getColor(activity, alert.colorAttribute!!))
                    } else if (alert.drawRes != null) {
                        setBackgroundDrawable(alert.drawRes!!)
                    } else {
                        setBackgroundColorRes(alert.colorRes ?: R.color.notification_accent_color)
                    }
                }
                .enableIconPulse(!noAnimation)
                .show()
    }

    private fun currentIsDismissed() {
        
        if (currentAlerter?.isLight == false) {
            setLightStatusBar()
        }
        currentAlerter = null
        Handler(Looper.getMainLooper()).postDelayed({
            displayNextIfPossible()
        }, 500)
    }

    private fun shouldBeDisplayedIn(alert: VectorAlert?, activity: Activity): Boolean {
        Timber.i("====shouldBeDisplayedIn=============${activity.javaClass.name}==")
        val display = "com.app.base.activity.WelcomActivity".equals(activity.javaClass.name)
        return alert != null &&
                !display &&
                activity !is PinActivity &&
                activity !is SignedOutActivity &&
                activity !is AnalyticsOptInActivity &&
                alert.shouldBeDisplayedIn.invoke(activity)
    }
}
