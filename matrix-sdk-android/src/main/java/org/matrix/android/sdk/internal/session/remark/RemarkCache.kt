package org.matrix.android.sdk.internal.session.remark

import androidx.lifecycle.LiveData
import com.zhuinden.monarchy.Monarchy
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.mapper.RemarkMapper
import org.matrix.android.sdk.internal.database.model.RemarkEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import timber.log.Timber
import javax.inject.Inject

internal class RemarkCache @Inject constructor(
    @SessionDatabase private val monarchy: Monarchy,
    private val realmSessionProvider: RealmSessionProvider,
    private val remarkMapper: RemarkMapper,
) {
    private val listeners = mutableSetOf<RemarkManager.RemarkListener>()

    private val remarks = mutableMapOf<String, RemarkEntity>()

    init {
    }

    fun addRemarkListener(listener: RemarkManager.RemarkListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun removeRemarkListener(listener: RemarkManager.RemarkListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    private fun dispatchRemarkChanged(userId: String, remark: String) {
        Timber.v("sky  dispatchRemarkChanged $remark  ${listeners.size}")
        synchronized(listeners) {
            listeners.forEach {
                it.onRemarkChanged(userId, remark)
            }
        }
    }

    
    fun updateRemark(userId: String, remark: String, isSync: Int = 0) {
        var re = RemarkEntity(userId, remark, 0)
        remarks[userId] = re
        remarks[userId] = re
        realmSessionProvider.withRealm {
            it.executeTransaction {
                it.insertOrUpdate(re)
            }
        }
        dispatchRemarkChanged(userId, remark)
    }

    
    fun forceUpdateRemarks(newRemarks: List<Remark>) {
        realmSessionProvider.withRealm { realm ->
            
            realm.executeTransaction { db ->
                newRemarks.forEach{
                    db.insertOrUpdate(it.toEntity())
                }
            }
        }
    }

    
    fun insertOnlineRemarks(newRemarks: List<Remark>) {
        realmSessionProvider.withRealm { realm ->
            
            realm.executeTransaction { db ->
                newRemarks.forEach {
                    var entity = db.where<RemarkEntity>().equalTo("userId", it.userId).findFirst()
                    if (entity == null) {
                        db.insert(it.toEntity())
                    }
                }
            }
        }
    }



    
    fun getRemark(userId: String): String? {
        return remarks[userId]?.remark ?: realmSessionProvider.withRealm { realm ->
            RemarkEntity.where(realm, userId).findFirst()?.remark
        }
    }

    fun getRemarks(): LiveData<List<Remark>> {
        return monarchy.findAllMappedWithChanges({ realm ->
            realm.where<RemarkEntity>()
        }, { remarkMapper.map(it) })
    }

    
    fun getRemarkFromCache(userId: String): String? {
        return remarks[userId]?.remark
    }
}
