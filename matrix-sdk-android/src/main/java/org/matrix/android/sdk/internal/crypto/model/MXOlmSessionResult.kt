

package org.matrix.android.sdk.internal.crypto.model

import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import java.io.Serializable

internal data class MXOlmSessionResult(
        
        val deviceInfo: CryptoDeviceInfo,
        
        var sessionId: String?
) : Serializable
