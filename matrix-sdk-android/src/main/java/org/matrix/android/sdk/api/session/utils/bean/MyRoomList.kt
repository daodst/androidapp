package org.matrix.android.sdk.api.session.utils.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class MyRoomList(
        @Json(name = "joined_rooms")
        var joined_rooms: List<String>? = null
) : Parcelable
