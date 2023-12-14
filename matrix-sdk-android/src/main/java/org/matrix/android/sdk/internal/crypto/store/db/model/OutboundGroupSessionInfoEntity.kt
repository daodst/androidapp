

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import org.matrix.android.sdk.internal.crypto.store.db.deserializeFromRealm
import org.matrix.android.sdk.internal.crypto.store.db.serializeForRealm
import org.matrix.olm.OlmOutboundGroupSession
import timber.log.Timber

internal open class OutboundGroupSessionInfoEntity(
        var serializedOutboundSessionData: String? = null,
        var creationTime: Long? = null
) : RealmObject() {

    fun getOutboundGroupSession(): OlmOutboundGroupSession? {
        return try {
            deserializeFromRealm(serializedOutboundSessionData)
        } catch (failure: Throwable) {
            Timber.e(failure, "## getOutboundGroupSession() Deserialization failure")
            return null
        }
    }

    fun putOutboundGroupSession(olmOutboundGroupSession: OlmOutboundGroupSession?) {
        serializedOutboundSessionData = serializeForRealm(olmOutboundGroupSession)
    }

    companion object
}
