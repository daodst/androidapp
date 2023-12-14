

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataEvent

@JsonClass(generateAdapter = true)
data class UserAccountDataSync(
        @Json(name = "events") val list: List<UserAccountDataEvent> = emptyList()
)
