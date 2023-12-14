

package im.vector.app.test.fakes

import im.vector.app.features.onboarding.DirectLoginUseCase
import im.vector.app.features.onboarding.OnboardingAction
import io.mockk.coEvery
import io.mockk.mockk
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

class FakeDirectLoginUseCase {
    val instance = mockk<DirectLoginUseCase>()

    fun givenSuccessResult(action: OnboardingAction.LoginOrRegister, config: HomeServerConnectionConfig?, result: FakeSession) {
        coEvery { instance.execute(action, config) } returns Result.success(result)
    }

    fun givenFailureResult(action: OnboardingAction.LoginOrRegister, config: HomeServerConnectionConfig?, cause: Throwable) {
        coEvery { instance.execute(action, config) } returns Result.failure(cause)
    }
}
