

package im.vector.app.test.fakes

import android.net.Uri
import androidx.core.net.toUri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.io.File

class FakeFile {

    val instance = mockk<File>()

    init {
        mockkStatic(Uri::class)
    }

    
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    fun givenName(name: String) {
        every { instance.name } returns name
    }

    fun givenUri(uri: Uri) {
        every { instance.toUri() } returns uri
    }
}
