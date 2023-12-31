

package org.matrix.android.sdk.internal.session.contentscanner.db

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where

internal fun ContentScanResultEntity.Companion.get(realm: Realm, attachmentUrl: String, contentScannerUrl: String?): ContentScanResultEntity? {
    return realm.where<ContentScanResultEntity>()
            .equalTo(ContentScanResultEntityFields.MEDIA_URL, attachmentUrl)
            .apply {
                contentScannerUrl?.let {
                    equalTo(ContentScanResultEntityFields.SCANNER_URL, it)
                }
            }
            .findFirst()
}

internal fun ContentScanResultEntity.Companion.getOrCreate(realm: Realm, attachmentUrl: String, contentScannerUrl: String?): ContentScanResultEntity {
    return ContentScanResultEntity.get(realm, attachmentUrl, contentScannerUrl)
            ?: realm.createObject<ContentScanResultEntity>().also {
                it.mediaUrl = attachmentUrl
                it.scanDateTimestamp = System.currentTimeMillis()
                it.scannerUrl = contentScannerUrl
            }
}
