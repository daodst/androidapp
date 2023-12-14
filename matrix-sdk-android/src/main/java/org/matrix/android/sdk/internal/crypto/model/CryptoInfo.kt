

package org.matrix.android.sdk.internal.crypto.model


internal interface CryptoInfo {

    val userId: String

    val keys: Map<String, String>?

    val signatures: Map<String, Map<String, String>>?

    fun signalableJSONDictionary(): Map<String, Any>
}
