

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject

internal open class IgnoredUserEntity(var userId: String = "") : RealmObject() {

    companion object
}
