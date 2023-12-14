

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class MXDeviceInfo(
        
        @Json(name = "device_id")
        val deviceId: String,

        
        @Json(name = "user_id")
        val userId: String,

        
        @Json(name = "algorithms")
        val algorithms: List<String>? = null,

        
        @Json(name = "keys")
        val keys: Map<String, String>? = null,

        
        @Json(name = "signatures")
        val signatures: Map<String, Map<String, String>>? = null,

        
        @Json(name = "unsigned")
        val unsigned: JsonDict? = null,

        
        val verified: Int = DEVICE_VERIFICATION_UNKNOWN
) : Serializable {
    
    val isUnknown: Boolean
        get() = verified == DEVICE_VERIFICATION_UNKNOWN

    
    val isVerified: Boolean
        get() = verified == DEVICE_VERIFICATION_VERIFIED

    
    val isUnverified: Boolean
        get() = verified == DEVICE_VERIFICATION_UNVERIFIED

    
    val isBlocked: Boolean
        get() = verified == DEVICE_VERIFICATION_BLOCKED

    
    fun fingerprint(): String? {
        return keys
                ?.takeIf { deviceId.isNotBlank() }
                ?.get("ed25519:$deviceId")
    }

    
    fun identityKey(): String? {
        return keys
                ?.takeIf { deviceId.isNotBlank() }
                ?.get("curve25519:$deviceId")
    }

    
    fun displayName(): String? {
        return unsigned?.get("device_display_name") as? String
    }

    
    fun signalableJSONDictionary(): Map<String, Any> {
        val map = HashMap<String, Any>()

        map["device_id"] = deviceId

        map["user_id"] = userId

        if (null != algorithms) {
            map["algorithms"] = algorithms
        }

        if (null != keys) {
            map["keys"] = keys
        }

        return map
    }

    override fun toString(): String {
        return "MXDeviceInfo $userId:$deviceId"
    }

    companion object {
        
        const val DEVICE_VERIFICATION_UNKNOWN = -1

        
        const val DEVICE_VERIFICATION_UNVERIFIED = 0

        
        const val DEVICE_VERIFICATION_VERIFIED = 1

        
        const val DEVICE_VERIFICATION_BLOCKED = 2
    }
}
