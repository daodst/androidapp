

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.Index


internal open class UserAccountDataEntity(
        @Index var type: String? = null,
        var contentStr: String? = null
) : RealmObject() {

    companion object
}
