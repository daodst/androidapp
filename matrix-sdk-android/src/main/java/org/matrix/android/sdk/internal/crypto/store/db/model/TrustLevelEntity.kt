

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject

internal open class TrustLevelEntity(
        var crossSignedVerified: Boolean? = null,
        var locallyVerified: Boolean? = null
) : RealmObject() {

    companion object

    fun isVerified(): Boolean = crossSignedVerified == true || locallyVerified == true
}
