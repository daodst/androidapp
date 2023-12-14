

package im.vector.app.features.roomdirectory

data class RoomDirectoryServer(
        val serverName: String,

        
        val isUserServer: Boolean,

        
        val isManuallyAdded: Boolean,

        
        val protocols: List<RoomDirectoryData>
)
