

package im.vector.app.features.location.domain.usecase

import com.airbnb.mvrx.test.MvRxTestRule
import im.vector.app.features.location.LocationData
import im.vector.app.test.fakes.FakeSession
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.OverrideMockKs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CompareLocationsUseCaseTest {

    @get:Rule
    val mvRxTestRule = MvRxTestRule()

    private val session = FakeSession()

    @OverrideMockKs
    lateinit var compareLocationsUseCase: CompareLocationsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `given 2 very near locations when calling execute then these locations are considered as equal`() = runTest {
        
        val location1 = LocationData(
                latitude = 48.858269,
                longitude = 2.294551,
                uncertainty = null
        )
        val location2 = LocationData(
                latitude = 48.858275,
                longitude = 2.294547,
                uncertainty = null
        )
        
        val areEqual = compareLocationsUseCase.execute(location1, location2)

        
        assert(areEqual)
    }

    @Test
    fun `given 2 far away locations when calling execute then these locations are considered as not equal`() = runTest {
        
        val location1 = LocationData(
                latitude = 48.858269,
                longitude = 2.294551,
                uncertainty = null
        )
        val location2 = LocationData(
                latitude = 48.861777,
                longitude = 2.289348,
                uncertainty = null
        )
        
        val areEqual = compareLocationsUseCase.execute(location1, location2)

        
        assert(areEqual.not())
    }
}
