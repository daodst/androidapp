

package org.matrix.android.sdk.internal.util

import androidx.core.text.HtmlCompat

internal fun String.unescapeHtml(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}
