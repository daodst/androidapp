

package org.matrix.android.sdk.internal.crypto.keysbackup.util

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.MatrixTest

@FixMethodOrder(MethodSorters.JVM)
class Base58Test : MatrixTest {

    @Test
    fun encode() {
        
        assertEquals("StV1DL6CwTryKyV", base58encode("hello world".toByteArray()))
    }

    @Test
    fun decode() {
        
        assertArrayEquals("hello world".toByteArray(), base58decode("StV1DL6CwTryKyV"))
    }

    @Test
    fun encode_curve25519() {
        
        assertEquals("4F85ZySpwyY6FuH7mQYyyr5b8nV9zFRBLj92AJa37sMr",
                base58encode(("0123456789" + "0123456789" + "0123456789" + "01").toByteArray()))
    }

    @Test
    fun decode_curve25519() {
        assertArrayEquals(("0123456789" + "0123456789" + "0123456789" + "01").toByteArray(),
                base58decode("4F85ZySpwyY6FuH7mQYyyr5b8nV9zFRBLj92AJa37sMr"))
    }
}
