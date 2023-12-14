

package im.vector.app.test.fakes

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import io.mockk.every
import io.mockk.mockk

class FakeContentResolver {

    val instance = mockk<ContentResolver>()

    fun givenUriResult(uri: Uri, cursor: Cursor?) {
        every { instance.query(uri, null, null, null, null) } returns cursor
    }
}
