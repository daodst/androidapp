

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class KeysBackupDataEntity(
        
        @PrimaryKey
        var primaryKey: Int = 0,
        
        var backupLastServerHash: String? = null,
        
        var backupLastServerNumberOfKeys: Int? = null
) : RealmObject()
