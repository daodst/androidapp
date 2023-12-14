

package org.matrix.android.sdk.api.listeners


interface StepProgressListener {

    sealed class Step {
        data class ComputingKey(val progress: Int, val total: Int) : Step()
        object DownloadingKey : Step()
        data class ImportingKey(val progress: Int, val total: Int) : Step()
    }

    
    fun onStepProgress(step: Step)
}
