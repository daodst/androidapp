

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo

@JsonClass(generateAdapter = true)
data class ImageInfo(
        
        @Json(name = "mimetype") val mimeType: String?,

        
        @Json(name = "w") val width: Int = 0,

        
        @Json(name = "h") val height: Int = 0,

        
        @Json(name = "size") val size: Long = 0,

        
        @Json(name = "thumbnail_info") val thumbnailInfo: ThumbnailInfo? = null,

        
        @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,

        
        @Json(name = "thumbnail_file") val thumbnailFile: EncryptedFileInfo? = null
)


fun ImageInfo.getThumbnailUrl(): String? {
    return thumbnailFile?.url ?: thumbnailUrl
}
