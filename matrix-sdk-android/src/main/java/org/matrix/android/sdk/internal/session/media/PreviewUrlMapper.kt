

package org.matrix.android.sdk.internal.session.media

import org.matrix.android.sdk.api.session.media.PreviewUrlData
import org.matrix.android.sdk.internal.database.model.PreviewUrlCacheEntity


internal fun PreviewUrlCacheEntity.toDomain() = PreviewUrlData(
        url = urlFromServer ?: url,
        siteName = siteName,
        title = title,
        description = description,
        mxcUrl = mxcUrl,
        imageWidth = imageWidth,
        imageHeight = imageHeight
)
