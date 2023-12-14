

package org.matrix.android.sdk.api.session.room.threads.model

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.sender.SenderInfo


data class ThreadSummary(val roomId: String,
                         val rootEvent: Event?,
                         val latestEvent: Event?,
                         val rootEventId: String,
                         val rootThreadSenderInfo: SenderInfo,
                         val latestThreadSenderInfo: SenderInfo,
                         val isUserParticipating: Boolean,
                         val numberOfThreads: Int,
                         val threadEditions: ThreadEditions = ThreadEditions())
