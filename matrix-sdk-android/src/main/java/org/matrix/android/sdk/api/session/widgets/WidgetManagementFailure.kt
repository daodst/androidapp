

package org.matrix.android.sdk.api.session.widgets

import org.matrix.android.sdk.api.failure.Failure

sealed class WidgetManagementFailure : Failure.FeatureFailure() {
    object NotEnoughPower : WidgetManagementFailure()
    object CreationFailed : WidgetManagementFailure()
    data class TermsNotSignedException(val baseUrl: String, val token: String) : WidgetManagementFailure()
}
