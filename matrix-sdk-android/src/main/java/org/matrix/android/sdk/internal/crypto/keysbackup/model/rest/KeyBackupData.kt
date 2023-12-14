

package org.matrix.android.sdk.internal.crypto.keysbackup.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.network.parsing.ForceToBoolean


@JsonClass(generateAdapter = true)
internal data class KeyBackupData(
        
        @Json(name = "first_message_index")
        val firstMessageIndex: Long,

        
        @Json(name = "forwarded_count")
        val forwardedCount: Int,

        
        @ForceToBoolean
        @Json(name = "is_verified")
        val isVerified: Boolean,

        
        @Json(name = "session_data")
        val sessionData: JsonDict
)
