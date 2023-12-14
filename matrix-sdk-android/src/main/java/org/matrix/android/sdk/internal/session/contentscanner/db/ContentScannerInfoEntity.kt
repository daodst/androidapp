

package org.matrix.android.sdk.internal.session.contentscanner.db

import io.realm.RealmObject

internal open class ContentScannerInfoEntity(
        var serverUrl: String? = null,
        var enabled: Boolean? = null
) : RealmObject() {

    companion object
}
