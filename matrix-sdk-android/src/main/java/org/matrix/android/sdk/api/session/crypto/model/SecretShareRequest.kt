

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SecretShareRequest(
        @Json(name = "action")
        override val action: String? = GossipingToDeviceObject.ACTION_SHARE_REQUEST,

        @Json(name = "requesting_device_id")
        override val requestingDeviceId: String? = null,

        @Json(name = "request_id")
        override val requestId: String? = null,

        @Json(name = "name")
        val secretName: String? = null
) : GossipingToDeviceObject
