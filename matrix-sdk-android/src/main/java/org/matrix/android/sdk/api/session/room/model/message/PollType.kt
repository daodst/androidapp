

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class PollType {
    
    @Json(name = "org.matrix.msc3381.poll.disclosed")
    DISCLOSED_UNSTABLE,

    @Json(name = "m.poll.disclosed")
    DISCLOSED,

    
    @Json(name = "org.matrix.msc3381.poll.undisclosed")
    UNDISCLOSED_UNSTABLE,

    @Json(name = "m.poll.undisclosed")
    UNDISCLOSED
}
