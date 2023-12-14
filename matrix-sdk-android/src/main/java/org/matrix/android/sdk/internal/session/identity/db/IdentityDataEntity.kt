

package org.matrix.android.sdk.internal.session.identity.db

import io.realm.RealmList
import io.realm.RealmObject

internal open class IdentityDataEntity(
        var identityServerUrl: String? = null,
        var token: String? = null,
        var hashLookupPepper: String? = null,
        var hashLookupAlgorithm: RealmList<String> = RealmList(),
        var userConsent: Boolean = false
) : RealmObject() {

    companion object
}
