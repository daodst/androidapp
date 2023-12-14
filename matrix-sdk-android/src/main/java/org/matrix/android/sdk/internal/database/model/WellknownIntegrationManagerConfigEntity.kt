

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class WellknownIntegrationManagerConfigEntity(
        @PrimaryKey var id: Long = 0,
        var apiUrl: String = "",
        var uiUrl: String = ""
) : RealmObject() {

    companion object
}
