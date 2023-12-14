

package org.matrix.android.sdk.api.session.file

interface ContentDownloadStateTracker {
    fun track(key: String, updateListener: UpdateListener)
    fun unTrack(key: String, updateListener: UpdateListener)
    fun clear()

    sealed class State {
        object Idle : State()
        data class Downloading(val current: Long, val total: Long, val indeterminate: Boolean) : State()
        object Decrypting : State()
        object Success : State()
        data class Failure(val errorCode: Int) : State()
    }

    interface UpdateListener {
        fun onDownloadStateUpdate(state: State)
    }
}
