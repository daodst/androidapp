

package org.matrix.android.sdk.internal.util.database

import io.realm.DynamicRealm
import io.realm.RealmObjectSchema
import timber.log.Timber

internal abstract class RealmMigrator(private val realm: DynamicRealm,
                                      private val targetSchemaVersion: Int) {
    fun perform() {
        Timber.d("Migrate ${realm.configuration.realmFileName} to $targetSchemaVersion")
        doMigrate(realm)
    }

    abstract fun doMigrate(realm: DynamicRealm)

    protected fun RealmObjectSchema.addFieldIfNotExists(fieldName: String, fieldType: Class<*>): RealmObjectSchema {
        if (!hasField(fieldName)) {
            addField(fieldName, fieldType)
        }
        return this
    }

    protected fun RealmObjectSchema.removeFieldIfExists(fieldName: String): RealmObjectSchema {
        if (hasField(fieldName)) {
            removeField(fieldName)
        }
        return this
    }

    protected fun RealmObjectSchema.setRequiredIfNotAlready(fieldName: String, isRequired: Boolean): RealmObjectSchema {
        if (isRequired != isRequired(fieldName)) {
            setRequired(fieldName, isRequired)
        }
        return this
    }
}
