

package org.matrix.android.sdk.internal.crypto.keysbackup.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class KeysBackupData(
        
        @Json(name = "rooms")
        val roomIdToRoomKeysBackupData: MutableMap<String, RoomKeysBackupData> = HashMap()
)
