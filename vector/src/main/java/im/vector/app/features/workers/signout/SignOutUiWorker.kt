

package im.vector.app.features.workers.signout

import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.extensions.cannotLogoutSafely
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs

class SignOutUiWorker(private val activity: FragmentActivity) {

    fun perform() {
        val session =
            activity.singletonEntryPoint().activeSessionHolder().getSafeActiveSession() ?: return
        if (session.cannotLogoutSafely()) {
            
            val signOutDialog = SignOutBottomSheetDialogFragment.newInstance()
            signOutDialog.onSignOut = Runnable {
                doSignOut()
            }
            signOutDialog.show(activity.supportFragmentManager, "SO")
        } else {
            
            MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.action_sign_out)
                .setMessage(R.string.action_sign_out_confirmation_simple)
                .setPositiveButton(R.string.action_sign_out) { _, _ ->
                    doSignOut()
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
        }
    }

    @Deprecated("useless")
    fun goLogin() {
        MainActivity.restartApp(activity, MainActivityArgs(isGoLogin = true))
    }
    
    fun forceSignOut() {
        doSignOut()
    }

    private fun doSignOut() {
        MainActivity.restartApp(activity, MainActivityArgs(clearCredentials = true))
    }
}
