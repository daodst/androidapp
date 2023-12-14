

package im.vector.app.test.fakes

import im.vector.app.features.login.HomeServerConnectionConfigFactory
import io.mockk.every
import io.mockk.mockk
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

class FakeHomeServerConnectionConfigFactory {
    val instance: HomeServerConnectionConfigFactory = mockk()

    fun givenConfigFor(url: String, config: HomeServerConnectionConfig) {
        every { instance.create(url) } returns config
    }
}
