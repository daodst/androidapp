

package org.matrix.android.sdk.internal.auth.db.migration

import android.net.Uri
import io.realm.DynamicRealm
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.internal.auth.db.SessionParamsEntityFields
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.util.database.RealmMigrator
import timber.log.Timber

internal class MigrateAuthTo004(realm: DynamicRealm) : RealmMigrator(realm, 4) {

    override fun doMigrate(realm: DynamicRealm) {
        Timber.d("Update SessionParamsEntity to add HomeServerConnectionConfig.homeServerUriBase value")

        val adapter = MoshiProvider.providesMoshi()
                .adapter(HomeServerConnectionConfig::class.java)

        realm.schema.get("SessionParamsEntity")
                ?.transform {
                    val homeserverConnectionConfigJson = it.getString(SessionParamsEntityFields.HOME_SERVER_CONNECTION_CONFIG_JSON)

                    val homeserverConnectionConfig = adapter
                            .fromJson(homeserverConnectionConfigJson)

                    val homeserverUrl = homeserverConnectionConfig?.homeServerUri?.toString()
                    
                    
                    val alteredHomeserverConnectionConfig =
                            if (homeserverUrl == "https://matrix.org" || homeserverUrl == "https://matrix-client.matrix.org") {
                                homeserverConnectionConfig.copy(
                                        homeServerUri = Uri.parse("https://matrix.org"),
                                        homeServerUriBase = Uri.parse("https://matrix-client.matrix.org")
                                )
                            } else {
                                homeserverConnectionConfig
                            }
                    it.set(SessionParamsEntityFields.HOME_SERVER_CONNECTION_CONFIG_JSON, adapter.toJson(alteredHomeserverConnectionConfig))
                }
    }
}
