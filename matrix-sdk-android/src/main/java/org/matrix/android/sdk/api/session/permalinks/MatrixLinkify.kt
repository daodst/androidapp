

package org.matrix.android.sdk.api.session.permalinks

import android.text.Spannable
import org.matrix.android.sdk.api.MatrixPatterns


object MatrixLinkify {

    
    @Suppress("UNUSED_PARAMETER")
    fun addLinks(spannable: Spannable, callback: MatrixPermalinkSpan.Callback?): Boolean {
        

        
        if (spannable.isEmpty()) {
            return false
        }
        val text = spannable.toString()
        var hasMatch = false
        for (pattern in MatrixPatterns.MATRIX_PATTERNS) {
            for (match in pattern.findAll(spannable)) {
                hasMatch = true
                val startPos = match.range.first
                if (startPos == 0 || text[startPos - 1] != '/') {
                    val endPos = match.range.last + 1
                    var url = text.substring(match.range)
                    if (MatrixPatterns.isUserId(url) ||
                            MatrixPatterns.isRoomAlias(url) ||
                            MatrixPatterns.isRoomId(url) ||
                            MatrixPatterns.isGroupId(url) ||
                            MatrixPatterns.isEventId(url)) {
                        url = PermalinkService.MATRIX_TO_URL_BASE + url
                    }
                    val span = MatrixPermalinkSpan(url, callback)
                    spannable.setSpan(span, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        return hasMatch

    }
}
