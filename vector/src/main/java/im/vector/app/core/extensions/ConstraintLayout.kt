

package im.vector.app.core.extensions

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import androidx.core.view.doOnLayout
import kotlin.math.roundToInt

fun ConstraintLayout.updateConstraintSet(block: (ConstraintSet) -> Unit) {
    ConstraintSet().let {
        it.clone(this)
        block.invoke(it)
        it.applyTo(this)
    }
}


fun ConstraintLayout.realignPercentagesToParent() {
    doOnLayout {
        val rootHeight = (parent as View).height
        children.forEach { child ->
            val params = child.layoutParams as ConstraintLayout.LayoutParams
            if (params.matchConstraintPercentHeight != 1.0f) {
                params.height = (rootHeight * params.matchConstraintPercentHeight).roundToInt()
                child.layoutParams = params
            }
        }
    }
}
