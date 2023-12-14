

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.matrix.android.sdk.api.session.room.model.Membership

internal open class GroupSummaryEntity(@PrimaryKey var groupId: String = "",
                                       var displayName: String = "",
                                       var shortDescription: String = "",
                                       var avatarUrl: String = "",
                                       var roomIds: RealmList<String> = RealmList(),
                                       var userIds: RealmList<String> = RealmList()
) : RealmObject() {

    private var membershipStr: String = Membership.NONE.name
    var membership: Membership
        get() {
            return Membership.valueOf(membershipStr)
        }
        set(value) {
            membershipStr = value.name
        }

    companion object
}
