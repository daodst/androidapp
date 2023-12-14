

package org.matrix.android.sdk.internal.session.thirdparty

import org.matrix.android.sdk.api.session.room.model.thirdparty.ThirdPartyProtocol
import org.matrix.android.sdk.api.session.thirdparty.ThirdPartyService
import org.matrix.android.sdk.api.session.thirdparty.model.ThirdPartyUser
import javax.inject.Inject

internal class DefaultThirdPartyService @Inject constructor(private val getThirdPartyProtocolTask: GetThirdPartyProtocolsTask,
                                                            private val getThirdPartyUserTask: GetThirdPartyUserTask) :
        ThirdPartyService {

    override suspend fun getThirdPartyProtocols(): Map<String, ThirdPartyProtocol> {
        return getThirdPartyProtocolTask.execute(Unit)
    }

    override suspend fun getThirdPartyUser(protocol: String, fields: Map<String, String>): List<ThirdPartyUser> {
        val taskParams = GetThirdPartyUserTask.Params(
                protocol = protocol,
                fields = fields
        )
        return getThirdPartyUserTask.execute(taskParams)
    }
}
