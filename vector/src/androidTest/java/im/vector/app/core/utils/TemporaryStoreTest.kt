

package im.vector.app.core.utils

import org.amshove.kluent.shouldBe
import org.junit.Test
import java.lang.Thread.sleep

class TemporaryStoreTest {

    @Test
    fun testTemporaryStore() {
        
        val store = TemporaryStore<String>(30)

        store.data = "test"
        store.data shouldBe "test"
        sleep(15)
        store.data shouldBe "test"
        sleep(20)
        store.data shouldBe null
    }
}
