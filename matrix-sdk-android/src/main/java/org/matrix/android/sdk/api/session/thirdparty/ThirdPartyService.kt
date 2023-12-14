

package org.matrix.android.sdk.api.session.thirdparty

import org.matrix.android.sdk.api.session.room.model.thirdparty.ThirdPartyProtocol
import org.matrix.android.sdk.api.session.thirdparty.model.ThirdPartyUser


interface ThirdPartyService {

    
    suspend fun getThirdPartyProtocols(): Map<String, ThirdPartyProtocol>

    
    suspend fun getThirdPartyUser(protocol: String, fields: Map<String, String> = emptyMap()): List<ThirdPartyUser>
}
