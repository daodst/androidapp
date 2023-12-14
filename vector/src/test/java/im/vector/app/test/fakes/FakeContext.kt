

package im.vector.app.test.fakes

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import io.mockk.every
import io.mockk.mockk
import java.io.OutputStream

class FakeContext(
        private val contentResolver: ContentResolver = mockk()
) {

    val instance = mockk<Context>()

    init {
        every { instance.contentResolver } returns contentResolver
    }

    fun givenFileDescriptor(uri: Uri, mode: String, factory: () -> ParcelFileDescriptor?) {
        val fileDescriptor = factory()
        every { contentResolver.openFileDescriptor(uri, mode, null) } returns fileDescriptor
    }

    fun givenSafeOutputStreamFor(uri: Uri): OutputStream {
        val outputStream = mockk<OutputStream>(relaxed = true)
        every { contentResolver.openOutputStream(uri, "wt") } returns outputStream
        return outputStream
    }

    fun givenMissingSafeOutputStreamFor(uri: Uri) {
        every { contentResolver.openOutputStream(uri, "wt") } returns null
    }
}
