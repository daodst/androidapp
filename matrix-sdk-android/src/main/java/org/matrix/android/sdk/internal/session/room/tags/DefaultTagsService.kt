

package org.matrix.android.sdk.internal.session.room.tags

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.matrix.android.sdk.api.session.room.tags.TagsService

internal class DefaultTagsService @AssistedInject constructor(
        @Assisted private val roomId: String,
        private val addTagToRoomTask: AddTagToRoomTask,
        private val deleteTagFromRoomTask: DeleteTagFromRoomTask
) : TagsService {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): DefaultTagsService
    }

    override suspend fun addTag(tag: String, order: Double?) {
        val params = AddTagToRoomTask.Params(roomId, tag, order)
        addTagToRoomTask.execute(params)
    }

    override suspend fun deleteTag(tag: String) {
        val params = DeleteTagFromRoomTask.Params(roomId, tag)
        deleteTagFromRoomTask.execute(params)
    }
}
