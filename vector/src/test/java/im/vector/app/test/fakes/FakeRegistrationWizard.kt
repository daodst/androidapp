

package im.vector.app.test.fakes

import io.mockk.coEvery
import io.mockk.mockk
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.RegistrationWizard
import org.matrix.android.sdk.api.session.Session

class FakeRegistrationWizard : RegistrationWizard by mockk(relaxed = false) {

    fun givenSuccessFor(result: Session, expect: suspend RegistrationWizard.() -> RegistrationResult) {
        coEvery { expect(this@FakeRegistrationWizard) } returns RegistrationResult.Success(result)
    }
}
