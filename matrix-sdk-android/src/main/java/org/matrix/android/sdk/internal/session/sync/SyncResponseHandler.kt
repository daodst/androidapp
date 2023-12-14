

package org.matrix.android.sdk.internal.session.sync

import androidx.work.ExistingPeriodicWorkPolicy
import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.pushrules.PushRuleService
import org.matrix.android.sdk.api.pushrules.RuleScope
import org.matrix.android.sdk.api.session.initsync.InitSyncStep
import org.matrix.android.sdk.api.session.sync.model.GroupsSyncResponse
import org.matrix.android.sdk.api.session.sync.model.RoomsSyncResponse
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.DefaultCryptoService
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.di.WorkManagerProvider
import org.matrix.android.sdk.internal.session.SessionListeners
import org.matrix.android.sdk.internal.session.dispatchTo
import org.matrix.android.sdk.internal.session.group.GetGroupDataWorker
import org.matrix.android.sdk.internal.session.initsync.ProgressReporter
import org.matrix.android.sdk.internal.session.initsync.reportSubtask
import org.matrix.android.sdk.internal.session.notification.ProcessEventForPushTask
import org.matrix.android.sdk.internal.session.sync.handler.CryptoSyncHandler
import org.matrix.android.sdk.internal.session.sync.handler.GroupSyncHandler
import org.matrix.android.sdk.internal.session.sync.handler.PresenceSyncHandler
import org.matrix.android.sdk.internal.session.sync.handler.SyncResponsePostTreatmentAggregatorHandler
import org.matrix.android.sdk.internal.session.sync.handler.UserAccountDataSyncHandler
import org.matrix.android.sdk.internal.session.sync.handler.room.RoomSyncHandler
import org.matrix.android.sdk.internal.util.awaitTransaction
import org.matrix.android.sdk.internal.worker.WorkerParamsFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.system.measureTimeMillis

private const val GET_GROUP_DATA_WORKER = "GET_GROUP_DATA_WORKER"

