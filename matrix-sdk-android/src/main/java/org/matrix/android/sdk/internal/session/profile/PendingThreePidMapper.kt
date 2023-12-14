

package org.matrix.android.sdk.internal.session.profile

import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.internal.database.model.PendingThreePidEntity
import javax.inject.Inject

internal class PendingThreePidMapper @Inject constructor() {

    fun map(entity: PendingThreePidEntity): PendingThreePid {
        return PendingThreePid(
                threePid = entity.email?.let { ThreePid.Email(it) }
                        ?: entity.msisdn?.let { ThreePid.Msisdn(it) }
                        ?: error("Invalid data"),
                clientSecret = entity.clientSecret,
                sendAttempt = entity.sendAttempt,
                sid = entity.sid,
                submitUrl = entity.submitUrl
        )
    }

    fun map(domain: PendingThreePid): PendingThreePidEntity {
        return PendingThreePidEntity(
                email = domain.threePid.takeIf { it is ThreePid.Email }?.value,
                msisdn = domain.threePid.takeIf { it is ThreePid.Msisdn }?.value,
                clientSecret = domain.clientSecret,
                sendAttempt = domain.sendAttempt,
                sid = domain.sid,
                submitUrl = domain.submitUrl
        )
    }
}
