
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects

internal open class PushRuleEntity(
        
        var actionsStr: String? = null,
        
        var default: Boolean = false,
        
        var enabled: Boolean = true,
        
        var ruleId: String = "",
        
        var conditions: RealmList<PushConditionEntity>? = RealmList(),
        
        var pattern: String? = null
) : RealmObject() {

    @LinkingObjects("pushRules")
    val parent: RealmResults<PushRulesEntity>? = null

    companion object
}

internal fun PushRuleEntity.deleteOnCascade() {
    conditions?.deleteAllFromRealm()
    deleteFromRealm()
}
