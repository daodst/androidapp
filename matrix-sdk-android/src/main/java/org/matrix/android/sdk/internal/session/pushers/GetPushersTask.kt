
package org.matrix.android.sdk.internal.session.pushers

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.session.pushers.PusherState
import org.matrix.android.sdk.internal.database.mapper.toEntity
import org.matrix.android.sdk.internal.database.model.PusherEntity
import org.matrix.android.sdk.internal.database.model.deleteOnCascade
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import javax.inject.Inject

internal interface GetPushersTask : Task<Unit, Unit>

internal class DefaultGetPushersTask @Inject constructor(
        private val pushersAPI: PushersAPI,
        @SessionDatabase private val monarchy: Monarchy,
        private val globalErrorReceiver: GlobalErrorReceiver
) : GetPushersTask {

    override suspend fun execute(params: Unit) {
        val response = executeRequest(globalErrorReceiver) {
            pushersAPI.getPushers()
        }
        monarchy.awaitTransaction { realm ->
            
            realm.where(PusherEntity::class.java)
                    .findAll()
                    .forEach { it.deleteOnCascade() }
            response.pushers?.forEach { jsonPusher ->
                jsonPusher.toEntity().also {
                    it.state = PusherState.REGISTERED
                    realm.insertOrUpdate(it)
                }
            }
        }
    }
}
