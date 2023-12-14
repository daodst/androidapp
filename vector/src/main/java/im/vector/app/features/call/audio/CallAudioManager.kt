

package im.vector.app.features.call.audio

import android.content.Context
import android.media.AudioManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import im.vector.app.R
import org.matrix.android.sdk.api.extensions.orFalse
import timber.log.Timber
import java.util.concurrent.Executors

class CallAudioManager(private val context: Context, val configChange: (() -> Unit)?) {

    private val audioManager: AudioManager? = context.getSystemService()
    private var audioDeviceDetector: AudioDeviceDetector? = null
    private var audioDeviceRouter: AudioDeviceRouter? = null

    sealed class Device(@StringRes val titleRes: Int, @DrawableRes val drawableRes: Int) {
        object Phone : Device(R.string.sound_device_phone, R.drawable.ic_sound_device_phone)
        object Speaker : Device(R.string.sound_device_speaker, R.drawable.ic_sound_device_speaker)
        object Headset : Device(R.string.sound_device_headset, R.drawable.ic_sound_device_headphone)
        data class WirelessHeadset(val name: String?) : Device(R.string.sound_device_wireless_headset, R.drawable.ic_sound_device_wireless)
    }

    enum class Mode {
        DEFAULT,
        AUDIO_CALL,
        VIDEO_CALL
    }

    private var mode = Mode.DEFAULT
    private var _availableDevices: MutableSet<Device> = HashSet()
    val availableDevices: Set<Device>
        get() = _availableDevices

    var selectedDevice: Device? = null
        private set
    private var userSelectedDevice: Device? = null

    init {
        runInAudioThread { setup() }
    }

    private fun setup() {
        if (audioManager == null) {
            return
        }
        audioDeviceDetector?.stop()
        audioDeviceDetector = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            API23AudioDeviceDetector(audioManager, this)
        } else {
            API21AudioDeviceDetector(context, audioManager, this)
        }
        audioDeviceDetector?.start()
        audioDeviceRouter = DefaultAudioDeviceRouter(audioManager, this)
    }

    fun runInAudioThread(runnable: Runnable) {
        executor.execute(runnable)
    }

    
    fun setAudioDevice(device: Device) {
        runInAudioThread(Runnable {
            if (!_availableDevices.contains(device)) {
                Timber.w("Audio device not available: $device")
                userSelectedDevice = null
                return@Runnable
            }
            if (mode != Mode.DEFAULT) {
                Timber.i("User selected device set to: $device")
                userSelectedDevice = device
                updateAudioRoute(mode, false)
            }
        })
    }

    
    fun setMode(mode: Mode) {
        runInAudioThread {
            var success: Boolean
            try {
                success = updateAudioRoute(mode, false)
            } catch (e: Throwable) {
                success = false
                Timber.e(e, "Failed to update audio route for mode: $mode")
            }
            if (success) {
                this@CallAudioManager.mode = mode
            }
        }
    }

    
    private fun updateAudioRoute(mode: Mode, force: Boolean): Boolean {
        Timber.i("Update audio route for mode: $mode")
        if (!audioDeviceRouter?.setMode(mode).orFalse()) {
            return false
        }
        if (mode == Mode.DEFAULT) {
            selectedDevice = null
            userSelectedDevice = null
            return true
        }
        val availableBluetoothDevice = _availableDevices.firstOrNull { it is Device.WirelessHeadset }
        val headsetAvailable = _availableDevices.contains(Device.Headset)

        
        var audioDevice: Device
        audioDevice = if (availableBluetoothDevice != null) {
            availableBluetoothDevice
        } else if (headsetAvailable) {
            Device.Headset
        } else if (mode == Mode.VIDEO_CALL) {
            Device.Speaker
        } else {
            Device.Phone
        }
        
        if (userSelectedDevice != null && _availableDevices.contains(userSelectedDevice)) {
            audioDevice = userSelectedDevice!!
        }

        
        
        if (!force && selectedDevice != null && selectedDevice == audioDevice) {
            return true
        }
        selectedDevice = audioDevice
        Timber.i("Selected audio device: $audioDevice")
        audioDeviceRouter?.setAudioRoute(audioDevice)
        configChange?.invoke()
        return true
    }

    
    fun resetSelectedDevice() {
        selectedDevice = null
        userSelectedDevice = null
    }

    
    fun addDevice(device: Device) {
        _availableDevices.add(device)
        resetSelectedDevice()
    }

    
    fun removeDevice(device: Device) {
        _availableDevices.remove(device)
        resetSelectedDevice()
    }

    
    fun replaceDevices(devices: Set<Device>) {
        _availableDevices.clear()
        _availableDevices.addAll(devices)
        resetSelectedDevice()
    }

    
    fun updateAudioRoute() {
        if (mode != Mode.DEFAULT) {
            updateAudioRoute(mode, false)
        }
    }

    
    fun resetAudioRoute() {
        if (mode != Mode.DEFAULT) {
            updateAudioRoute(mode, true)
        }
    }

    
    interface AudioDeviceDetector {
        
        fun start()

        
        fun stop()
    }

    interface AudioDeviceRouter {
        
        fun setAudioRoute(device: Device)

        
        fun setMode(mode: Mode): Boolean
    }

    companion object {
        
        private val executor = Executors.newSingleThreadExecutor()
    }
}
