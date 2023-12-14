
package org.matrix.android.sdk.internal.database

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

internal fun <T> CoroutineScope.asyncTransaction(monarchy: Monarchy, transaction: suspend (realm: Realm) -> T) {
    asyncTransaction(monarchy.realmConfiguration, transaction)
}

internal fun <T> CoroutineScope.asyncTransaction(realmConfiguration: RealmConfiguration, transaction: suspend (realm: Realm) -> T) {
    launch {
        awaitTransaction(realmConfiguration, transaction)
    }
}

internal suspend fun <T> awaitTransaction(config: RealmConfiguration, transaction: suspend (realm: Realm) -> T): T {
    return withContext(Realm.WRITE_EXECUTOR.asCoroutineDispatcher()) {
        Realm.getInstance(config).use { bgRealm ->
            bgRealm.beginTransaction()
            val result: T
            try {
                val start = System.currentTimeMillis()
                result = transaction(bgRealm)
                if (isActive) {
                    bgRealm.commitTransaction()
                    val end = System.currentTimeMillis()
                    val time = end - start
                    Timber.v("Execute transaction in $time millis")
                }
            } finally {
                if (bgRealm.isInTransaction) {
                    bgRealm.cancelTransaction()
                }
            }
            result
        }
    }
}
