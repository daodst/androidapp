package org.matrix.android.sdk.internal.session.remark

import org.matrix.android.sdk.internal.database.model.RemarkEntity

data class Remark(
        val userId: String,
        val remark: String? = null,
        var isSync: Int = 0,      
        val avatarUrl: String? = null,
        val avatarUid: String? = null,
        var did: String? = null,
        val address: String? = null,
        var syncTime: Long? = null

) {
    fun toEntity() : RemarkEntity {
        return RemarkEntity(userId, remark?:"", isSync)
    }

}
