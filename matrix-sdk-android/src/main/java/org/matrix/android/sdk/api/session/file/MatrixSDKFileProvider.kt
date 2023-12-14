

package org.matrix.android.sdk.api.session.file

import android.net.Uri
import androidx.core.content.FileProvider


class MatrixSDKFileProvider : FileProvider() {
    override fun getType(uri: Uri): String? {
        return super.getType(uri) ?: "plain/text"
    }
}
