

package org.matrix.android.sdk.internal.crypto.store.db

import android.util.Base64
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmObject
import io.realm.exceptions.RealmFileException
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


internal fun <T> doWithRealm(realmConfiguration: RealmConfiguration, action: (Realm) -> T): T {
    return Realm.getInstance(realmConfiguration).use { realm ->
        action.invoke(realm)
    }
}


internal fun <T : RealmObject> doRealmQueryAndCopy(realmConfiguration: RealmConfiguration, action: (Realm) -> T?): T? {
    return Realm.getInstance(realmConfiguration).use { realm ->
        action.invoke(realm)?.let { realm.copyFromRealm(it) }
    }
}


internal fun <T : RealmObject> doRealmQueryAndCopyList(realmConfiguration: RealmConfiguration, action: (Realm) -> Iterable<T>): Iterable<T> {
    return Realm.getInstance(realmConfiguration).use { realm ->
        action.invoke(realm).let { realm.copyFromRealm(it) }
    }
}




internal fun doRealmTransaction(realmConfiguration: RealmConfiguration, action: (Realm) -> Unit) {
    lateinit var realm:Realm;
    try {
        realm =Realm.getInstance(realmConfiguration)
    } catch (any:Throwable){
        if (any is RealmFileException) {
            val  REALM_NAME = realmConfiguration.realmFileName;
            listOf(REALM_NAME, "$REALM_NAME.lock", "$REALM_NAME.note", "$REALM_NAME.management").forEach { file ->
                try {
                    Timber.i("delete file:${file}")
                    File(realmConfiguration.realmDirectory, file).deleteRecursively()
                } catch (e: Exception) {
                    Timber.e(e, "Unable to delete files")
                }
            }

            Timber.i("re create realm instance")
            realm =Realm.getInstance(realmConfiguration)
        }
    }
    realm.use { realm2 ->
        realm2.executeTransaction { action.invoke(it) }
    }



}

internal fun doRealmTransactionAsync(realmConfiguration: RealmConfiguration, action: (Realm) -> Unit) {
    Realm.getInstance(realmConfiguration).use { realm ->
        realm.executeTransactionAsync { action.invoke(it) }
    }
}


internal fun serializeForRealm(o: Any?): String? {
    if (o == null) {
        return null
    }

    val baos = ByteArrayOutputStream()
    val gzis = GZIPOutputStream(baos)
    val out = ObjectOutputStream(gzis)
    out.use {
        it.writeObject(o)
    }
    return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
}


@Suppress("UNCHECKED_CAST")
internal fun <T> deserializeFromRealm(string: String?): T? {
    if (string == null) {
        return null
    }
    val decodedB64 = Base64.decode(string.toByteArray(), Base64.DEFAULT)

    val bais = decodedB64.inputStream()
    val gzis = GZIPInputStream(bais)
    val ois = SafeObjectInputStream(gzis)
    return ois.use {
        it.readObject() as T
    }
}
