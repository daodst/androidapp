package org.matrix.android.sdk.api.session.utils.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class InviteRoomPayStatus(

        @Json(name = "available_list") val available_list: MutableList<UserBasicInfo>? = ArrayList(),

        @Json(name = "need_pay_list") val need_pay_list: MutableList<UserBasicInfo>? = ArrayList(),
        @Json(name = "cant_list") val cant_list: MutableList<UserBasicInfo>? = ArrayList(),

        ) {

    fun isEmpty(): Boolean {
        return isPayEmpty() && isFreeEmpty()
    }

    fun isPayEmpty(): Boolean {
        return need_pay_list?.isEmpty() ?: true
    }

    private fun isFreeEmpty(): Boolean {
        return available_list?.isEmpty() ?: true
    }

    fun getUserByPhoneList(): MutableList<UserByPhone> {
        val users = mutableListOf<UserByPhone>()
        available_list?.map { it.asFreeDomain() }?.let { users.addAll(it) }
        need_pay_list?.map { it.asPayDomain() }?.let { users.addAll(it) }
        cant_list?.map { it.asBlackDomain() }?.let { users.addAll(it) }
        return users
    }
}

