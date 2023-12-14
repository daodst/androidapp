

package org.matrix.android.sdk.internal.session.room.draft

import androidx.lifecycle.LiveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.session.room.send.DraftService
import org.matrix.android.sdk.api.session.room.send.UserDraft
import org.matrix.android.sdk.api.util.Optional

internal class DefaultDraftService @AssistedInject constructor(@Assisted private val roomId: String,
                                                               private val draftRepository: DraftRepository,
                                                               private val coroutineDispatchers: MatrixCoroutineDispatchers
) : DraftService {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): DefaultDraftService
    }

    
    override suspend fun saveDraft(draft: UserDraft) {
        withContext(coroutineDispatchers.main) {
            draftRepository.saveDraft(roomId, draft)
        }
    }

    override suspend fun deleteDraft() {
        withContext(coroutineDispatchers.main) {
            draftRepository.deleteDraft(roomId)
        }
    }

    override fun getDraft(): UserDraft? {
        return draftRepository.getDraft(roomId)
    }

    override fun getDraftLive(): LiveData<Optional<UserDraft>> {
        return draftRepository.getDraftsLive(roomId)
    }
}
