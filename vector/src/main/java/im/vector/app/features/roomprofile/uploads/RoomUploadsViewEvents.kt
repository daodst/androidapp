

package im.vector.app.features.roomprofile.uploads

import im.vector.app.core.platform.VectorViewEvents
import java.io.File

sealed class RoomUploadsViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : RoomUploadsViewEvents()

    data class FileReadyForSharing(val file: File) : RoomUploadsViewEvents()
    data class FileReadyForSaving(val file: File, val title: String) : RoomUploadsViewEvents()
}
