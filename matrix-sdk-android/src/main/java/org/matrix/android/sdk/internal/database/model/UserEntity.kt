

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class UserEntity(@PrimaryKey var userId: String = "",
                               var displayName: String = "",
                               var avatarUrl: String = "",
                               var telNumbers: RealmList<String> = RealmList()
) : RealmObject() {

    companion object
}
