

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.matrix.android.sdk.internal.crypto.model.OlmInboundGroupSessionWrapper2
import org.matrix.android.sdk.internal.crypto.store.db.deserializeFromRealm
import org.matrix.android.sdk.internal.crypto.store.db.serializeForRealm
import timber.log.Timber

internal fun OlmInboundGroupSessionEntity.Companion.createPrimaryKey(sessionId: String?, senderKey: String?) = "$sessionId|$senderKey"

internal open class OlmInboundGroupSessionEntity(
        
        @PrimaryKey var primaryKey: String? = null,
        var sessionId: String? = null,
        var senderKey: String? = null,
        
        var olmInboundGroupSessionData: String? = null,
        
        var backedUp: Boolean = false) :
        RealmObject() {

    fun getInboundGroupSession(): OlmInboundGroupSessionWrapper2? {
        return try {
            deserializeFromRealm<OlmInboundGroupSessionWrapper2?>(olmInboundGroupSessionData)
        } catch (failure: Throwable) {
            Timber.e(failure, "## Deserialization failure")
            return null
        }
    }

    fun putInboundGroupSession(olmInboundGroupSessionWrapper: OlmInboundGroupSessionWrapper2?) {
        olmInboundGroupSessionData = serializeForRealm(olmInboundGroupSessionWrapper)
    }

    companion object
}
