

package im.vector.app.features.onboarding.ftueauth

import android.content.ComponentName
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.withState
import im.vector.app.core.utils.openUrlInChromeCustomTab
import im.vector.app.features.login.SSORedirectRouterActivity
import im.vector.app.features.login.hasSso
import im.vector.app.features.login.ssoIdentityProviders

abstract class AbstractSSOFtueAuthFragment<VB : ViewBinding> : AbstractFtueAuthFragment<VB>() {

    
    private var customTabsServiceConnection: CustomTabsServiceConnection? = null
    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null

    override fun onStart() {
        super.onStart()
        val hasSSO = withState(viewModel) { it.selectedHomeserver.preferredLoginMode.hasSso() }
        if (hasSSO) {
            val packageName = CustomTabsClient.getPackageName(requireContext(), null)

            
            if (packageName != null) {
                customTabsServiceConnection = object : CustomTabsServiceConnection() {
                    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                        customTabsClient = client
                                .also { it.warmup(0L) }
                        prefetchIfNeeded()
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                    }
                }
                        .also {
                            CustomTabsClient.bindCustomTabsService(
                                    requireContext(),
                                    
                                    packageName,
                                    it
                            )
                        }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val hasSSO = withState(viewModel) { it.selectedHomeserver.preferredLoginMode.hasSso() }
        if (hasSSO) {
            customTabsServiceConnection?.let { requireContext().unbindService(it) }
            customTabsServiceConnection = null
        }
    }

    private fun prefetchUrl(url: String) {
        if (customTabsSession == null) {
            customTabsSession = customTabsClient?.newSession(null)
        }

        customTabsSession?.mayLaunchUrl(Uri.parse(url), null, null)
    }

    fun openInCustomTab(ssoUrl: String) {
        openUrlInChromeCustomTab(requireContext(), customTabsSession, ssoUrl)
    }

    private fun prefetchIfNeeded() {
        withState(viewModel) { state ->
            if (state.selectedHomeserver.preferredLoginMode.hasSso() && state.selectedHomeserver.preferredLoginMode.ssoIdentityProviders().isNullOrEmpty()) {
                
                viewModel.getSsoUrl(
                        redirectUrl = SSORedirectRouterActivity.VECTOR_REDIRECT_URL,
                        deviceId = state.deviceId,
                        providerId = null
                )
                        ?.let { prefetchUrl(it) }
            }
        }
    }
}
