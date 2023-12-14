

package org.matrix.android.sdk.internal.crypto.verification.qrcode

import org.amshove.kluent.shouldBeEqualTo
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.MatrixTest

@FixMethodOrder(MethodSorters.JVM)
class BinaryStringTest : MatrixTest {

    
    @Test
    fun testNominalCase() {
        val byteArray = ByteArray(256)
        for (i in byteArray.indices) {
            byteArray[i] = i.toByte() 
        }

        val str = byteArray.toString(Charsets.ISO_8859_1)

        str.length shouldBeEqualTo 256

        

        val result = str.toByteArray(Charsets.ISO_8859_1)

        result.size shouldBeEqualTo 256

        for (i in 0..255) {
            result[i] shouldBeEqualTo i.toByte()
            result[i] shouldBeEqualTo byteArray[i]
        }
    }
}
