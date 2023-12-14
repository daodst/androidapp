

package org.matrix.android.sdk.internal.session.identity.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.identity.toMedium

internal open class IdentityPendingBindingEntity(
        @PrimaryKey var threePid: String = "",
        
        var clientSecret: String = "",
        
        var sendAttempt: Int = 0,
        
        var sid: String = ""
) : RealmObject() {

    companion object {
        fun ThreePid.toPrimaryKey() = "${toMedium()}_$value"
    }
}
