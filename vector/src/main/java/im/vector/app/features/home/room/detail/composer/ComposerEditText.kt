

package im.vector.app.features.home.room.detail.composer

import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.OnReceiveContentListener
import androidx.core.view.ViewCompat
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import im.vector.app.core.extensions.ooi
import im.vector.app.core.platform.SimpleTextWatcher
import im.vector.app.features.html.PillImageSpan
import timber.log.Timber

class ComposerEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    interface Callback {
        fun onRichContentSelected(contentUri: Uri): Boolean
        fun onTextChanged(text: CharSequence)
    }

    var callback: Callback? = null

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection? {
        var ic = super.onCreateInputConnection(editorInfo) ?: return null
        val mimeTypes = ViewCompat.getOnReceiveContentMimeTypes(this) ?: arrayOf("image/*")

        EditorInfoCompat.setContentMimeTypes(editorInfo, mimeTypes)
        ic = InputConnectionCompat.createWrapper(this, ic, editorInfo)

        val onReceiveContentListener = OnReceiveContentListener { _, payload ->
            val split = payload.partition { item -> item.uri != null }
            val uriContent = split.first
            val remaining = split.second

            if (uriContent != null) {
                val clip: ClipData = uriContent.clip
                for (i in 0 until clip.itemCount) {
                    val uri = clip.getItemAt(i).uri
                    
                    callback?.onRichContentSelected(uri)
                }
            }
            
            
            
            
            remaining
        }

        ViewCompat.setOnReceiveContentListener(this, mimeTypes, onReceiveContentListener)

        return ic
    }

    init {
        addTextChangedListener(
                object : SimpleTextWatcher() {
                    var spanToRemove: PillImageSpan? = null

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        Timber.v("Pills: beforeTextChanged: start:$start count:$count after:$after")

                        if (count > after) {
                            
                            val deleteCharPosition = start + count
                            Timber.v("Pills: beforeTextChanged: deleted char at $deleteCharPosition")

                            
                            spanToRemove = editableText.getSpans(deleteCharPosition, deleteCharPosition, PillImageSpan::class.java)
                                    .ooi { Timber.v("Pills: beforeTextChanged: found ${it.size} span(s)") }
                                    .firstOrNull()
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                        if (spanToRemove != null) {
                            val start = editableText.getSpanStart(spanToRemove)
                            val end = editableText.getSpanEnd(spanToRemove)
                            Timber.v("Pills: afterTextChanged Removing the span start:$start end:$end")
                            
                            editableText.removeSpan(spanToRemove)
                            if (start != -1 && end != -1) {
                                editableText.replace(start, end, "")
                            }
                            spanToRemove = null
                        }
                        callback?.onTextChanged(s.toString())
                    }
                }
        )
    }
}
