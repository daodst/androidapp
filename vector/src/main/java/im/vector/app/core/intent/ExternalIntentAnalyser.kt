

package im.vector.app.core.intent

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import androidx.core.util.PatternsCompat.WEB_URL


sealed class ExternalIntentData {
    
    data class IntentDataText(
            val text: CharSequence? = null,
            val htmlText: String? = null,
            val format: String? = null,
            val clipDataItem: ClipData.Item = ClipData.Item(text, htmlText),
            val mimeType: String? = if (null == htmlText) ClipDescription.MIMETYPE_TEXT_PLAIN else format
    ) : ExternalIntentData()

    
    data class IntentDataClipData(
            val clipDataItem: ClipData.Item,
            val mimeType: String?
    ) : ExternalIntentData()

    
    data class IntentDataUri(
            val uri: Uri,
            val filename: String? = null
    ) : ExternalIntentData()
}

fun analyseIntent(intent: Intent): List<ExternalIntentData> {
    val externalIntentDataList = ArrayList<ExternalIntentData>()

    
    
    if (intent.type == ClipDescription.MIMETYPE_TEXT_PLAIN) {
        var message: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
                ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()

        val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT)

        if (!subject.isNullOrEmpty()) {
            if (message.isNullOrEmpty()) {
                message = subject
            } else if (WEB_URL.matcher(message).matches()) {
                message = subject + "\n" + message
            }
        }

        if (!message.isNullOrEmpty()) {
            externalIntentDataList.add(ExternalIntentData.IntentDataText(message, null, intent.type))
            return externalIntentDataList
        }
    }

    val clipData: ClipData? = intent.clipData
    var mimeTypes: List<String>? = null

    
    if (null != clipData) {
        if (null != clipData.description) {
            if (0 != clipData.description.mimeTypeCount) {
                mimeTypes = with(clipData.description) {
                    List(mimeTypeCount) { getMimeType(it) }
                }

                
                if (1 == mimeTypes.size) {
                    if (mimeTypes[0].endsWith("/*")) {
                        mimeTypes = null
                    }
                }
            }
        }

        for (i in 0 until clipData.itemCount) {
            val item = clipData.getItemAt(i)
            val mimeType = mimeTypes?.getOrElse(i) { mimeTypes[0] }
                    
                    .takeUnless { it == ClipDescription.MIMETYPE_TEXT_URILIST }

            externalIntentDataList.add(ExternalIntentData.IntentDataClipData(item, mimeType))
        }
    } else if (null != intent.data) {
        externalIntentDataList.add(ExternalIntentData.IntentDataUri(intent.data!!))
    }

    return externalIntentDataList
}
