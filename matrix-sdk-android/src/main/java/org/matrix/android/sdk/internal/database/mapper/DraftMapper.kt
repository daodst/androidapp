

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.session.room.send.UserDraft
import org.matrix.android.sdk.internal.database.model.DraftEntity


internal object DraftMapper {

    fun map(entity: DraftEntity): UserDraft {
        return when (entity.draftMode) {
            DraftEntity.MODE_REGULAR -> UserDraft.Regular(entity.content)
            DraftEntity.MODE_EDIT    -> UserDraft.Edit(entity.linkedEventId, entity.content)
            DraftEntity.MODE_QUOTE   -> UserDraft.Quote(entity.linkedEventId, entity.content)
            DraftEntity.MODE_REPLY   -> UserDraft.Reply(entity.linkedEventId, entity.content)
            DraftEntity.MODE_VOICE   -> UserDraft.Voice(entity.content)
            else                     -> null
        } ?: UserDraft.Regular("")
    }

    fun map(domain: UserDraft): DraftEntity {
        return when (domain) {
            is UserDraft.Regular -> DraftEntity(content = domain.content, draftMode = DraftEntity.MODE_REGULAR, linkedEventId = "")
            is UserDraft.Edit    -> DraftEntity(content = domain.content, draftMode = DraftEntity.MODE_EDIT, linkedEventId = domain.linkedEventId)
            is UserDraft.Quote   -> DraftEntity(content = domain.content, draftMode = DraftEntity.MODE_QUOTE, linkedEventId = domain.linkedEventId)
            is UserDraft.Reply   -> DraftEntity(content = domain.content, draftMode = DraftEntity.MODE_REPLY, linkedEventId = domain.linkedEventId)
            is UserDraft.Voice   -> DraftEntity(content = domain.content, draftMode = DraftEntity.MODE_VOICE, linkedEventId = "")
        }
    }
}
