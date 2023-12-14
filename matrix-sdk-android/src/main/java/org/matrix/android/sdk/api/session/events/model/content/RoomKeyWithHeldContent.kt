
package org.matrix.android.sdk.api.session.events.model.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomKeyWithHeldContent(

        
        @Json(name = "room_id") val roomId: String? = null,

        
        @Json(name = "algorithm") val algorithm: String? = null,

        
        @Json(name = "session_id") val sessionId: String? = null,

        
        @Json(name = "sender_key") val senderKey: String? = null,

        
        @Json(name = "code") val codeString: String? = null,

        
        @Json(name = "reason") val reason: String? = null

) {
    val code: WithHeldCode?
        get() {
            return WithHeldCode.fromCode(codeString)
        }
}

enum class WithHeldCode(val value: String) {
    
    BLACKLISTED("m.blacklisted"),

    
    UNVERIFIED("m.unverified"),

    
    UNAUTHORISED("m.unauthorised"),

    
    UNAVAILABLE("m.unavailable"),

    
    NO_OLM("m.no_olm");

    companion object {
        fun fromCode(code: String?): WithHeldCode? {
            return when (code) {
                BLACKLISTED.value  -> BLACKLISTED
                UNVERIFIED.value   -> UNVERIFIED
                UNAUTHORISED.value -> UNAUTHORISED
                UNAVAILABLE.value  -> UNAVAILABLE
                NO_OLM.value       -> NO_OLM
                else               -> null
            }
        }
    }
}
