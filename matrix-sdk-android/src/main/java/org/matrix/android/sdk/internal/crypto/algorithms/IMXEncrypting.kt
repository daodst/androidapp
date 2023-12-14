

package org.matrix.android.sdk.internal.crypto.algorithms

import org.matrix.android.sdk.api.session.events.model.Content


internal interface IMXEncrypting {

    
    suspend fun encryptEventContent(eventContent: Content, eventType: String, userIds: List<String>): Content
}
