

package org.matrix.android.sdk.internal.session.content

import java.io.File

internal sealed class VideoCompressionResult {
    data class Success(val compressedFile: File) : VideoCompressionResult()
    object CompressionNotNeeded : VideoCompressionResult()
    object CompressionCancelled : VideoCompressionResult()
    data class CompressionFailed(val failure: Throwable) : VideoCompressionResult()
}
