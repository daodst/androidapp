

package org.matrix.android.sdk.internal.session.cache

import io.realm.RealmConfiguration
import org.matrix.android.sdk.internal.database.awaitTransaction
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface ClearCacheTask : Task<Unit, Unit>

internal class RealmClearCacheTask @Inject constructor(private val realmConfiguration: RealmConfiguration) : ClearCacheTask {

    override suspend fun execute(params: Unit) {
        awaitTransaction(realmConfiguration) {
            it.deleteAll()
        }
    }
}
