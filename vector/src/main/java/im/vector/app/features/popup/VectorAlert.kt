

package im.vector.app.features.popup

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import im.vector.app.R
import java.lang.ref.WeakReference

interface VectorAlert {
    val uid: String
    val title: String
    val description: String
    val iconId: Int?
    val priority: Int
    val dismissOnClick: Boolean
    val isLight: Boolean
    val shouldBeDisplayedIn: ((Activity) -> Boolean)

    data class Button(val title: String, val action: Runnable, val autoClose: Boolean)

    
    var weakCurrentActivity: WeakReference<Activity>?

    val actions: MutableList<Button>

    var contentAction: Runnable?
    var dismissedAction: Runnable?

    
    var expirationTimestamp: Long?

    fun addButton(title: String, action: Runnable, autoClose: Boolean = true) {
        actions.add(Button(title, action, autoClose))
    }

    var viewBinder: ViewBinder?

    val layoutRes: Int

    var colorRes: Int?

    var colorInt: Int?

    var colorAttribute: Int?

    var drawRes: Drawable?

    interface ViewBinder {
        fun bind(view: View)
    }
}


open class DefaultVectorAlert(
        override val uid: String,
        override val title: String,
        override val description: String,
        @DrawableRes override val iconId: Int?,
        
        override val shouldBeDisplayedIn: ((Activity) -> Boolean) = { true }
) : VectorAlert {

    
    override var weakCurrentActivity: WeakReference<Activity>? = null

    override val actions = ArrayList<VectorAlert.Button>()

    override var contentAction: Runnable? = null
    override var dismissedAction: Runnable? = null

    
    override var expirationTimestamp: Long? = null

    @LayoutRes
    override val layoutRes = R.layout.alerter_alert_default_layout

    override val dismissOnClick: Boolean = true

    override val priority: Int = 0

    override val isLight: Boolean = false

    @ColorRes
    override var colorRes: Int? = null

    @ColorInt
    override var colorInt: Int? = null

    override var drawRes: Drawable? = null

    @AttrRes
    override var colorAttribute: Int? = null

    override var viewBinder: VectorAlert.ViewBinder? = null
}
