

package org.matrix.android.sdk.api.session.content

interface ContentUploadStateTracker {

    fun track(key: String, updateListener: UpdateListener)

    fun untrack(key: String, updateListener: UpdateListener)

    fun clear()

    interface UpdateListener {
        fun onUpdate(state: State)
    }

    sealed class State {
        object Idle : State()
        object EncryptingThumbnail : State()
        object CompressingImage : State()
        data class CompressingVideo(val percent: Float) : State()
        data class UploadingThumbnail(val current: Long, val total: Long) : State()
        data class Encrypting(val current: Long, val total: Long) : State()
        data class Uploading(val current: Long, val total: Long) : State()
        object Success : State()
        data class Failure(val throwable: Throwable) : State()
    }
}
