

package im.vector.app.core.utils

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.appbar.MaterialToolbar
import im.vector.app.R


class ToolbarConfig(val activity: AppCompatActivity, val toolbar: MaterialToolbar) {
    private var customBackResId: Int? = null

    fun setup() = apply {
        activity.setSupportActionBar(toolbar)
    }

    
    var title: CharSequence?
        set(value) {
            setTitle(value)
        }
        get() = activity.supportActionBar?.title

    
    var subtitle: CharSequence?
        set(value) {
            setSubtitle(value)
        }
        get() = activity.supportActionBar?.subtitle

    
    fun setTitle(title: CharSequence?) = apply { activity.supportActionBar?.title = title }

    
    fun setTitle(@StringRes titleRes: Int) = apply { activity.supportActionBar?.setTitle(titleRes) }

    
    fun setSubtitle(subtitle: CharSequence?) = apply { activity.supportActionBar?.subtitle = subtitle }

    
    fun setSubtitle(@StringRes subtitleRes: Int) = apply { activity.supportActionBar?.setSubtitle(subtitleRes) }

    
    fun allowBack(isAllowed: Boolean = true, useCross: Boolean = false) = apply {
        activity.supportActionBar?.let {
            it.setDisplayShowHomeEnabled(isAllowed)
            it.setDisplayHomeAsUpEnabled(isAllowed)
            if (isAllowed && useCross) {
                val navResId = customBackResId ?: R.drawable.ic_x_18dp
                toolbar.navigationIcon = AppCompatResources.getDrawable(activity, navResId)
            }
        }
    }
}
