

package org.matrix.android.sdk.internal.session.pushers

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import org.matrix.android.sdk.api.session.pushers.PusherState
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.PusherEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import javax.inject.Inject

internal interface RemovePusherTask : Task<RemovePusherTask.Params, Unit> {
    data class Params(val pushKey: String,
                      val pushAppId: String)
}

internal class DefaultRemovePusherTask @Inject constructor(
        private val pushersAPI: PushersAPI,
        @SessionDatabase private val monarchy: Monarchy,
        private val globalErrorReceiver: GlobalErrorReceiver
) : RemovePusherTask {

    override suspend fun execute(params: RemovePusherTask.Params) {
        monarchy.awaitTransaction { realm ->
            val existingEntity = PusherEntity.where(realm, params.pushKey).findFirst()
            existingEntity?.state = PusherState.UNREGISTERING
        }

        val existing = Realm.getInstance(monarchy.realmConfiguration).use { realm ->
            PusherEntity.where(realm, params.pushKey).findFirst()?.asDomain()
        } ?: throw Exception("No existing pusher")

        val deleteBody = JsonPusher(
                pushKey = params.pushKey,
                appId = params.pushAppId,
                
                kind = null,
                appDisplayName = existing.appDisplayName ?: "",
                deviceDisplayName = existing.deviceDisplayName ?: "",
                profileTag = existing.profileTag ?: "",
                lang = existing.lang,
                data = JsonPusherData(existing.data.url, existing.data.format),
                append = false
        )
        executeRequest(globalErrorReceiver) {
            pushersAPI.setPusher(deleteBody)
        }
        monarchy.awaitTransaction {
            PusherEntity.where(it, params.pushKey).findFirst()?.deleteFromRealm()
        }
    }
}
