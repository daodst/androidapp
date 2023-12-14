

package org.matrix.android.sdk.internal.session.group

import androidx.lifecycle.LiveData
import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.RealmQuery
import org.matrix.android.sdk.api.session.group.Group
import org.matrix.android.sdk.api.session.group.GroupService
import org.matrix.android.sdk.api.session.group.GroupSummaryQueryParams
import org.matrix.android.sdk.api.session.group.model.GroupSummary
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.GroupEntity
import org.matrix.android.sdk.internal.database.model.GroupSummaryEntity
import org.matrix.android.sdk.internal.database.model.GroupSummaryEntityFields
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.query.QueryStringValueProcessor
import org.matrix.android.sdk.internal.query.process
import org.matrix.android.sdk.internal.util.fetchCopyMap
import javax.inject.Inject

internal class DefaultGroupService @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy,
        private val groupFactory: GroupFactory,
        private val queryStringValueProcessor: QueryStringValueProcessor,
) : GroupService {

    override fun getGroup(groupId: String): Group? {
        return Realm.getInstance(monarchy.realmConfiguration).use { realm ->
            GroupEntity.where(realm, groupId).findFirst()?.let {
                groupFactory.create(groupId)
            }
        }
    }

    override fun getGroupSummary(groupId: String): GroupSummary? {
        return monarchy.fetchCopyMap(
                { realm -> GroupSummaryEntity.where(realm, groupId).findFirst() },
                { it, _ -> it.asDomain() }
        )
    }

    override fun getGroupSummaries(groupSummaryQueryParams: GroupSummaryQueryParams): List<GroupSummary> {
        return monarchy.fetchAllMappedSync(
                { groupSummariesQuery(it, groupSummaryQueryParams) },
                { it.asDomain() }
        )
    }

    override fun getGroupSummariesLive(groupSummaryQueryParams: GroupSummaryQueryParams): LiveData<List<GroupSummary>> {
        return monarchy.findAllMappedWithChanges(
                { groupSummariesQuery(it, groupSummaryQueryParams) },
                { it.asDomain() }
        )
    }

    private fun groupSummariesQuery(realm: Realm, queryParams: GroupSummaryQueryParams): RealmQuery<GroupSummaryEntity> {
        return with(queryStringValueProcessor) {
            GroupSummaryEntity.where(realm)
                    .process(GroupSummaryEntityFields.DISPLAY_NAME, queryParams.displayName)
                    .process(GroupSummaryEntityFields.MEMBERSHIP_STR, queryParams.memberships)
        }
    }
}
