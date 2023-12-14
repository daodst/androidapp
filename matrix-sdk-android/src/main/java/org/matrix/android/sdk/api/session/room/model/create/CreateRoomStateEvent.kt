

package org.matrix.android.sdk.api.session.room.model.create

import org.matrix.android.sdk.api.session.events.model.Content

data class CreateRoomStateEvent(
        
        val type: String,

        
        val content: Content,

        
        val stateKey: String = ""
)
