

package im.vector.app.features.usercode

import com.airbnb.mvrx.MavericksState
import org.matrix.android.sdk.api.util.MatrixItem

data class UserCodeState(
        val userId: String,
        val matrixItem: MatrixItem? = null,
        val shareLink: String? = null,
        val mode: Mode = Mode.SHOW
) : MavericksState {
    sealed class Mode {
        object SHOW : Mode()
        object SCAN : Mode()
        data class RESULT(val matrixItem: MatrixItem, val rawLink: String) : Mode()
    }

    constructor(args: UserCodeActivity.Args) : this(
            userId = args.userId,
            mode = if (args.mode == 1) Mode.SCAN else Mode.SHOW
    )
}
