

package im.vector.app.test.fakes

import im.vector.app.core.resources.StringProvider
import io.mockk.every
import io.mockk.mockk

class FakeStringProvider {
    val instance = mockk<StringProvider>()

    init {
        every { instance.getString(any()) } answers {
            "test-${args[0]}"
        }
    }

    fun given(id: Int, result: String) {
        every { instance.getString(id) } returns result
    }
}

fun Int.toTestString() = "test-$this"
