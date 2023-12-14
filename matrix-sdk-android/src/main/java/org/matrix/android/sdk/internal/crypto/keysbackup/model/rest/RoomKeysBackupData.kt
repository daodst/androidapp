

package org.matrix.android.sdk.internal.crypto.keysbackup.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class RoomKeysBackupData(
        
        @Json(name = "sessions")
        val sessionIdToKeyBackupData: MutableMap<String, KeyBackupData> = HashMap()
)
