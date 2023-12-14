

package org.matrix.android.sdk.internal.auth.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class SessionParamsEntity(
        @PrimaryKey var sessionId: String = "",
        var userId: String = "",
        var credentialsJson: String = "",
        var homeServerConnectionConfigJson: String = "",
        
        
        var isTokenValid: Boolean = true
) : RealmObject()