internal class SyncResponseHandler @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy,
        @SessionId private val sessionId: String,
        private val sessionManager: SessionManager,
        private val sessionListeners: SessionListeners,
        private val workManagerProvider: WorkManagerProvider,
        private val roomSyncHandler: RoomSyncHandler,
        private val userAccountDataSyncHandler: UserAccountDataSyncHandler,
        private val groupSyncHandler: GroupSyncHandler,
        private val cryptoSyncHandler: CryptoSyncHandler,
        private val aggregatorHandler: SyncResponsePostTreatmentAggregatorHandler,
        private val cryptoService: DefaultCryptoService,
        private val tokenStore: SyncTokenStore,
        private val processEventForPushTask: ProcessEventForPushTask,
        private val pushRuleService: PushRuleService,
        private val presenceSyncHandler: PresenceSyncHandler
) {

    suspend fun handleResponse(syncResponse: SyncResponse,
                               fromToken: String?,
                               reporter: ProgressReporter?,
                               updateSince: Boolean = true) {
        val isInitialSync = fromToken == null
        Timber.v("Start handling sync, is InitialSync: $isInitialSync")

        measureTimeMillis {
            if (!cryptoService.isStarted()) {
                Timber.v("Should start cryptoService")
                cryptoService.start()
            }
            cryptoService.onSyncWillProcess(isInitialSync)
        }.also {
            Timber.v("Finish handling start cryptoService in $it ms")
        }

        
        
        measureTimeMillis {
            Timber.v("Handle toDevice")
            reportSubtask(reporter, InitSyncStep.ImportingAccountCrypto, 100, 0.1f) {
                if (syncResponse.toDevice != null) {
                    cryptoSyncHandler.handleToDevice(syncResponse.toDevice, reporter)
                }
            }
        }.also {
            Timber.v("Finish handling toDevice in $it ms")
        }
        val aggregator = SyncResponsePostTreatmentAggregator()

        

        
        monarchy.awaitTransaction { realm ->
            
            measureTimeMillis {
                Timber.v("Handle rooms")
                reportSubtask(reporter, InitSyncStep.ImportingAccountRoom, 1, 0.7f) {
                    if (syncResponse.rooms != null) {
                        roomSyncHandler.handle(realm, syncResponse.rooms, isInitialSync, aggregator, reporter)
                    }
                }
            }.also {
                Timber.v("Finish handling rooms in $it ms")
            }

            measureTimeMillis {
                reportSubtask(reporter, InitSyncStep.ImportingAccountGroups, 1, 0.1f) {
                    Timber.v("Handle groups")
                    if (syncResponse.groups != null) {
                        groupSyncHandler.handle(realm, syncResponse.groups, reporter)
                    }
                }
            }.also {
                Timber.v("Finish handling groups in $it ms")
            }

            measureTimeMillis {
                reportSubtask(reporter, InitSyncStep.ImportingAccountData, 1, 0.1f) {
                    Timber.v("Handle accountData")
                    userAccountDataSyncHandler.handle(realm, syncResponse.accountData)
                }
            }.also {
                Timber.v("Finish handling accountData in $it ms")
            }

            measureTimeMillis {
                Timber.v("Handle Presence")
                presenceSyncHandler.handle(realm, syncResponse.presence)
            }.also {
                Timber.v("Finish handling Presence in $it ms")
            }
            if (updateSince) {
                tokenStore.saveToken(realm, syncResponse.nextBatch)
            }
        }

        
        aggregatorHandler.handle(aggregator)

        syncResponse.rooms?.let {
            checkPushRules(it, isInitialSync)
            userAccountDataSyncHandler.synchronizeWithServerIfNeeded(it.invite)
            dispatchInvitedRoom(it)
        }
        syncResponse.groups?.let {
            scheduleGroupDataFetchingIfNeeded(it)
        }

        Timber.v("On sync completed")
        cryptoSyncHandler.onSyncCompleted(syncResponse)

        
        monarchy.writeAsync {
            roomSyncHandler.postSyncSpaceHierarchyHandle(it)
        }
    }

    private fun dispatchInvitedRoom(roomsSyncResponse: RoomsSyncResponse) {
        val session = sessionManager.getSessionComponent(sessionId)?.session()
        roomsSyncResponse.invite.keys.forEach { roomId ->
            session.dispatchTo(sessionListeners) { session, listener ->
                listener.onNewInvitedRoom(session, roomId)
            }
        }
    }

    
    private fun scheduleGroupDataFetchingIfNeeded(groupsSyncResponse: GroupsSyncResponse) {
        val groupIds = ArrayList<String>()
        groupIds.addAll(groupsSyncResponse.join.keys)
        groupIds.addAll(groupsSyncResponse.invite.keys)
        if (groupIds.isEmpty()) {
            Timber.v("No new groups to fetch data for.")
            return
        }
        Timber.v("There are ${groupIds.size} new groups to fetch data for.")
        val getGroupDataWorkerParams = GetGroupDataWorker.Params(sessionId)
        val workData = WorkerParamsFactory.toData(getGroupDataWorkerParams)

        val getGroupWork = workManagerProvider.matrixPeriodicWorkRequestBuilder<GetGroupDataWorker>(1, TimeUnit.HOURS)
                .setInputData(workData)
                .setConstraints(WorkManagerProvider.workConstraints)
                .build()

        workManagerProvider.workManager
                .enqueueUniquePeriodicWork(GET_GROUP_DATA_WORKER, ExistingPeriodicWorkPolicy.REPLACE, getGroupWork)
    }

    private suspend fun checkPushRules(roomsSyncResponse: RoomsSyncResponse, isInitialSync: Boolean) {
        Timber.v("[PushRules] --> checkPushRules")
        if (isInitialSync) {
            Timber.v("[PushRules] <-- No push rule check on initial sync")
            return
        } 

        val rules = pushRuleService.getPushRules(RuleScope.GLOBAL).getAllRules()
        processEventForPushTask.execute(ProcessEventForPushTask.Params(roomsSyncResponse, rules))
        Timber.v("[PushRules] <-- Push task scheduled")
    }
}
