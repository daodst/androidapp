

package org.matrix.android.sdk.internal.database

import io.realm.Realm
import java.io.Closeable

internal class RealmInstanceWrapper(private val realm: Realm, private val closeRealmOnClose: Boolean) : Closeable {

    override fun close() {
        if (closeRealmOnClose) {
            realm.close()
        }
    }

    fun <R> withRealm(block: (Realm) -> R): R {
        return use {
            block(it.realm)
        }
    }
}
