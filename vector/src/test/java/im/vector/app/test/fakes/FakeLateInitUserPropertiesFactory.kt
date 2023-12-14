

package im.vector.app.test.fakes

import im.vector.app.features.analytics.impl.LateInitUserPropertiesFactory
import im.vector.app.features.analytics.plan.UserProperties
import io.mockk.coEvery
import io.mockk.mockk

class FakeLateInitUserPropertiesFactory {

    val instance = mockk<LateInitUserPropertiesFactory>()

    fun givenCreatesProperties(userProperties: UserProperties?) {
        coEvery { instance.createUserProperties() } returns userProperties
    }
}
