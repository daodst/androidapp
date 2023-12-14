

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class PreviewUrlCacheEntity(
        @PrimaryKey
        var url: String = "",

        var urlFromServer: String? = null,
        var siteName: String? = null,
        var title: String? = null,
        var description: String? = null,
        var mxcUrl: String? = null,
        var imageWidth: Int? = null,
        var imageHeight: Int? = null,
        var lastUpdatedTimestamp: Long = 0L
) : RealmObject() {

    companion object
}
