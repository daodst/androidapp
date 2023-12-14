package org.matrix.android.sdk.internal.session.remark

import androidx.lifecycle.LiveData

interface RemarkManager {
    interface RemarkListener {
        
        fun onRemarkChanged(userId: String, remark: String)

        
        fun onRemarkLoaded()
    }

    fun addRemarkListener(listener: RemarkListener)

    fun removeRemarkListener(listener: RemarkListener)

    suspend fun getRemark(userId: String): String?

    fun getRemarks(): LiveData<List<Remark>>

    fun getRemarkFromCache(userId: String): String?

    suspend fun updateRemark(userId: String, remark: String, isSync: Int = 0)

    
    fun forceUpdateRemarks(newRemarks: List<Remark>);

    
    fun insertOnlineRemarks(newRemarks: List<Remark>);
}
