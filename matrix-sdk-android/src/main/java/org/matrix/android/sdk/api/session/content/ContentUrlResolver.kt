

package org.matrix.android.sdk.api.session.content

import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt


interface ContentUrlResolver {

    enum class ThumbnailMethod(val value: String) {
        CROP("crop"),
        SCALE("scale")
    }

    
    val uploadUrl: String

    
    fun resolveFullSize(contentUrl: String?): String?

    
    fun resolveForDownload(contentUrl: String?, elementToDecrypt: ElementToDecrypt? = null): ResolvedMethod?

    
    fun resolveThumbnail(contentUrl: String?, width: Int, height: Int, method: ThumbnailMethod): String?

    sealed class ResolvedMethod {
        data class GET(val url: String) : ResolvedMethod()
        data class POST(val url: String, val jsonBody: String) : ResolvedMethod()
    }
}
