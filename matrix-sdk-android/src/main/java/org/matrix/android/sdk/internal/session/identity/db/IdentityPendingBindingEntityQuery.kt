

package org.matrix.android.sdk.internal.session.identity.db

import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.matrix.android.sdk.api.session.identity.ThreePid

internal fun IdentityPendingBindingEntity.Companion.get(realm: Realm, threePid: ThreePid): IdentityPendingBindingEntity? {
    return realm.where<IdentityPendingBindingEntity>()
            .equalTo(IdentityPendingBindingEntityFields.THREE_PID, threePid.toPrimaryKey())
            .findFirst()
}

internal fun IdentityPendingBindingEntity.Companion.getOrCreate(realm: Realm, threePid: ThreePid): IdentityPendingBindingEntity {
    return get(realm, threePid) ?: realm.createObject(threePid.toPrimaryKey())
}

internal fun IdentityPendingBindingEntity.Companion.delete(realm: Realm, threePid: ThreePid) {
    get(realm, threePid)?.deleteFromRealm()
}

internal fun IdentityPendingBindingEntity.Companion.deleteAll(realm: Realm) {
    realm.where<IdentityPendingBindingEntity>()
            .findAll()
            .deleteAllFromRealm()
}
