
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.GossipingToDeviceObject
import org.matrix.android.sdk.api.session.crypto.model.GossipingToDeviceObject.Companion.ACTION_SHARE_CANCELLATION


@JsonClass(generateAdapter = true)
internal data class ShareRequestCancellation(
        @Json(name = "action")
        override val action: String? = ACTION_SHARE_CANCELLATION,

        @Json(name = "requesting_device_id")
        override val requestingDeviceId: String? = null,

        @Json(name = "request_id")
        override val requestId: String? = null
) : GossipingToDeviceObject
