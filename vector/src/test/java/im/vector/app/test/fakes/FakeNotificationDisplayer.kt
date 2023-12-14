

package im.vector.app.test.fakes

import im.vector.app.features.notifications.NotificationDisplayer
import im.vector.app.features.notifications.NotificationDrawerManager
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder

class FakeNotificationDisplayer {

    val instance = mockk<NotificationDisplayer>(relaxed = true)

    fun verifySummaryCancelled() {
        verify { instance.cancelNotificationMessage(tag = null, NotificationDrawerManager.SUMMARY_NOTIFICATION_ID) }
    }

    fun verifyNoOtherInteractions() {
        confirmVerified(instance)
    }

    fun verifyInOrder(verifyBlock: NotificationDisplayer.() -> Unit) {
        verifyOrder { verifyBlock(instance) }
        verifyNoOtherInteractions()
    }
}
