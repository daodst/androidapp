

package org.matrix.android.sdk.internal.session.identity.db

import io.realm.Realm
import io.realm.RealmConfiguration
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.internal.di.IdentityDatabase
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.identity.data.IdentityData
import org.matrix.android.sdk.internal.session.identity.data.IdentityPendingBinding
import org.matrix.android.sdk.internal.session.identity.data.IdentityStore
import org.matrix.android.sdk.internal.session.identity.model.IdentityHashDetailResponse
import javax.inject.Inject

@SessionScope
internal class RealmIdentityStore @Inject constructor(
        @IdentityDatabase
        private val realmConfiguration: RealmConfiguration
) : IdentityStore {

    override fun getIdentityData(): IdentityData? {
        try {
            return Realm.getInstance(realmConfiguration).use { realm ->
                IdentityDataEntity.get(realm)?.let { IdentityMapper.map(it) }
            }
        } catch (e: Exception) {
            return null
        }
    }

    override fun setUrl(url: String?) {
        Realm.getInstance(realmConfiguration).use {
            it.executeTransaction { realm ->
                IdentityDataEntity.setUrl(realm, url)
            }
        }
    }

    override fun setToken(token: String?) {
        Realm.getInstance(realmConfiguration).use {
            it.executeTransaction { realm ->
                IdentityDataEntity.setToken(realm, token)
            }
        }
    }

    override fun setUserConsent(consent: Boolean) {
        Realm.getInstance(realmConfiguration).use {
            it.executeTransaction { realm ->
                IdentityDataEntity.setUserConsent(realm, consent)
            }
        }
    }

    override fun setHashDetails(hashDetailResponse: IdentityHashDetailResponse) {
        Realm.getInstance(realmConfiguration).use {
            it.executeTransaction { realm ->
                IdentityDataEntity.setHashDetails(realm, hashDetailResponse.pepper, hashDetailResponse.algorithms)
            }
        }
    }

    override fun storePendingBinding(threePid: ThreePid, data: IdentityPendingBinding) {
        Realm.getInstance(realmConfiguration).use {
            it.executeTransaction { realm ->
                IdentityPendingBindingEntity.getOrCreate(realm, threePid).let { entity ->
                    entity.clientSecret = data.clientSecret
                    entity.sendAttempt = data.sendAttempt
                    entity.sid = data.sid
                }
            }
        }
    }

    override fun getPendingBinding(threePid: ThreePid): IdentityPendingBinding? {
        return Realm.getInstance(realmConfiguration).use { realm ->
            IdentityPendingBindingEntity.get(realm, threePid)?.let { IdentityMapper.map(it) }
        }
    }

    override fun deletePendingBinding(threePid: ThreePid) {
        Realm.getInstance(realmConfiguration).use {
            it.executeTransaction { realm ->
                IdentityPendingBindingEntity.delete(realm, threePid)
            }
        }
    }
}
