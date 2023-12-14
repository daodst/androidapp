

package im.vector.app.test.fakes

import im.vector.app.features.DefaultVectorFeatures
import im.vector.app.features.VectorFeatures
import io.mockk.every
import io.mockk.spyk

class FakeVectorFeatures : VectorFeatures by spyk<DefaultVectorFeatures>() {

    fun givenPersonalisationEnabled() {
        every { isOnboardingPersonalizeEnabled() } returns true
    }
}
