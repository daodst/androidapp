

package im.vector.app.features.displayname

import org.matrix.android.sdk.api.util.MatrixItem

fun MatrixItem.getBestName(): String {
    
    return if (this is MatrixItem.GroupItem || this is MatrixItem.RoomAliasItem) {
        
        id
    } else {
        displayName
                ?.takeIf { it.isNotBlank() }
                ?: VectorMatrixItemDisplayNameFallbackProvider.getDefaultName(this)
    }
}
