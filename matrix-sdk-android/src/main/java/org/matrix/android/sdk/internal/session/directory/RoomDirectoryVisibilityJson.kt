

package org.matrix.android.sdk.internal.session.directory

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility

@JsonClass(generateAdapter = true)
internal data class RoomDirectoryVisibilityJson(
        
        @Json(name = "visibility") val visibility: RoomDirectoryVisibility
)
