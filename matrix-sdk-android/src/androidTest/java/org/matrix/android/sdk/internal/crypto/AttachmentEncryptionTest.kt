

package org.matrix.android.sdk.internal.crypto

import android.os.MemoryFile
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.api.session.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileKey
import org.matrix.android.sdk.internal.crypto.attachments.MXEncryptedAttachments
import java.io.ByteArrayOutputStream
import java.io.InputStream


@Suppress("SpellCheckingInspection")
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AttachmentEncryptionTest {

    private fun checkDecryption(input: String, encryptedFileInfo: EncryptedFileInfo): String {
        val inputAsByteArray = Base64.decode(input, Base64.DEFAULT)

        val inputStream: InputStream

        inputStream = if (inputAsByteArray.isEmpty()) {
            inputAsByteArray.inputStream()
        } else {
            val memoryFile = MemoryFile("file" + System.currentTimeMillis(), inputAsByteArray.size)
            memoryFile.outputStream.write(inputAsByteArray)
            memoryFile.inputStream
        }

        val decryptedStream = ByteArrayOutputStream()
        val result = MXEncryptedAttachments.decryptAttachment(inputStream, encryptedFileInfo.toElementToDecrypt()!!, decryptedStream)

        assert(result)

        val toByteArray = decryptedStream.toByteArray()

        return Base64.encodeToString(toByteArray, 0, toByteArray.size, Base64.DEFAULT).replace("\n".toRegex(), "").replace("=".toRegex(), "")
    }

    @Test
    fun checkDecrypt1() {
        val encryptedFileInfo = EncryptedFileInfo(
                v = "v2",
                hashes = mapOf("sha256" to "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU"),
                key = EncryptedFileKey(
                        alg = "A256CTR",
                        k = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        keyOps = listOf("encrypt", "decrypt"),
                        kty = "oct",
                        ext = true
                ),
                iv = "AAAAAAAAAAAAAAAAAAAAAA",
                url = "dummyUrl"
        )

        assertEquals("", checkDecryption("", encryptedFileInfo))
    }

    @Test
    fun checkDecrypt2() {
        val encryptedFileInfo = EncryptedFileInfo(
                v = "v2",
                hashes = mapOf("sha256" to "YzF08lARDdOCzJpzuSwsjTNlQc4pHxpdHcXiD/wpK6k"),
                key = EncryptedFileKey(
                        alg = "A256CTR",
                        k = "__________________________________________8",
                        keyOps = listOf("encrypt", "decrypt"),
                        kty = "oct",
                        ext = true
                ),
                iv = "
                url = "dummyUrl"
        )

        assertEquals("SGVsbG8sIFdvcmxk", checkDecryption("5xJZTt5cQicm+9f4", encryptedFileInfo))
    }

    @Test
    fun checkDecrypt3() {
        val encryptedFileInfo = EncryptedFileInfo(
                v = "v2",
                hashes = mapOf("sha256" to "IOq7/dHHB+mfHfxlRY5XMeCWEwTPmlf4cJcgrkf6fVU"),
                key = EncryptedFileKey(
                        alg = "A256CTR",
                        k = "__________________________________________8",
                        keyOps = listOf("encrypt", "decrypt"),
                        kty = "oct",
                        ext = true
                ),
                iv = "
                url = "dummyUrl"
        )

        assertEquals("YWxwaGFudW1lcmljYWxseWFscGhhbnVtZXJpY2FsbHlhbHBoYW51bWVyaWNhbGx5YWxwaGFudW1lcmljYWxseQ",
                checkDecryption("zhtFStAeFx0s+9L/sSQO+WQMtldqYEHqTxMduJrCIpnkyer09kxJJuA4K+adQE4w+7jZe/vR9kIcqj9rOhDR8Q",
                        encryptedFileInfo))
    }

    @Test
    fun checkDecrypt4() {
        val encryptedFileInfo = EncryptedFileInfo(
                v = "v2",
                hashes = mapOf("sha256" to "LYG/orOViuFwovJpv2YMLSsmVKwLt7pY3f8SYM7KU5E"),
                key = EncryptedFileKey(
                        alg = "A256CTR",
                        k = "__________________________________________8",
                        keyOps = listOf("encrypt", "decrypt"),
                        kty = "oct",
                        ext = true
                ),
                iv = "
                url = "dummyUrl"
        )

        assertNotEquals("YWxwaGFudW1lcmljYWxseWFscGhhbnVtZXJpY2FsbHlhbHBoYW51bWVyaWNhbGx5YWxwaGFudW1lcmljYWxseQ",
                checkDecryption("tJVNBVJ/vl36UQt4Y5e5m84bRUrQHhcdLPvS/7EkDvlkDLZXamBB6k8THbiawiKZ5Mnq9PZMSSbgOCvmnUBOMA",
                        encryptedFileInfo))
    }
}
