

package org.matrix.android.sdk.internal.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.matrix.android.sdk.InstrumentedTest

@RunWith(AndroidJUnit4::class)
internal class JsonCanonicalizerTest : InstrumentedTest {

    @Test
    fun identityTest() {
        listOf(
                "{}",
                """{"a":true}""",
                """{"a":false}""",
                """{"a":1}""",
                """{"a":1.2}""",
                """{"a":null}""",
                """{"a":[]}""",
                """{"a":["b":"c"]}""",
                """{"a":["c":"b","d":"e"]}""",
                """{"a":["d":"b","c":"e"]}"""
        ).forEach {
            assertEquals(it,
                    JsonCanonicalizer.canonicalize(it))
        }
    }

    @Test
    fun reorderTest() {
        assertEquals("""{"a":true,"b":false}""",
                JsonCanonicalizer.canonicalize("""{"b":false,"a":true}"""))
    }

    @Test
    fun realSampleTest() {
        assertEquals("""{"algorithms":["m.megolm.v1.aes-sha2","m.olm.v1.curve25519-aes-sha2"],"device_id":"VSCUNFSOUI","keys":{"curve25519:VSCUNFSOUI":"utyOjnhiQ73qNhi9HlN0OgWIowe5gthTS8r0r9TcJ3o","ed25519:VSCUNFSOUI":"qNhEt+Yggaajet0hX\/FjTRLfySgs65ldYyomm7PIx6U"},"user_id":"@benoitx:matrix.org"}""",
                JsonCanonicalizer.canonicalize("""{"algorithms":["m.megolm.v1.aes-sha2","m.olm.v1.curve25519-aes-sha2"],"device_id":"VSCUNFSOUI","user_id":"@benoitx:matrix.org","keys":{"curve25519:VSCUNFSOUI":"utyOjnhiQ73qNhi9HlN0OgWIowe5gthTS8r0r9TcJ3o","ed25519:VSCUNFSOUI":"qNhEt+Yggaajet0hX/FjTRLfySgs65ldYyomm7PIx6U"}}"""))
    }

    @Test
    fun doubleQuoteTest() {
        assertEquals("{\"a\":\"\\\"\"}",
                JsonCanonicalizer.canonicalize("{\"a\":\"\\\"\"}"))
    }

    

    @Test
    fun matrixOrg001Test() {
        assertEquals("""{}""",
                JsonCanonicalizer.canonicalize("""{}"""))
    }

    @Test
    fun matrixOrg002Test() {
        assertEquals("""{"one":1,"two":"Two"}""",
                JsonCanonicalizer.canonicalize("""{
    "one": 1,
    "two": "Two"
}"""))
    }

    @Test
    fun matrixOrg003Test() {
        assertEquals("""{"a":"1","b":"2"}""",
                JsonCanonicalizer.canonicalize("""{
    "b": "2",
    "a": "1"
}"""))
    }

    @Test
    fun matrixOrg004Test() {
        assertEquals("""{"a":"1","b":"2"}""",
                JsonCanonicalizer.canonicalize("""{"b":"2","a":"1"}"""))
    }

    @Test
    fun matrixOrg005Test() {
        assertEquals("""{"auth":{"mxid":"@john.doe:example.com","profile":{"display_name":"John Doe","three_pids":[{"address":"john.doe@example.org","medium":"email"},{"address":"123456789","medium":"msisdn"}]},"success":true}}""",
                JsonCanonicalizer.canonicalize("""{
    "auth": {
        "success": true,
        "mxid": "@john.doe:example.com",
        "profile": {
            "display_name": "John Doe",
            "three_pids": [
                {
                    "medium": "email",
                    "address": "john.doe@example.org"
                },
                {
                    "medium": "msisdn",
                    "address": "123456789"
                }
            ]
        }
    }
}"""))
    }

    @Test
    fun matrixOrg006Test() {
        assertEquals("""{"a":""}""",
                JsonCanonicalizer.canonicalize("""{
    "a": ""
}"""))
    }

    @Test
    fun matrixOrg007Test() {
        assertEquals("""{"":1,"":2}""",
                JsonCanonicalizer.canonicalize("""{
    "": 2,
    "": 1
}"""))
    }

    @Test
    fun matrixOrg008Test() {
        assertEquals("""{"a":""}""",
                JsonCanonicalizer.canonicalize("{\"a\": \"\u65E5\"}"))
    }

    @Test
    fun matrixOrg009Test() {
        assertEquals("""{"a":null}""",
                JsonCanonicalizer.canonicalize("""{
    "a": null
}"""))
    }
}
