

package im.vector.app.core.resources

import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.annotation.NonNull
import javax.inject.Inject

class StringArrayProvider @Inject constructor(private val resources: Resources) {

    
    @NonNull
    fun getStringArray(@ArrayRes resId: Int): Array<String> {
        return resources.getStringArray(resId)
    }
}
