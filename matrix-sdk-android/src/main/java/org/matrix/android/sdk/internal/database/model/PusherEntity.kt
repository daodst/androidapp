
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import org.matrix.android.sdk.api.session.pushers.PusherState

internal open class PusherEntity(
        var pushKey: String = "",
        var kind: String? = null,
        var appId: String = "",
        var appDisplayName: String? = null,
        var deviceDisplayName: String? = null,
        var profileTag: String? = null,
        var lang: String? = null,
        var data: PusherDataEntity? = null
) : RealmObject() {
    private var stateStr: String = PusherState.UNREGISTERED.name

    var state: PusherState
        get() {
            try {
                return PusherState.valueOf(stateStr)
            } catch (e: Exception) {
                
                return PusherState.UNREGISTERED
            }
        }
        set(value) {
            stateStr = value.name
        }

    companion object
}

internal fun PusherEntity.deleteOnCascade() {
    data?.deleteFromRealm()
    deleteFromRealm()
}
