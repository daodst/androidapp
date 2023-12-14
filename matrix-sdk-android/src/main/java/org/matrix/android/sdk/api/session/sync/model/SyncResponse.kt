

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncResponse(

        
        val canUse: Boolean = false,

        val position: Int? = null,
        
        @Json(name = "account_data") val accountData: UserAccountDataSync? = null,

        
        @Json(name = "next_batch") val nextBatch: String? = null,

        
        @Json(name = "presence") val presence: PresenceSyncResponse? = null,

        
        @Json(name = "to_device") val toDevice: ToDeviceSyncResponse? = null,

        
        @Json(name = "rooms") val rooms: RoomsSyncResponse? = null,

        
        @Json(name = "device_lists") val deviceLists: DeviceListResponse? = null,

        
        @Json(name = "device_one_time_keys_count")
        val deviceOneTimeKeysCount: DeviceOneTimeKeysCountSyncResponse? = null,

        
        @Json(name = "org.matrix.msc2732.device_unused_fallback_key_types")
        val deviceUnusedFallbackKeyTypes: List<String>? = null,

        
        @Json(name = "groups") val groups: GroupsSyncResponse? = null

)
