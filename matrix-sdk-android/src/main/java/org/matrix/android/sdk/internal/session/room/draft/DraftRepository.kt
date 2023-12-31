

package org.matrix.android.sdk.internal.session.room.draft

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.kotlin.createObject
import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.session.room.send.UserDraft
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.api.util.toOptional
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.mapper.DraftMapper
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.UserDraftsEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.util.awaitTransaction
import timber.log.Timber
import javax.inject.Inject

internal class DraftRepository @Inject constructor(@SessionDatabase private val monarchy: Monarchy,
                                                   private val realmSessionProvider: RealmSessionProvider) {

    suspend fun saveDraft(roomId: String, userDraft: UserDraft) {
        monarchy.awaitTransaction {
            saveDraftInDb(it, userDraft, roomId)
        }
    }

    suspend fun deleteDraft(roomId: String) {
        monarchy.awaitTransaction {
            deleteDraftFromDb(it, roomId)
        }
    }

    fun getDraft(roomId: String): UserDraft? {
        return realmSessionProvider.withRealm { realm ->
            UserDraftsEntity.where(realm, roomId).findFirst()
                    ?.userDrafts
                    ?.firstOrNull()
                    ?.let {
                        DraftMapper.map(it)
                    }
        }
    }

    fun getDraftsLive(roomId: String): LiveData<Optional<UserDraft>> {
        val liveData = monarchy.findAllMappedWithChanges(
                { UserDraftsEntity.where(it, roomId) },
                {
                    it.userDrafts.map { draft ->
                        DraftMapper.map(draft)
                    }
                }
        )
        return Transformations.map(liveData) {
            it.firstOrNull()?.firstOrNull().toOptional()
        }
    }

    private fun deleteDraftFromDb(realm: Realm, roomId: String) {
        UserDraftsEntity.where(realm, roomId).findFirst()?.userDrafts?.clear()
    }

    private fun saveDraftInDb(realm: Realm, draft: UserDraft, roomId: String) {
        val roomSummaryEntity = RoomSummaryEntity.where(realm, roomId).findFirst()
                ?: realm.createObject(roomId)

        val userDraftsEntity = roomSummaryEntity.userDrafts
                ?: realm.createObject<UserDraftsEntity>().also {
                    roomSummaryEntity.userDrafts = it
                }

        userDraftsEntity.let { userDraftEntity ->
            
            if (draft.isValid()) {
                
                val newDraft = DraftMapper.map(draft)
                Timber.d("Draft: create a new draft ${privacySafe(draft)}")
                userDraftEntity.userDrafts.clear()
                userDraftEntity.userDrafts.add(newDraft)
            } else {
                
                Timber.d("Draft: delete a draft")
                val topDraft = userDraftEntity.userDrafts.lastOrNull()
                if (topDraft == null) {
                    Timber.d("Draft: nothing to do")
                } else {
                    
                    Timber.d("Draft: remove the top draft")
                    userDraftEntity.userDrafts.remove(topDraft)
                }
            }
        }
    }

    private fun privacySafe(o: Any): Any {
        if (BuildConfig.LOG_PRIVATE_DATA) {
            return o
        }
        return ""
    }
}
