

package im.vector.app.test.fakes

import im.vector.app.features.onboarding.StartAuthenticationFlowUseCase
import im.vector.app.features.onboarding.StartAuthenticationFlowUseCase.StartAuthenticationResult
import io.mockk.coEvery
import io.mockk.mockk
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

class FakeStartAuthenticationFlowUseCase {

    val instance = mockk<StartAuthenticationFlowUseCase>()

    fun givenResult(config: HomeServerConnectionConfig, result: StartAuthenticationResult) {
        coEvery { instance.execute(config) } returns result
    }
}
