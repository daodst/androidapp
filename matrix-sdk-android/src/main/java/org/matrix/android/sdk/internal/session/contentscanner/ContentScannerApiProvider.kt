

package org.matrix.android.sdk.internal.session.contentscanner

import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class ContentScannerApiProvider @Inject constructor() {
    var contentScannerApi: ContentScannerApi? = null
}
