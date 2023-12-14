

package im.vector.app.features.analytics.extensions

import im.vector.app.features.analytics.plan.Composer
import im.vector.app.features.home.room.detail.composer.MessageComposerViewState
import im.vector.app.features.home.room.detail.composer.SendMode

fun MessageComposerViewState.toAnalyticsComposer(): Composer =
        Composer(
                inThread = isInThreadTimeline(),
                isEditing = sendMode is SendMode.Edit,
                isReply = sendMode is SendMode.Reply,
                startsThread = startsThread
        )
