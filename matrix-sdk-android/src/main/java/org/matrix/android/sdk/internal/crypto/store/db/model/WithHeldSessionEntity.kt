

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.Index
import org.matrix.android.sdk.api.session.events.model.content.WithHeldCode


internal open class WithHeldSessionEntity(
        var roomId: String? = null,
        var algorithm: String? = null,
        @Index var sessionId: String? = null,
        @Index var senderKey: String? = null,
        var codeString: String? = null,
        var reason: String? = null
) : RealmObject() {

    var code: WithHeldCode?
        get() {
            return WithHeldCode.fromCode(codeString)
        }
        set(code) {
            codeString = code?.value
        }

    companion object
}
