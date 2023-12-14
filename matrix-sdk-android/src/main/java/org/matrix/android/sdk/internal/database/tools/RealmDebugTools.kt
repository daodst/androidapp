

package org.matrix.android.sdk.internal.database.tools

import io.realm.Realm
import io.realm.RealmConfiguration
import org.matrix.android.sdk.BuildConfig
import timber.log.Timber

internal class RealmDebugTools(
        private val realmConfiguration: RealmConfiguration
) {
    
    fun logInfo(baseName: String) {
        buildString {
            append("\n$baseName Realm located at : ${realmConfiguration.realmDirectory}/${realmConfiguration.realmFileName}")

            if (BuildConfig.LOG_PRIVATE_DATA) {
                val key = realmConfiguration.encryptionKey.joinToString("") { byte -> "%02x".format(byte) }
                append("\n$baseName Realm encryption key : $key")
            }

            Realm.getInstance(realmConfiguration).use { realm ->
                
                separator()
                separator()
                append("\n$baseName Realm is empty: ${realm.isEmpty}")
                var total = 0L
                val maxNameLength = realmConfiguration.realmObjectClasses.maxOf { it.simpleName.length }
                realmConfiguration.realmObjectClasses.forEach { modelClazz ->
                    val count = realm.where(modelClazz).count()
                    total += count
                    append("\n$baseName Realm - count ${modelClazz.simpleName.padEnd(maxNameLength)} : $count")
                }
                separator()
                append("\n$baseName Realm - total count: $total")
                separator()
                separator()
            }
        }
                .let { Timber.i(it) }
    }

    private fun StringBuilder.separator() = append("\n==============================================")
}
