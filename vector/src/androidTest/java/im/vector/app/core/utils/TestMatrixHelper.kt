

package im.vector.app.core.utils

import androidx.test.platform.app.InstrumentationRegistry
import im.vector.app.features.room.VectorRoomDisplayNameFallbackProvider
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration

fun getMatrixInstance(): Matrix {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val configuration = MatrixConfiguration(
            roomDisplayNameFallbackProvider = VectorRoomDisplayNameFallbackProvider(context)
    )
    return Matrix.createInstance(context, configuration)
}
