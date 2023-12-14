

package org.matrix.android.sdk.internal.raw

import io.realm.annotations.RealmModule
import org.matrix.android.sdk.internal.database.model.KnownServerUrlEntity
import org.matrix.android.sdk.internal.database.model.RawCacheEntity


@RealmModule(library = true,
        classes = [
            RawCacheEntity::class,
            KnownServerUrlEntity::class
        ])
internal class GlobalRealmModule
