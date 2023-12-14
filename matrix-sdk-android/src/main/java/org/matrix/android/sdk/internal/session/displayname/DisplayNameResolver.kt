

package org.matrix.android.sdk.internal.session.displayname

import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject

internal class DisplayNameResolver @Inject constructor(
        private val matrixConfiguration: MatrixConfiguration
) {
    fun getBestName(matrixItem: MatrixItem): String {
        return if (matrixItem is MatrixItem.GroupItem || matrixItem is MatrixItem.RoomAliasItem) {
            
            matrixItem.id
        } else {
            matrixItem.displayName?.takeIf { it.isNotBlank() }
                    ?: matrixConfiguration.matrixItemDisplayNameFallbackProvider?.getDefaultName(matrixItem)
                    ?: matrixItem.id
        }
    }
}
