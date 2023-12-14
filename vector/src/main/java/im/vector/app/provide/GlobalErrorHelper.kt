package im.vector.app.provide

import androidx.appcompat.app.AppCompatActivity
import im.vector.app.core.extensions.observeEvent
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import org.matrix.android.sdk.api.failure.GlobalError
import timber.log.Timber

open class GlobalErrorHelper {

    fun init(context: AppCompatActivity) {

        val singletonEntryPoint = context.singletonEntryPoint()
        val sessionListener = singletonEntryPoint.sessionListener()

        sessionListener.globalErrorLiveData.observeEvent(context) {
            handleGlobalError(context, it)
        }
    }

    
    private fun handleGlobalError(context: AppCompatActivity, globalError: GlobalError) {
        when (globalError) {
            is GlobalError.InvalidToken ->
                handleInvalidToken(context, globalError)
            else                        -> {}
        }
    }

    
    private var mainActivityStarted = false
    protected open fun handleInvalidToken(context: AppCompatActivity, globalError: GlobalError.InvalidToken) {
        Timber.w("Invalid token event received")
        if (mainActivityStarted) {
            return
        }
        mainActivityStarted = true

        MainActivity.restartApp(
                context,
                MainActivityArgs(
                        clearCredentials = !globalError.softLogout,
                        isUserLoggedOut = true,
                        isSoftLogout = globalError.softLogout
                )
        )
    }
}
