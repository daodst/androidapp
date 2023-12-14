package org.matrix.android.sdk.internal.session.profile

import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal abstract class ServerPubKeyTask : Task<Unit, String> {

}

internal class DefaultServerPubKeyTask @Inject constructor(
    private val profileAPI: ProfileAPI
) : ServerPubKeyTask() {
    override suspend fun execute(params: Unit): String {
        return profileAPI.getServerPublicKey()
    }

}