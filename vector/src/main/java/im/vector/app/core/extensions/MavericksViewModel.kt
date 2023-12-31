

package im.vector.app.core.extensions

import androidx.activity.ComponentActivity
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelProvider

inline fun <reified VM : MavericksViewModel<S>, reified S : MavericksState> ComponentActivity.lazyViewModel(): Lazy<VM> {
    return lazy(mode = LazyThreadSafetyMode.NONE) {
        MavericksViewModelProvider.get(
                viewModelClass = VM::class.java,
                stateClass = S::class.java,
                viewModelContext = ActivityViewModelContext(this, intent.extras?.get(Mavericks.KEY_ARG)),
                key = VM::class.java.name
        )
    }
}
