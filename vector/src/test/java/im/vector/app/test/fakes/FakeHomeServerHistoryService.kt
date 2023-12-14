

package im.vector.app.test.fakes

import io.mockk.justRun
import io.mockk.mockk
import org.matrix.android.sdk.api.auth.HomeServerHistoryService

class FakeHomeServerHistoryService : HomeServerHistoryService by mockk() {
    override fun getKnownServersUrls() = emptyList<String>()
    fun expectUrlToBeAdded(url: String) {
        justRun { addHomeServerToHistory(url) }
    }
}
