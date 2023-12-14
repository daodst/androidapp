

package org.matrix.android.sdk.api.session.room.model.message

interface MessageContentWithFormattedBody : MessageContent {
    
    val format: String?

    
    val formattedBody: String?

    
    val matrixFormattedBody: String?
        get() = formattedBody?.takeIf { it.isNotBlank() && format == MessageFormat.FORMAT_MATRIX_HTML }
}
