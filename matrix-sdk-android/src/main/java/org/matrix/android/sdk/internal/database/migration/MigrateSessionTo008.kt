

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.EditAggregatedSummaryEntityFields
import org.matrix.android.sdk.internal.database.model.EditionOfEventFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo008(realm: DynamicRealm) : RealmMigrator(realm, 8) {

    override fun doMigrate(realm: DynamicRealm) {
        val editionOfEventSchema = realm.schema.create("EditionOfEvent")
                .addField(EditionOfEventFields.CONTENT, String::class.java)
                .addField(EditionOfEventFields.EVENT_ID, String::class.java)
                .setRequired(EditionOfEventFields.EVENT_ID, true)
                .addField(EditionOfEventFields.SENDER_ID, String::class.java)
                .setRequired(EditionOfEventFields.SENDER_ID, true)
                .addField(EditionOfEventFields.TIMESTAMP, Long::class.java)
                .addField(EditionOfEventFields.IS_LOCAL_ECHO, Boolean::class.java)

        realm.schema.get("EditAggregatedSummaryEntity")
                ?.removeField("aggregatedContent")
                ?.removeField("sourceEvents")
                ?.removeField("lastEditTs")
                ?.removeField("sourceLocalEchoEvents")
                ?.addRealmListField(EditAggregatedSummaryEntityFields.EDITIONS.`$`, editionOfEventSchema)

        
        
        editionOfEventSchema.isEmbedded = true
    }
}
