

package im.vector.app.features.pin

import android.content.SharedPreferences
import androidx.core.content.edit
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.security.PFSecurityManager
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.extensions.orFalse
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface PinCodeStore {

    suspend fun storeEncodedPin(encodePin: String)

    suspend fun deleteEncodedPin()

    fun getEncodedPin(): String?

    suspend fun hasEncodedPin(): Boolean

    fun getRemainingPinCodeAttemptsNumber(): Int

    fun getRemainingBiometricsAttemptsNumber(): Int

    
    fun onWrongPin(): Int

    
    fun onWrongBiometrics(): Int

    
    fun resetCounters()

    fun addListener(listener: PinCodeStoreListener)
    fun removeListener(listener: PinCodeStoreListener)
}

interface PinCodeStoreListener {
    fun onPinSetUpChange(isConfigured: Boolean)
}

@Singleton
class SharedPrefPinCodeStore @Inject constructor(private val sharedPreferences: SharedPreferences) : PinCodeStore {
    private val listeners = mutableSetOf<PinCodeStoreListener>()

    override suspend fun storeEncodedPin(encodePin: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                putString(ENCODED_PIN_CODE_KEY, encodePin)
            }
        }
        listeners.forEach { it.onPinSetUpChange(isConfigured = true) }
    }

    override suspend fun deleteEncodedPin() {
        withContext(Dispatchers.IO) {
            
            resetCounters()
            sharedPreferences.edit {
                remove(ENCODED_PIN_CODE_KEY)
            }
            awaitPinCodeCallback<Boolean> {
                PFSecurityManager.getInstance().pinCodeHelper.delete(it)
            }
        }
        listeners.forEach { it.onPinSetUpChange(isConfigured = false) }
    }

    override fun getEncodedPin(): String? {
        return sharedPreferences.getString(ENCODED_PIN_CODE_KEY, null)
    }

    override suspend fun hasEncodedPin(): Boolean = withContext(Dispatchers.IO) {
        val hasEncodedPin = getEncodedPin()?.isNotBlank().orFalse()
        if (!hasEncodedPin) {
            return@withContext false
        }
        val result = awaitPinCodeCallback<Boolean> {
            PFSecurityManager.getInstance().pinCodeHelper.isPinCodeEncryptionKeyExist(it)
        }
        result.error == null && result.result
    }

    override fun getRemainingPinCodeAttemptsNumber(): Int {
        return sharedPreferences.getInt(REMAINING_PIN_CODE_ATTEMPTS_KEY, MAX_PIN_CODE_ATTEMPTS_NUMBER_BEFORE_LOGOUT)
    }

    override fun getRemainingBiometricsAttemptsNumber(): Int {
        return sharedPreferences.getInt(REMAINING_BIOMETRICS_ATTEMPTS_KEY, MAX_BIOMETRIC_ATTEMPTS_NUMBER_BEFORE_FORCE_PIN)
    }

    override fun onWrongPin(): Int {
        val remaining = getRemainingPinCodeAttemptsNumber() - 1
        sharedPreferences.edit {
            putInt(REMAINING_PIN_CODE_ATTEMPTS_KEY, remaining)
        }
        return remaining
    }

    override fun onWrongBiometrics(): Int {
        val remaining = getRemainingBiometricsAttemptsNumber() - 1
        sharedPreferences.edit {
            putInt(REMAINING_BIOMETRICS_ATTEMPTS_KEY, remaining)
        }
        return remaining
    }

    override fun resetCounters() {
        sharedPreferences.edit {
            remove(REMAINING_PIN_CODE_ATTEMPTS_KEY)
            remove(REMAINING_BIOMETRICS_ATTEMPTS_KEY)
        }
    }

    override fun addListener(listener: PinCodeStoreListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: PinCodeStoreListener) {
        listeners.remove(listener)
    }

    private suspend inline fun <T> awaitPinCodeCallback(crossinline callback: (PFPinCodeHelperCallback<T>) -> Unit) = suspendCoroutine<PFResult<T>> { cont ->
        callback(PFPinCodeHelperCallback<T> { result -> cont.resume(result) })
    }

    companion object {
        private const val ENCODED_PIN_CODE_KEY = "ENCODED_PIN_CODE_KEY"
        private const val REMAINING_PIN_CODE_ATTEMPTS_KEY = "REMAINING_PIN_CODE_ATTEMPTS_KEY"
        private const val REMAINING_BIOMETRICS_ATTEMPTS_KEY = "REMAINING_BIOMETRICS_ATTEMPTS_KEY"

        private const val MAX_PIN_CODE_ATTEMPTS_NUMBER_BEFORE_LOGOUT = 3
        private const val MAX_BIOMETRIC_ATTEMPTS_NUMBER_BEFORE_FORCE_PIN = 5
    }
}
