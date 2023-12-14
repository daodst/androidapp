

package org.matrix.android.sdk.api.session.widgets

interface WidgetURLFormatter {
    
    suspend fun format(
            baseUrl: String,
            params: Map<String, String> = emptyMap(),
            forceFetchScalarToken: Boolean = false,
            bypassWhitelist: Boolean
    ): String
}
