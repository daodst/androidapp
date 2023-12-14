

package im.vector.app.test.fakes

import android.net.Uri
import im.vector.app.features.onboarding.UriFilenameResolver
import io.mockk.every
import io.mockk.mockk

class FakeUriFilenameResolver {

    val instance = mockk<UriFilenameResolver>()

    fun givenFilename(uri: Uri, name: String?) {
        every { instance.getFilenameFromUri(uri) } returns name
    }
}
