

package im.vector.app.test.fakes

import android.net.Uri
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.matrix.android.sdk.api.session.profile.ProfileService

class FakeProfileService : ProfileService by mockk(relaxed = true) {

    fun givenSetDisplayNameErrors(errorCause: RuntimeException) {
        coEvery { setDisplayName(any(), any()) } throws errorCause
    }

    fun givenUpdateAvatarErrors(errorCause: RuntimeException) {
        coEvery { updateAvatar(any(), any(), any()) } throws errorCause
    }

    fun verifyUpdatedName(userId: String, newName: String) {
        coVerify { setDisplayName(userId, newName) }
    }

    fun verifyAvatarUpdated(userId: String, newAvatarUri: Uri, fileName: String) {
        coVerify { updateAvatar(userId, newAvatarUri, fileName) }
    }
}
