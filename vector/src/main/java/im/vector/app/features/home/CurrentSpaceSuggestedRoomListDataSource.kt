

package im.vector.app.features.home

import im.vector.app.core.utils.BehaviorDataSource
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSpaceSuggestedRoomListDataSource @Inject constructor() : BehaviorDataSource<List<SpaceChildInfo>>()
