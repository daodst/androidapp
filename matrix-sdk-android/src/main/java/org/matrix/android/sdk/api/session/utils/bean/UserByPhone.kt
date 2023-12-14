package org.matrix.android.sdk.api.session.utils.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class UserByPhone(
        
        @Json(name = "display_name")
        var display_name: String = "",
        
        @Json(name = "avatar_url")
        var avatar_url: String = "",
        
        @Json(name = "localpart")
        val localpart: String = "",
        
        @Json(name = "servername")
        var servername: String = "",
        
        @Json(name = "chat_fee")
        val chat_fee: String = "",
        
        @Json(name = "can_we_talk")
        val can_we_talk: Boolean = false,
        
        @Json(name = "can_pay_talk")
        val can_pay_talk: Boolean = false,

        

        
        @Json(name = "reason")
        var reason: String = "",
        
        @Json(name = "tel_numbers")
        var tel_numbers: List<String>? = null,

        
        @Json(name = "payed")
        var payed: Boolean = false,

        var shouldSendFlowers: Boolean = false,
) : Parcelable {

    
    @IgnoredOnParcel
    val userId
        get() = "@$localpart:${servername}"
}

