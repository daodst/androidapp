

package org.matrix.android.sdk.api.session.crypto.model

import org.matrix.android.sdk.api.util.JsonDict


data class MXEventDecryptionResult(
        
        val clearEvent: JsonDict,

        
        val senderCurve25519Key: String? = null,

        
        val claimedEd25519Key: String? = null,

        
        val forwardingCurve25519KeyChain: List<String> = emptyList()
)
