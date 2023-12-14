

package im.vector.app.core.resources

import android.content.res.Resources
import androidx.annotation.NonNull
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import javax.inject.Inject

class StringProvider @Inject constructor( val resources: Resources) {

    
    @NonNull
    fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    
    @NonNull
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return resources.getString(resId, *formatArgs)
    }

    @NonNull
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any?): String {
        return resources.getQuantityString(resId, quantity, *formatArgs)
    }
}
