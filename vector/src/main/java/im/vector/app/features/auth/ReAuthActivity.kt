

package im.vector.app.features.auth

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.platform.SimpleFragmentActivity
import im.vector.app.core.utils.openUrlInChromeCustomTab
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.auth.AuthenticationService
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.auth.registration.nextUncompletedStage
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReAuthActivity : SimpleFragmentActivity() {

    @Parcelize
    data class Args(
            val flowType: String?,
            val title: String?,
            val session: String?,
            val lastErrorCode: String?,
            val resultKeyStoreAlias: String
    ) : Parcelable

    
    private var customTabsServiceConnection: CustomTabsServiceConnection? = null
    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null

    @Inject lateinit var authenticationService: AuthenticationService

    private val sharedViewModel: ReAuthViewModel by viewModel()

    

    override fun initUiAndData() {
        super.initUiAndData()

        val title = intent.extras?.getString(EXTRA_REASON_TITLE) ?: getString(R.string.re_authentication_activity_title)
        supportActionBar?.setTitle(title) ?: run { setTitle(title) }


        
        
        
        
        if (isFirstCreation()) {
            addFragment(
                    views.container,
                    PromptFragment::class.java
            )
        }

        sharedViewModel.observeViewEvents {
            when (it) {
                is ReAuthEvents.OpenSsoURl            -> {
                    openInCustomTab(it.url)
                }
                ReAuthEvents.Dismiss                  -> {
                    setResult(RESULT_CANCELED)
                    finish()
                }
                is ReAuthEvents.PasswordFinishSuccess -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(RESULT_FLOW_TYPE, LoginFlowTypes.PASSWORD)
                        putExtra(RESULT_VALUE, it.passwordSafeForIntent)
                    })
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        
        withState(sharedViewModel) {
            if (it.ssoFallbackPageWasShown) {
                Timber.d("## UIA ssoFallbackPageWasShown tentative success")
                setResult(RESULT_OK, Intent().apply {
                    putExtra(RESULT_FLOW_TYPE, LoginFlowTypes.SSO)
                })
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        withState(sharedViewModel) { state ->
            if (state.ssoFallbackPageWasShown) {
                sharedViewModel.handle(ReAuthActions.FallBackPageClosed)
                return@withState
            }
        }

        val packageName = CustomTabsClient.getPackageName(this, null)

        
        if (packageName != null) {
            customTabsServiceConnection = object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                    Timber.d("## CustomTab onCustomTabsServiceConnected($name)")
                    customTabsClient = client
                            .also { it.warmup(0L) }
                    customTabsSession = customTabsClient?.newSession(object : CustomTabsCallback() {


                        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                            Timber.v("## CustomTab onNavigationEvent($navigationEvent), $extras")
                            super.onNavigationEvent(navigationEvent, extras)
                            if (navigationEvent == NAVIGATION_FINISHED) {
                            }
                        }

                        override fun onRelationshipValidationResult(relation: Int, requestedOrigin: Uri, result: Boolean, extras: Bundle?) {
                            Timber.v("## CustomTab onRelationshipValidationResult($relation), $requestedOrigin")
                            super.onRelationshipValidationResult(relation, requestedOrigin, result, extras)
                        }
                    })
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    Timber.d("## CustomTab onServiceDisconnected($name)")
                }
            }.also {
                CustomTabsClient.bindCustomTabsService(
                        this,
                        
                        packageName,
                        it
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        customTabsServiceConnection?.let { this.unbindService(it) }
        customTabsServiceConnection = null
        customTabsSession = null
    }

    private fun openInCustomTab(ssoUrl: String) {
        openUrlInChromeCustomTab(this, customTabsSession, ssoUrl)
        val channelOpened = customTabsSession?.requestPostMessageChannel(Uri.parse("https://element.io"))
        Timber.d("## CustomTab channelOpened: $channelOpened")
    }

    companion object {

        const val EXTRA_AUTH_TYPE = "EXTRA_AUTH_TYPE"
        const val EXTRA_REASON_TITLE = "EXTRA_REASON_TITLE"
        const val RESULT_FLOW_TYPE = "RESULT_FLOW_TYPE"
        const val RESULT_VALUE = "RESULT_VALUE"
        const val DEFAULT_RESULT_KEYSTORE_ALIAS = "ReAuthActivity"

        fun newIntent(context: Context,
                      fromError: RegistrationFlowResponse,
                      lastErrorCode: String?,
                      reasonTitle: String?,
                      resultKeyStoreAlias: String = DEFAULT_RESULT_KEYSTORE_ALIAS): Intent {
            val authType = when (fromError.nextUncompletedStage()) {
                LoginFlowTypes.PASSWORD -> {
                    LoginFlowTypes.PASSWORD
                }
                LoginFlowTypes.SSO      -> {
                    LoginFlowTypes.SSO
                }
                else                    -> {
                    
                    null
                }
            }
            return Intent(context, ReAuthActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, Args(authType, reasonTitle, fromError.session, lastErrorCode, resultKeyStoreAlias))
            }
        }
    }
}
