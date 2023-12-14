
package org.matrix.android.sdk.internal.session.initsync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.matrix.android.sdk.api.session.initsync.InitSyncStep
import org.matrix.android.sdk.api.session.initsync.SyncStatusService
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class DefaultSyncStatusService @Inject constructor() :
        SyncStatusService,
        ProgressReporter {

    private val status = MutableLiveData<SyncStatusService.Status>()

    private var rootTask: TaskInfo? = null

    override fun getSyncStatusLive(): LiveData<SyncStatusService.Status> {
        return status
    }

    
    fun setStatus(newStatus: SyncStatusService.Status.IncrementalSyncStatus) {
        status.postValue(newStatus)
    }

    
    fun startRoot(initSyncStep: InitSyncStep,
                  totalProgress: Int) {
        endAll()
        rootTask = TaskInfo(initSyncStep, totalProgress, null, 1F)
        reportProgress(0F)
    }

    
    override fun startTask(initSyncStep: InitSyncStep,
                           totalProgress: Int,
                           parentWeight: Float) {
        val currentLeaf = rootTask?.leaf() ?: return
        currentLeaf.child = TaskInfo(
                initSyncStep = initSyncStep,
                totalProgress = totalProgress,
                parent = currentLeaf,
                parentWeight = parentWeight
        )
        reportProgress(0F)
    }

    override fun reportProgress(progress: Float) {
        rootTask?.let { root ->
            root.leaf().let { leaf ->
                
                leaf.setProgress(progress)
                
                status.postValue(SyncStatusService.Status.InitialSyncProgressing(leaf.initSyncStep, root.currentProgress.toInt()))
            }
        }
    }

    override fun endTask() {
        rootTask?.leaf()?.let { endedTask ->
            
            reportProgress(endedTask.totalProgress.toFloat())
            endedTask.parent?.child = null

            if (endedTask.parent != null) {
                
                endedTask.parent.child = null
            } else {
                status.postValue(SyncStatusService.Status.Idle)
            }
        }
    }

    fun endAll() {
        rootTask = null
        status.postValue(SyncStatusService.Status.Idle)
    }
}
