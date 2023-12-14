

package im.vector.app.features.home

import im.vector.app.R
import im.vector.app.features.home.room.detail.timeline.helper.MatrixItemColorProvider.Companion.getColorFromUserId
import org.junit.Assert.assertEquals
import org.junit.Test

class UserColorTest {

    @Test
    fun testNull() {
        assertEquals(R.color.element_name_01, getColorFromUserId(null))
    }

    @Test
    fun testEmpty() {
        assertEquals(R.color.element_name_01, getColorFromUserId(""))
    }

    @Test
    fun testName() {
        assertEquals(R.color.element_name_01, getColorFromUserId("@ganfra:matrix.org"))
        assertEquals(R.color.element_name_04, getColorFromUserId("@benoit0816:matrix.org"))
        assertEquals(R.color.element_name_05, getColorFromUserId("@hubert:uhoreg.ca"))
        assertEquals(R.color.element_name_07, getColorFromUserId("@nadonomy:matrix.org"))
    }
}
