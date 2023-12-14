

package im.vector.app.test.fakes

import im.vector.app.features.notifications.SummaryGroupMessageCreator
import io.mockk.mockk

class FakeSummaryGroupMessageCreator {

    val instance = mockk<SummaryGroupMessageCreator>()
}
