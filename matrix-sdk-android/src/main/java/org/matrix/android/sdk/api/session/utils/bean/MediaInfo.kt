package org.matrix.android.sdk.api.session.utils.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MediaInfo(

        
        @Json(name = "username")
        val Username: String = "",

        
        @Json(name = "cur_used_flow")
        val CurUsedFlow: Long = 0,

        
        @Json(name = "flow_limit")
        val FlowLimit: Long = 0,

        
        @Json(name = "user_improve")
        val UserImprove: String = "",
)
