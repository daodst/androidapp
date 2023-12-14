

package im.vector.app.features.displayname

import org.matrix.android.sdk.api.MatrixItemDisplayNameFallbackProvider
import org.matrix.android.sdk.api.util.MatrixItem

object VectorMatrixItemDisplayNameFallbackProvider : MatrixItemDisplayNameFallbackProvider {
    override fun getDefaultName(matrixItem: MatrixItem): String {
        
        return matrixItem.id
    }
}
