

package org.matrix.android.sdk.internal.database

import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmConfiguration
import io.realm.RealmQuery
import io.realm.RealmResults
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

internal suspend fun <T> awaitNotEmptyResult(realmConfiguration: RealmConfiguration,
                                             timeoutMillis: Long,
                                             builder: (Realm) -> RealmQuery<T>) {
    withTimeout(timeoutMillis) {
        
        withContext(Dispatchers.Main) {
            val latch = CompletableDeferred<Unit>()

            Realm.getInstance(realmConfiguration).use { realm ->
                val result = builder(realm).findAllAsync()

                val listener = object : RealmChangeListener<RealmResults<T>> {
                    override fun onChange(it: RealmResults<T>) {
                        if (it.isNotEmpty()) {
                            result.removeChangeListener(this)
                            latch.complete(Unit)
                        }
                    }
                }

                result.addChangeListener(listener)
                try {
                    latch.await()
                } catch (e: CancellationException) {
                    result.removeChangeListener(listener)
                    throw e
                }
            }
        }
    }
}
