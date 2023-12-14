

package org.matrix.android.sdk.api.session.crypto.model

import org.matrix.android.sdk.api.session.events.model.Content

data class MXEncryptEventContentResult(
        
        val eventContent: Content,
        
        val eventType: String
)
