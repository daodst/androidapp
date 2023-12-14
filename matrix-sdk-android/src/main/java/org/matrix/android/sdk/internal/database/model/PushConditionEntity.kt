
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject

internal open class PushConditionEntity(
        var kind: String = "",
        var key: String? = null,
        var pattern: String? = null,
        var iz: String? = null
) : RealmObject() {

    companion object
}
