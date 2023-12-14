

package org.matrix.android.sdk.api.auth.registration

import org.matrix.android.sdk.api.failure.Failure

sealed class RegistrationAvailability {
    object Available : RegistrationAvailability()
    data class NotAvailable(val failure: Failure.ServerError) : RegistrationAvailability()
}
