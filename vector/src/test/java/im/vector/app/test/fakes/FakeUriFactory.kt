

package im.vector.app.test.fakes

import im.vector.app.features.onboarding.UriFactory
import io.mockk.every
import io.mockk.mockk

class FakeUriFactory {

    val instance = mockk<UriFactory>().also {
        every { it.parse(any()) } answers {
            val input = it.invocation.args.first() as String
            FakeUri().also { it.givenEquals(input) }.instance
        }
    }
}
