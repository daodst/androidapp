

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.matrix.android.sdk.internal.crypto.store.db.deserializeFromRealm
import org.matrix.android.sdk.internal.crypto.store.db.serializeForRealm
import org.matrix.olm.OlmSession

internal fun OlmSessionEntity.Companion.createPrimaryKey(sessionId: String, deviceKey: String) = "$sessionId|$deviceKey"

internal open class OlmSessionEntity(@PrimaryKey var primaryKey: String = "",
                                     var sessionId: String? = null,
                                     var deviceKey: String? = null,
                                     var olmSessionData: String? = null,
                                     var lastReceivedMessageTs: Long = 0) :
        RealmObject() {

    fun getOlmSession(): OlmSession? {
        return deserializeFromRealm(olmSessionData)
    }

    fun putOlmSession(olmSession: OlmSession?) {
        olmSessionData = serializeForRealm(olmSession)
    }

    companion object
}
