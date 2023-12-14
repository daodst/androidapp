

package org.matrix.android.sdk.internal.session.contentscanner.db

import io.realm.annotations.RealmModule


@RealmModule(library = true,
        classes = [
            ContentScannerInfoEntity::class,
            ContentScanResultEntity::class
        ])
internal class ContentScannerRealmModule
