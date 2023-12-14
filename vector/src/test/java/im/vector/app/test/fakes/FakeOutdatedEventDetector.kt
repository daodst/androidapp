

package im.vector.app.test.fakes

import im.vector.app.features.notifications.NotifiableEvent
import im.vector.app.features.notifications.OutdatedEventDetector
import io.mockk.every
import io.mockk.mockk

class FakeOutdatedEventDetector {
    val instance = mockk<OutdatedEventDetector>()

    fun givenEventIsOutOfDate(notifiableEvent: NotifiableEvent) {
        every { instance.isMessageOutdated(notifiableEvent) } returns true
    }

    fun givenEventIsInDate(notifiableEvent: NotifiableEvent) {
        every { instance.isMessageOutdated(notifiableEvent) } returns false
    }
}
