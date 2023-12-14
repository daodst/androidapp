

package org.matrix.android.sdk.internal.session.integrationmanager

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class IntegrationProvisioningContent(
        @Json(name = "enabled") val enabled: Boolean
)
