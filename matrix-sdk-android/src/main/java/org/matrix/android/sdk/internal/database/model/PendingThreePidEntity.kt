

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject


internal open class PendingThreePidEntity(
        var email: String? = null,
        var msisdn: String? = null,
        var clientSecret: String = "",
        var sendAttempt: Int = 0,
        var sid: String = "",
        var submitUrl: String? = null
) : RealmObject()
