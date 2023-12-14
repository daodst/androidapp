

package im.vector.app.test.fakes

import im.vector.app.features.notifications.NotifiableMessageEvent
import im.vector.app.features.notifications.RoomGroupMessageCreator
import im.vector.app.features.notifications.RoomNotification
import io.mockk.every
import io.mockk.mockk

class FakeRoomGroupMessageCreator {

    val instance = mockk<RoomGroupMessageCreator>()

    fun givenCreatesRoomMessageFor(events: List<NotifiableMessageEvent>,
                                   roomId: String,
                                   userDisplayName: String,
                                   userAvatarUrl: String?): RoomNotification.Message {
        val mockMessage = mockk<RoomNotification.Message>()
        every { instance.createRoomMessage(events, roomId, userDisplayName, userAvatarUrl) } returns mockMessage
        return mockMessage
    }
}
