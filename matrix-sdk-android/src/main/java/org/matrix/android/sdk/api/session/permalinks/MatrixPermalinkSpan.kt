

package org.matrix.android.sdk.api.session.permalinks

import android.text.style.ClickableSpan
import android.view.View
import org.matrix.android.sdk.api.session.permalinks.MatrixPermalinkSpan.Callback


class MatrixPermalinkSpan(private val url: String,
                          private val callback: Callback? = null) : ClickableSpan() {

    interface Callback {
        fun onUrlClicked(url: String)
    }

    override fun onClick(widget: View) {
        callback?.onUrlClicked(url)
    }
}
