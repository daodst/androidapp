

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoRoomEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateCryptoTo015(realm: DynamicRealm) : RealmMigrator(realm, 15) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.get("CryptoRoomEntity")
                ?.addField(CryptoRoomEntityFields.WAS_ENCRYPTED_ONCE, Boolean::class.java)
                ?.setNullable(CryptoRoomEntityFields.WAS_ENCRYPTED_ONCE, true)
                ?.transform {
                    val currentAlgorithm = it.getString(CryptoRoomEntityFields.ALGORITHM)
                    it.set(CryptoRoomEntityFields.WAS_ENCRYPTED_ONCE, currentAlgorithm == MXCRYPTO_ALGORITHM_MEGOLM)
                }
    }
}
