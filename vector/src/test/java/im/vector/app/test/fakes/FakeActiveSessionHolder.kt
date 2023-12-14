

package im.vector.app.test.fakes

import im.vector.app.core.di.ActiveSessionHolder
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.matrix.android.sdk.api.session.Session

class FakeActiveSessionHolder(
        private val fakeSession: FakeSession = FakeSession()
) {
    val instance = mockk<ActiveSessionHolder> {
        every { getActiveSession() } returns fakeSession
    }

    fun expectSetsActiveSession(session: Session) {
        justRun { instance.setActiveSession(session) }
    }
}
