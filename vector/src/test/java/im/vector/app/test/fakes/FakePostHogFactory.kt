

package im.vector.app.test.fakes

import com.posthog.android.PostHog
import im.vector.app.features.analytics.impl.PostHogFactory
import io.mockk.every
import io.mockk.mockk

class FakePostHogFactory(postHog: PostHog) {
    val instance = mockk<PostHogFactory>().also {
        every { it.createPosthog() } returns postHog
    }
}
