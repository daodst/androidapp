

package im.vector.app.features.home.room.detail.timeline.action

import androidx.recyclerview.widget.DefaultItemAnimator

private const val ANIM_DURATION_IN_MILLIS = 300L


class MessageActionsAnimator : DefaultItemAnimator() {

    init {
        addDuration = ANIM_DURATION_IN_MILLIS
        removeDuration = 0
        moveDuration = 0
        changeDuration = 0
    }
}
