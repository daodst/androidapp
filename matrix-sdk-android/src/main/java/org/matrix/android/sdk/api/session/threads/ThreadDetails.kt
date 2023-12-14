

package org.matrix.android.sdk.api.session.threads

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.sender.SenderInfo


data class ThreadDetails(
        val isRootThread: Boolean = false,
        val numberOfThreads: Int = 0,
        val threadSummarySenderInfo: SenderInfo? = null,
        val threadSummaryLatestEvent: Event? = null,
        val lastMessageTimestamp: Long? = null,
        val threadNotificationState: ThreadNotificationState = ThreadNotificationState.NO_NEW_MESSAGE,
        val isThread: Boolean = false,
        val lastRootThreadEdition: String? = null
)
