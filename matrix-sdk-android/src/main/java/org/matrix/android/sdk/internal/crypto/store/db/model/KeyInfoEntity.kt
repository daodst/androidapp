

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmList
import io.realm.RealmObject

internal open class KeyInfoEntity(
        var publicKeyBase64: String? = null,
        var usages: RealmList<String> = RealmList(),
        
        var signatures: String? = null,
        var trustLevelEntity: TrustLevelEntity? = null
) : RealmObject()

internal fun KeyInfoEntity.deleteOnCascade() {
    trustLevelEntity?.deleteFromRealm()
    deleteFromRealm()
}
