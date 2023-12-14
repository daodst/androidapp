

package org.matrix.android.sdk.internal.crypto.store.db

import org.junit.Assert.assertEquals
import org.junit.Test
import org.matrix.android.sdk.MatrixTest
import org.matrix.android.sdk.api.util.md5

class HelperTest : MatrixTest {

    @Test
    fun testHash_ok() {
        assertEquals("e9ee13b1ba2afc0825f4e556114785dd", "alice_15428931567802abf5ba7-d685-4333-af47-d38417ab3724:localhost:8480".md5())
    }

    @Test
    fun testHash_size_ok() {
        
        for (i in 1..100) {
            assertEquals(32, "a".repeat(i).md5().length)
        }
    }
}
