

package org.matrix.android.sdk.internal.session.identity.db

import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.createObject
import io.realm.kotlin.where


internal fun IdentityDataEntity.Companion.get(realm: Realm): IdentityDataEntity? {
    return realm.where<IdentityDataEntity>().findFirst()
}

private fun IdentityDataEntity.Companion.getOrCreate(realm: Realm): IdentityDataEntity {
    return get(realm) ?: realm.createObject()
}

internal fun IdentityDataEntity.Companion.setUrl(realm: Realm,
                                                 url: String?) {
    realm.where<IdentityDataEntity>().findAll().deleteAllFromRealm()
    
    IdentityPendingBindingEntity.deleteAll(realm)

    if (url != null) {
        getOrCreate(realm).apply {
            identityServerUrl = url
        }
    }
}

internal fun IdentityDataEntity.Companion.setToken(realm: Realm,
                                                   newToken: String?) {
    get(realm)?.apply {
        token = newToken
    }
}

internal fun IdentityDataEntity.Companion.setUserConsent(realm: Realm,
                                                         newConsent: Boolean) {
    get(realm)?.apply {
        userConsent = newConsent
    }
}

internal fun IdentityDataEntity.Companion.setHashDetails(realm: Realm,
                                                         pepper: String,
                                                         algorithms: List<String>) {
    get(realm)?.apply {
        hashLookupPepper = pepper
        hashLookupAlgorithm = RealmList<String>().apply { addAll(algorithms) }
    }
}
