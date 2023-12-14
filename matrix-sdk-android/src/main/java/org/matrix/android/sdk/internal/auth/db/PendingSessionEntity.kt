

package org.matrix.android.sdk.internal.auth.db

import io.realm.RealmObject

internal open class PendingSessionEntity(
        var homeServerConnectionConfigJson: String = "",
        var clientSecret: String = "",
        var sendAttempt: Int = 0,
        var resetPasswordDataJson: String? = null,
        var currentSession: String? = null,
        var isRegistrationStarted: Boolean = false,
        var currentThreePidDataJson: String? = null
) : RealmObject()
