

package org.matrix.android.sdk.internal.crypto

import io.realm.RealmConfiguration
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.store.db.RealmCryptoStore
import org.matrix.android.sdk.internal.crypto.store.db.RealmCryptoStoreModule
import org.matrix.android.sdk.internal.crypto.store.db.mapper.CrossSigningKeysMapper
import org.matrix.android.sdk.internal.di.MoshiProvider
import kotlin.random.Random

internal class CryptoStoreHelper {

    fun createStore(): IMXCryptoStore {
        return RealmCryptoStore(
                realmConfiguration = RealmConfiguration.Builder()
                        .name("test.realm")
                        .modules(RealmCryptoStoreModule())
                        .build(),
                crossSigningKeysMapper = CrossSigningKeysMapper(MoshiProvider.providesMoshi()),
                userId = "userId_" + Random.nextInt(),
                deviceId = "deviceId_sample"
        )
    }
}
