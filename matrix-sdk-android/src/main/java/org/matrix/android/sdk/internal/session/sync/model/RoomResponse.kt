

package org.matrix.android.sdk.internal.session.sync.model

import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event


@JsonClass(generateAdapter = true)
internal data class RoomResponse(
        
        val roomId: String? = null,

        
        val messages: TokensChunkResponse<Event>? = null,

        
        val state: List<Event>? = null,

        
        val accountData: List<Event>? = null,

        
        val membership: String? = null,

        
        val visibility: String? = null,

        
        val inviter: String? = null,

        
        val invite: Event? = null,

        
        
        val presence: List<Event>? = null,

        
        val receipts: List<Event>? = null
)
