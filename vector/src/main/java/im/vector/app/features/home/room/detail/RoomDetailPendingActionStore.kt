

package im.vector.app.features.home.room.detail

import im.vector.app.core.utils.TemporaryStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomDetailPendingActionStore @Inject constructor() : TemporaryStore<RoomDetailPendingAction>(10_000)
