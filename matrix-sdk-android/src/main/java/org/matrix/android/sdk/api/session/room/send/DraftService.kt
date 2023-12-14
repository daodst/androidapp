

package org.matrix.android.sdk.api.session.room.send

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.util.Optional

interface DraftService {

    
    suspend fun saveDraft(draft: UserDraft)

    
    suspend fun deleteDraft()

    
    fun getDraft(): UserDraft?

    
    fun getDraftLive(): LiveData<Optional<UserDraft>>
}
