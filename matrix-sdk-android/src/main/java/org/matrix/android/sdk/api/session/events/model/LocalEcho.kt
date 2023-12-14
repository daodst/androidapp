

package org.matrix.android.sdk.api.session.events.model

import java.util.UUID

object LocalEcho {

    private const val PREFIX = "\$local."

    fun isLocalEchoId(eventId: String) = eventId.startsWith(PREFIX)

    fun createLocalEchoId() = "${PREFIX}${UUID.randomUUID()}"
}
