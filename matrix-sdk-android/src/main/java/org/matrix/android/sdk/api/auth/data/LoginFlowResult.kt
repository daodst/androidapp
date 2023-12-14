

package org.matrix.android.sdk.api.auth.data

data class LoginFlowResult(
        val supportedLoginTypes: List<String>,
        val ssoIdentityProviders: List<SsoIdentityProvider>?,
        val isLoginAndRegistrationSupported: Boolean,
        val homeServerUrl: String,
        val isOutdatedHomeserver: Boolean
)
