package org.matrix.android.sdk.internal.session.chain

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.ChainEntity
import org.matrix.android.sdk.internal.di.SessionDatabase
import javax.inject.Inject


class DefaultChainUpload @Inject constructor(@SessionDatabase private val monarchy: Monarchy) :
    ChainUploadService {
    override fun insertChain(chain: ChainEntity, call: Realm.Transaction.OnSuccess) {
        monarchy.doWithRealm {realm ->
            realm?.executeTransactionAsync(Realm.Transaction { transition ->
                val first = transition.where<ChainEntity>().findFirst()
                first?.apply {
                    md5 = chain.md5
                    json = chain.json
                    transition.copyToRealm(first)
                }
                if (first == null) {
                    transition.copyToRealm(chain)
                }
            }, call)
        }
    }

    override fun queryChainAll(): ChainEntity? {
        val list = monarchy.fetchAllCopiedSync {
            it.where<ChainEntity>().equalTo("id", "1")
        }
        return if (list.size > 0) list[0] else null
    }

    
    override fun deleteAll() {
        monarchy.doWithRealm { realm->
            realm.executeTransactionAsync{
                it.where<ChainEntity>().findAll().deleteAllFromRealm()
            }
        }
    }
}
