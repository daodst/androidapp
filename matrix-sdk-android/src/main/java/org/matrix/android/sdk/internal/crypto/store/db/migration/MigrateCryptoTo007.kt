

package org.matrix.android.sdk.internal.crypto.store.db.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.crypto.model.OlmInboundGroupSessionWrapper
import org.matrix.android.sdk.internal.crypto.model.OlmInboundGroupSessionWrapper2
import org.matrix.android.sdk.internal.crypto.store.db.deserializeFromRealm
import org.matrix.android.sdk.internal.crypto.store.db.mapper.CrossSigningKeysMapper
import org.matrix.android.sdk.internal.crypto.store.db.model.KeyInfoEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.OlmInboundGroupSessionEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.serializeForRealm
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.util.database.RealmMigrator
import timber.log.Timber

internal class MigrateCryptoTo007(realm: DynamicRealm) : RealmMigrator(realm, 7) {

    override fun doMigrate(realm: DynamicRealm) {
        Timber.d("Updating KeyInfoEntity table")
        val crossSigningKeysMapper = CrossSigningKeysMapper(MoshiProvider.providesMoshi())

        val keyInfoEntities = realm.where("KeyInfoEntity").findAll()
        try {
            keyInfoEntities.forEach {
                val stringSignatures = it.getString(KeyInfoEntityFields.SIGNATURES)
                val objectSignatures: Map<String, Map<String, String>>? = deserializeFromRealm(stringSignatures)
                val jsonSignatures = crossSigningKeysMapper.serializeSignatures(objectSignatures)
                it.setString(KeyInfoEntityFields.SIGNATURES, jsonSignatures)
            }
        } catch (failure: Throwable) {
        }

        
        val inboundGroupSessions = realm.where("OlmInboundGroupSessionEntity").findAll()
        inboundGroupSessions.forEach { dynamicObject ->
            dynamicObject.getString(OlmInboundGroupSessionEntityFields.OLM_INBOUND_GROUP_SESSION_DATA)?.let { serializedObject ->
                try {
                    deserializeFromRealm<OlmInboundGroupSessionWrapper?>(serializedObject)?.let { oldFormat ->
                        val newFormat = oldFormat.exportKeys()?.let {
                            OlmInboundGroupSessionWrapper2(it)
                        }
                        dynamicObject.setString(OlmInboundGroupSessionEntityFields.OLM_INBOUND_GROUP_SESSION_DATA, serializeForRealm(newFormat))
                    }
                } catch (failure: Throwable) {
                    Timber.e(failure, "## OlmInboundGroupSessionEntity migration failed")
                }
            }
        }
    }
}
