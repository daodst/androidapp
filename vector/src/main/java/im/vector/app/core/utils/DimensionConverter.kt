
package im.vector.app.core.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.Px
import javax.inject.Inject

class DimensionConverter @Inject constructor(val resources: Resources) {

    @Px
    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics
        ).toInt()
    }

    @Px
    fun spToPx(sp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp.toFloat(),
                resources.displayMetrics
        ).toInt()
    }

    fun pxToDp(@Px px: Int): Int {
        return (px.toFloat() / resources.displayMetrics.density).toInt()
    }
}
