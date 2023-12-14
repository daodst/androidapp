

package im.vector.app.features.home.room.detail.timeline.helper

import android.os.Handler
import android.os.Looper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioMessagePlaybackTracker @Inject constructor() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val listeners = mutableMapOf<String, Listener>()
    private val activityListeners = mutableListOf<ActivityListener>()
    private val states = mutableMapOf<String, Listener.State>()

    fun trackActivity(listener: ActivityListener) {
        activityListeners.add(listener)
    }

    fun untrackActivity(listener: ActivityListener) {
        activityListeners.remove(listener)
    }

    fun track(id: String, listener: Listener) {
        listeners[id] = listener

        val currentState = states[id] ?: Listener.State.Idle
        mainHandler.post {
            listener.onUpdate(currentState)
        }
    }

    fun untrack(id: String) {
        listeners.remove(id)
    }

    fun pauseAllPlaybacks() {
        listeners.keys.forEach { key ->
            pausePlayback(key)
        }
    }

    fun makeAllPlaybacksIdle() {
        listeners.keys.forEach { key ->
            setState(key, Listener.State.Idle)
        }
    }

    
    private fun setState(key: String, state: Listener.State) {
        states[key] = state
        val isPlayingOrRecording = states.values.any { it is Listener.State.Playing || it is Listener.State.Recording }
        mainHandler.post {
            listeners[key]?.onUpdate(state)
            activityListeners.forEach { it.onUpdate(isPlayingOrRecording) }
        }
    }

    fun startPlayback(id: String) {
        val currentPlaybackTime = getPlaybackTime(id)
        val currentPercentage = getPercentage(id)
        val currentState = Listener.State.Playing(currentPlaybackTime, currentPercentage)
        setState(id, currentState)
        
        states
                .filter { it.key != id }
                .keys
                .forEach { key ->
                    val state = states[key]
                    if (state is Listener.State.Playing) {
                        
                        setState(key, Listener.State.Idle)
                    }
                }
    }

    fun pausePlayback(id: String) {
        if (getPlaybackState(id) is Listener.State.Playing) {
            val currentPlaybackTime = getPlaybackTime(id)
            val currentPercentage = getPercentage(id)
            setState(id, Listener.State.Paused(currentPlaybackTime, currentPercentage))
        }
    }

    fun stopPlayback(id: String) {
        setState(id, Listener.State.Idle)
    }

    fun updatePlayingAtPlaybackTime(id: String, time: Int, percentage: Float) {
        setState(id, Listener.State.Playing(time, percentage))
    }

    fun updatePausedAtPlaybackTime(id: String, time: Int, percentage: Float) {
        setState(id, Listener.State.Paused(time, percentage))
    }

    fun updateCurrentRecording(id: String, amplitudeList: List<Int>) {
        setState(id, Listener.State.Recording(amplitudeList))
    }

    fun getPlaybackState(id: String) = states[id]

    fun getPlaybackTime(id: String): Int {
        return when (val state = states[id]) {
            is Listener.State.Playing -> state.playbackTime
            is Listener.State.Paused  -> state.playbackTime
            
            else                      -> 0
        }
    }

    private fun getPercentage(id: String): Float {
        return when (val state = states[id]) {
            is Listener.State.Playing -> state.percentage
            is Listener.State.Paused  -> state.percentage
            
            else                      -> 0f
        }
    }

    fun clear() {
        listeners.forEach {
            it.value.onUpdate(Listener.State.Idle)
        }
        listeners.clear()
        states.clear()
    }

    companion object {
        const val RECORDING_ID = "RECORDING_ID"
    }

    interface Listener {

        fun onUpdate(state: State)

        sealed class State {
            object Idle : State()
            data class Playing(val playbackTime: Int, val percentage: Float) : State()
            data class Paused(val playbackTime: Int, val percentage: Float) : State()
            data class Recording(val amplitudeList: List<Int>) : State()
        }
    }

    fun interface ActivityListener {
        fun onUpdate(isPlayingOrRecording: Boolean)
    }
}
