

package im.vector.app.test.fakes

import im.vector.app.features.onboarding.RegisterAction
import im.vector.app.features.onboarding.RegistrationActionHandler
import io.mockk.coEvery
import io.mockk.mockk
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.RegistrationWizard

class FakeRegisterActionHandler {

    val instance = mockk<RegistrationActionHandler>()

    fun givenResultsFor(wizard: RegistrationWizard, result: List<Pair<RegisterAction, RegistrationResult>>) {
        coEvery { instance.handleRegisterAction(wizard, any()) } answers { call ->
            val actionArg = call.invocation.args[1] as RegisterAction
            result.first { it.first == actionArg }.second
        }
    }

    fun givenThrowsFor(wizard: RegistrationWizard, action: RegisterAction, cause: Throwable) {
        coEvery { instance.handleRegisterAction(wizard, action) } throws cause
    }
}
