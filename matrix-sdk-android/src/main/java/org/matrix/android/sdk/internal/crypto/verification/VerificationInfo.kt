
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject
import org.matrix.android.sdk.api.session.events.model.Content

internal interface VerificationInfo<ValidObjectType> {
    fun toEventContent(): Content? = null
    fun toSendToDeviceObject(): SendToDeviceObject? = null

    fun asValidObject(): ValidObjectType?

    
    val transactionId: String?
}
