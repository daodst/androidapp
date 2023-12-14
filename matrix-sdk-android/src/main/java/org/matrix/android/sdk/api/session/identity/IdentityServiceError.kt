

package org.matrix.android.sdk.api.session.identity

import org.matrix.android.sdk.api.failure.Failure

sealed class IdentityServiceError : Failure.FeatureFailure() {
    object OutdatedIdentityServer : IdentityServiceError()
    object OutdatedHomeServer : IdentityServiceError()
    object NoIdentityServerConfigured : IdentityServiceError()
    object TermsNotSignedException : IdentityServiceError()
    object BulkLookupSha256NotSupported : IdentityServiceError()
    object UserConsentNotProvided : IdentityServiceError()
    object BindingError : IdentityServiceError()
    object NoCurrentBindingError : IdentityServiceError()
}
