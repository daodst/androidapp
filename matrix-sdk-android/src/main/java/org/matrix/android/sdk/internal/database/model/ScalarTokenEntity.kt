

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class ScalarTokenEntity(
        @PrimaryKey var serverUrl: String = "",
        var token: String = ""
) : RealmObject() {

    companion object
}
