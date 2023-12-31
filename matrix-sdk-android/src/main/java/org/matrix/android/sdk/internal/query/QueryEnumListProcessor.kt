

package org.matrix.android.sdk.internal.query

import io.realm.RealmObject
import io.realm.RealmQuery

internal fun <T : RealmObject, E : Enum<E>> RealmQuery<T>.process(field: String, enums: List<Enum<E>>): RealmQuery<T> {
    val lastEnumValue = enums.lastOrNull()
    beginGroup()
    for (enumValue in enums) {
        equalTo(field, enumValue.name)
        if (enumValue != lastEnumValue) {
            or()
        }
    }
    endGroup()
    return this
}
