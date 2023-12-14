

package org.matrix.android.sdk.api.util

import org.matrix.android.sdk.api.extensions.orFalse

object MimeTypes {
    const val Any: String = "*/*"
    const val OctetStream = "application/octet-stream"
    const val Apk = "application/vnd.android.package-archive"

    const val Images = "image/*"

    const val Png = "image/png"
    const val BadJpg = "image/jpg"
    const val Jpeg = "image/jpeg"
    const val Gif = "image/gif"

    const val Ogg = "audio/ogg"

    fun String?.normalizeMimeType() = if (this == BadJpg) Jpeg else this

    fun String?.isMimeTypeImage() = this?.startsWith("image/").orFalse()
    fun String?.isMimeTypeVideo() = this?.startsWith("video/").orFalse()
    fun String?.isMimeTypeAudio() = this?.startsWith("audio/").orFalse()
}
