
package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import org.matrix.android.sdk.api.pushrules.RuleKind
import org.matrix.android.sdk.internal.extensions.clearWith

internal open class PushRulesEntity(
        var scope: String = "",
        var pushRules: RealmList<PushRuleEntity> = RealmList()
) : RealmObject() {

    private var kindStr: String = RuleKind.CONTENT.name
    var kind: RuleKind
        get() {
            return RuleKind.valueOf(kindStr)
        }
        set(value) {
            kindStr = value.name
        }

    companion object
}

internal fun PushRulesEntity.deleteOnCascade() {
    pushRules.clearWith { it.deleteOnCascade() }
    deleteFromRealm()
}
