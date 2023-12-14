

package im.vector.app.features.roomdirectory


data class RoomDirectoryData(
        
        val homeServer: String? = null,

        
        val displayName: String = MATRIX_PROTOCOL_NAME,

        
        val avatarUrl: String? = null,

        
        val thirdPartyInstanceId: String? = null,

        
        val includeAllNetworks: Boolean = false
) {

    companion object {
        const val MATRIX_PROTOCOL_NAME = "Matrix"
    }
}
