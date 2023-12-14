package org.matrix.android.sdk.api.session.utils.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.util.MatrixItem

@Parcelize
@JsonClass(generateAdapter = true)
class UserBasicInfo(
        
        @Json(name = "localpart")
        val localpart: String = "",
        
        @Json(name = "servername")
        val servername: String = "",
        
        @Json(name = "chat_fee")
        val chat_fee: String = "",
        
        @Json(name = "reason")
        var reason: String = "",
        
        @Json(name = "tel_numbers")
        var tel_numbers: List<String>? = null,

        
        @Json(name = "payed")
        var payed: Boolean = false,

        var shouldSendFlowers: Boolean = false,
) : Parcelable {

    
    @IgnoredOnParcel
    val userId = "@$localpart:${servername}"
}

internal object UserByPhoneMapper {

    fun map(user: UserBasicInfo, item: MatrixItem?, can_we_talk: Boolean, can_pay_talk: Boolean): UserByPhone {
        return UserByPhone(
                display_name = getBestName(item),
                avatar_url = item?.avatarUrl ?: "",
                localpart = user.localpart,
                servername = user.servername,
                chat_fee = user.chat_fee,
                can_we_talk = can_we_talk,
                can_pay_talk = can_pay_talk,
                reason = user.reason,
                tel_numbers = user.tel_numbers,
                payed = user.payed,
        )
    }
}

internal fun getBestName(item: MatrixItem?): String {
    
    return if (item is MatrixItem.GroupItem || item is MatrixItem.RoomAliasItem) {
        
        item.id
    } else {
        item?.displayName
                ?.takeIf { it.isNotBlank() }
                ?: item?.id ?: ""
    }
}


fun UserBasicInfo.asFreeDomain(item: MatrixItem? = null): UserByPhone {
    
    return UserByPhoneMapper.map(this, item, can_we_talk = true, can_pay_talk = false)
}


fun UserBasicInfo.asPayDomain(item: MatrixItem? = null): UserByPhone {
    
    return UserByPhoneMapper.map(this, item, can_we_talk = false, can_pay_talk = true)
}


fun UserBasicInfo.asBlackDomain(item: MatrixItem? = null): UserByPhone {
    
    return UserByPhoneMapper.map(this, item, can_we_talk = false, can_pay_talk = false)
}
