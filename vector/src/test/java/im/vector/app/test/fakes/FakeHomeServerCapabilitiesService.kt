

package im.vector.app.test.fakes

import io.mockk.every
import io.mockk.mockk
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilities
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilitiesService

class FakeHomeServerCapabilitiesService : HomeServerCapabilitiesService by mockk() {

    fun givenCapabilities(homeServerCapabilities: HomeServerCapabilities) {
        every { getHomeServerCapabilities() } returns homeServerCapabilities
    }
}
