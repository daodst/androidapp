package org.matrix.android.sdk.internal.session.remark

import androidx.lifecycle.LiveData
import javax.inject.Inject

internal class DefaultRemarkManager @Inject constructor(private val remarkCache: RemarkCache) :
    RemarkManager {

    override fun addRemarkListener(listener: RemarkManager.RemarkListener) {
        remarkCache.addRemarkListener(listener)
    }

    override fun removeRemarkListener(listener: RemarkManager.RemarkListener) {
        remarkCache.removeRemarkListener(listener)
    }

    override suspend fun getRemark(userId: String): String? {
        return remarkCache.getRemark(userId)
    }

    override fun getRemarkFromCache(userId: String): String? {
        return remarkCache.getRemark(userId)
    }

    override fun getRemarks(): LiveData<List<Remark>> {
        return remarkCache.getRemarks()
    }

    override suspend fun updateRemark(userId: String, remark: String, isSync: Int) {
        remarkCache.updateRemark(userId, remark, isSync)
    }

    override fun forceUpdateRemarks(newRemarks: List<Remark>) {
        remarkCache.forceUpdateRemarks(newRemarks)
    }

    override fun insertOnlineRemarks(newRemarks: List<Remark>) {
        remarkCache.insertOnlineRemarks(newRemarks)
    }
}
