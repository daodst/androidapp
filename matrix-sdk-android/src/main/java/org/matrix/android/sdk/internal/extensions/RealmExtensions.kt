

package org.matrix.android.sdk.internal.extensions

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmObjectSchema
import org.matrix.android.sdk.internal.database.model.HomeServerCapabilitiesEntityFields

internal fun RealmObject.assertIsManaged() {
    check(isManaged) { "${javaClass.simpleName} entity should be managed to use this function" }
}


internal fun <T> RealmList<T>.clearWith(delete: (T) -> Unit) {
    while (!isEmpty()) {
        first()?.let { delete.invoke(it) }
    }
}


internal fun RealmObjectSchema?.forceRefreshOfHomeServerCapabilities(): RealmObjectSchema? {
    return this?.transform { obj ->
        obj.setLong(HomeServerCapabilitiesEntityFields.LAST_UPDATED_TIMESTAMP, 0)
    }
}
