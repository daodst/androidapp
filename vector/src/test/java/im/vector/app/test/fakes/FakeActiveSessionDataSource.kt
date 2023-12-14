

package im.vector.app.test.fakes

import arrow.core.Option
import im.vector.app.ActiveSessionDataSource
import org.matrix.android.sdk.api.session.Session

class FakeActiveSessionDataSource {

    val instance = ActiveSessionDataSource()

    fun setActiveSession(session: Session) {
        instance.post(Option.just(session))
    }
}
