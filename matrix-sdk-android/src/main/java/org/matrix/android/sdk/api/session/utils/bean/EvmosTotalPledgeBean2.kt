package org.matrix.android.sdk.api.session.utils.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class EvmosTotalPledgeBean2 {
    @Json(name = "data")
    var data: Data? = null

    @Json(name = "status")
     var status = 0

    @Json(name = "info")
    var info: String? = null

    
    val isSuccess: Boolean
        get() = status == 1
}

@JsonClass(generateAdapter = true)
class Data {
    @Json(name = "pre_pledge_amount")
    var pre_pledge_amount 
            : EvmosAmountsBean? = null

    @Json(name = "all_pledge_amount")
    var all_pledge_amount 
            : EvmosAmountsBean? = null

    @Json(name = "remain_pledge_amount")
    var remain_pledge_amount 
            : EvmosAmountsBean? = null
}

@JsonClass(generateAdapter = true)
class EvmosAmountsBean {
    @Json(name = "denom")
    var denom 
            : String = ""

    @Json(name = "amount")
    var amount 
            : String = ""
}
