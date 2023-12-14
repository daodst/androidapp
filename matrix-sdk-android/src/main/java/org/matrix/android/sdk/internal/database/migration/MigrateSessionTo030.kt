

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.ChatPhoneLogFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator


internal class MigrateSessionTo030(realm: DynamicRealm) : RealmMigrator(realm, 30) {

    override fun doMigrate(realm: DynamicRealm) {
        realm.schema.create("ChatPhoneLog")
               .addField(ChatPhoneLogFields.BEST_NAME, String::class.java)
               .addField(ChatPhoneLogFields.TIME, Long::class.java)
               .addField(ChatPhoneLogFields.FORMATTED_DURATION, String::class.java)
               .addField(ChatPhoneLogFields.ADDRESS, String::class.java)
               .addField(ChatPhoneLogFields.PHONE, String::class.java)
               .addField(ChatPhoneLogFields.INCOME, Int::class.java)
               .addField(ChatPhoneLogFields.STATUS, Int::class.java)
    }
}
