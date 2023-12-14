

package im.vector.app.features.attachments.preview

import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.util.MimeTypes
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeImage


fun ContentAttachmentData.isEditable(): Boolean {
    return type == ContentAttachmentData.Type.IMAGE &&
            getSafeMimeType()?.isMimeTypeImage() == true &&
            getSafeMimeType() != MimeTypes.Gif
}
