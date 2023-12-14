

package org.matrix.android.sdk.internal.crypto.model

import org.matrix.android.sdk.api.util.JsonDict
import timber.log.Timber

internal data class MXKey(
        
        val type: String,

        
        private val keyId: String,

        
        val value: String,

        
        private val signatures: Map<String, Map<String, String>>,

        
        private val rawMap: JsonDict
) {

    
    fun signalableJSONDictionary(): Map<String, Any> {
        return rawMap.filter {
            it.key != "signatures" && it.key != "unsigned"
        }
    }

    
    fun signatureForUserId(userId: String, signkey: String): String? {
        
        if (userId.isNotBlank() && signkey.isNotBlank()) {
            return signatures[userId]?.get(signkey)
        }

        return null
    }

    companion object {
        
        const val KEY_CURVE_25519_TYPE = "curve25519"
        const val KEY_SIGNED_CURVE_25519_TYPE = "signed_curve25519"
        

        
        fun from(map: Map<String, JsonDict>?): MXKey? {
            if (map?.isNotEmpty() == true) {
                val firstKey = map.keys.first()

                val components = firstKey.split(":").dropLastWhile { it.isEmpty() }

                if (components.size == 2) {
                    val params = map[firstKey]
                    if (params != null) {
                        if (params["key"] is String) {
                            @Suppress("UNCHECKED_CAST")
                            return MXKey(
                                    type = components[0],
                                    keyId = components[1],
                                    value = params["key"] as String,
                                    signatures = params["signatures"] as Map<String, Map<String, String>>,
                                    rawMap = params
                            )
                        }
                    }
                }
            }

            
            Timber.e("## Unable to parse map")
            return null
        }
    }
}
