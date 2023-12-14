

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RemarkEntity(@PrimaryKey var userId: String = "",
                                 var remark: String = "",
                                 var isSync: Int = 0
) : RealmObject() {

    companion object
}
