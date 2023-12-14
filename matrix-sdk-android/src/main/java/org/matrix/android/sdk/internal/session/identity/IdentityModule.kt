

package org.matrix.android.sdk.internal.session.identity

import dagger.Binds
import dagger.Module
import dagger.Provides
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.session.identity.IdentityService
import org.matrix.android.sdk.internal.database.RealmKeysUtils
import org.matrix.android.sdk.internal.di.AuthenticatedIdentity
import org.matrix.android.sdk.internal.di.IdentityDatabase
import org.matrix.android.sdk.internal.di.SessionFilesDirectory
import org.matrix.android.sdk.internal.di.UnauthenticatedWithCertificate
import org.matrix.android.sdk.internal.di.UserMd5
import org.matrix.android.sdk.internal.network.httpclient.addAccessTokenInterceptor
import org.matrix.android.sdk.internal.network.token.AccessTokenProvider
import org.matrix.android.sdk.internal.session.SessionModule
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.identity.data.IdentityStore
import org.matrix.android.sdk.internal.session.identity.db.IdentityRealmModule
import org.matrix.android.sdk.internal.session.identity.db.RealmIdentityStore
import org.matrix.android.sdk.internal.session.identity.db.RealmIdentityStoreMigration
import java.io.File

@Module
internal abstract class IdentityModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @SessionScope
        @AuthenticatedIdentity
        fun providesOkHttpClient(@UnauthenticatedWithCertificate okHttpClient: OkHttpClient,
                                 @AuthenticatedIdentity accessTokenProvider: AccessTokenProvider): OkHttpClient {
            return okHttpClient
                    .newBuilder()
                    .addAccessTokenInterceptor(accessTokenProvider)
                    .build()
        }

        @JvmStatic
        @Provides
        @IdentityDatabase
        @SessionScope
        fun providesIdentityRealmConfiguration(realmKeysUtils: RealmKeysUtils,
                                               realmIdentityStoreMigration: RealmIdentityStoreMigration,
                                               @SessionFilesDirectory directory: File,
                                               @UserMd5 userMd5: String): RealmConfiguration {
            return RealmConfiguration.Builder()
                    .directory(directory)
                    .name("matrix-sdk-identity.realm")
                    .apply {
                        realmKeysUtils.configureEncryption(this, SessionModule.getKeyAlias(userMd5))
                    }
                    .schemaVersion(realmIdentityStoreMigration.schemaVersion)
                    .migration(realmIdentityStoreMigration)
                    .allowWritesOnUiThread(true)
                    .modules(IdentityRealmModule())
                    .build()
        }
    }

    @Binds
    abstract fun bindIdentityService(service: DefaultIdentityService): IdentityService

    @Binds
    @AuthenticatedIdentity
    abstract fun bindAccessTokenProvider(provider: IdentityAccessTokenProvider): AccessTokenProvider

    @Binds
    abstract fun bindIdentityStore(store: RealmIdentityStore): IdentityStore

    @Binds
    abstract fun bindEnsureIdentityTokenTask(task: DefaultEnsureIdentityTokenTask): EnsureIdentityTokenTask

    @Binds
    abstract fun bindIdentityPingTask(task: DefaultIdentityPingTask): IdentityPingTask

    @Binds
    abstract fun bindIdentityRegisterTask(task: DefaultIdentityRegisterTask): IdentityRegisterTask

    @Binds
    abstract fun bindIdentityRequestTokenForBindingTask(task: DefaultIdentityRequestTokenForBindingTask): IdentityRequestTokenForBindingTask

    @Binds
    abstract fun bindIdentitySubmitTokenForBindingTask(task: DefaultIdentitySubmitTokenForBindingTask): IdentitySubmitTokenForBindingTask

    @Binds
    abstract fun bindIdentityBulkLookupTask(task: DefaultIdentityBulkLookupTask): IdentityBulkLookupTask

    @Binds
    abstract fun bindIdentityDisconnectTask(task: DefaultIdentityDisconnectTask): IdentityDisconnectTask
}
