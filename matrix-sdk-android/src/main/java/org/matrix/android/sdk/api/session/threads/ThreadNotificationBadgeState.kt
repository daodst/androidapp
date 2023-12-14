

package org.matrix.android.sdk.api.session.threads


data class ThreadNotificationBadgeState(
        val numberOfLocalUnreadThreads: Int = 0,
        val isUserMentioned: Boolean = false
)
