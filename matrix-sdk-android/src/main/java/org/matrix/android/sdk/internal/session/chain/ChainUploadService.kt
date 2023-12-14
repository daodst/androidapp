package org.matrix.android.sdk.internal.session.chain

import io.realm.Realm
import org.matrix.android.sdk.internal.database.model.ChainEntity


interface ChainUploadService {

    fun insertChain(chain: ChainEntity, call: Realm.Transaction.OnSuccess)

    fun queryChainAll(): ChainEntity?

    fun deleteAll()
}
