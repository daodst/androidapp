

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.Index

internal open class CurrentStateEventEntity(var eventId: String = "",
                                            var root: EventEntity? = null,
                                            @Index var roomId: String = "",
                                            @Index var type: String = "",
                                            @Index var stateKey: String = ""
) : RealmObject() {
    companion object
}
