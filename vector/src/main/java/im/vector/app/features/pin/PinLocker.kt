

package im.vector.app.features.pin

import android.os.SystemClock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import im.vector.app.features.settings.VectorPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val PERIOD_OF_GRACE_IN_MS = 2 * 60 * 1000L


@Singleton
class PinLocker @Inject constructor(
        private val pinCodeStore: PinCodeStore,
        private val vectorPreferences: VectorPreferences
) : DefaultLifecycleObserver {

    enum class State {
        
        LOCKED,

        
        UNLOCKED
    }

    private val liveState = MutableLiveData<State>()

    private var shouldBeLocked = true
    private var entersBackgroundTs = 0L

    fun getLiveState(): LiveData<State> {
        return liveState
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private fun computeState() {
        GlobalScope.launch {
            val state = if (shouldBeLocked && pinCodeStore.hasEncodedPin()) {
                State.LOCKED
            } else {
                State.UNLOCKED
            }
                    .also { Timber.v("New state: $it") }

            if (liveState.value != state) {
                liveState.postValue(state)
            }
        }
    }

    fun unlock() {
        Timber.v("Unlock app")
        shouldBeLocked = false
        computeState()
    }

    fun screenIsOff() {
        shouldBeLocked = true
        computeState()
    }

    override fun onResume(owner: LifecycleOwner) {
        val timeElapsedSinceBackground = SystemClock.elapsedRealtime() - entersBackgroundTs
        shouldBeLocked = shouldBeLocked || timeElapsedSinceBackground >= getGracePeriod()
        Timber.v("App enters foreground after $timeElapsedSinceBackground ms spent in background shouldBeLocked: $shouldBeLocked")
        computeState()
    }

    override fun onPause(owner: LifecycleOwner) {
        Timber.v("App enters background")
        entersBackgroundTs = SystemClock.elapsedRealtime()
    }

    private fun getGracePeriod(): Long {
        return if (vectorPreferences.useGracePeriod()) {
            PERIOD_OF_GRACE_IN_MS
        } else {
            0L
        }
    }
}
