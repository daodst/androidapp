

package org.matrix.android.sdk.internal.crypto.crosssigning

import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.junit.Test
import org.matrix.android.sdk.api.util.fromBase64

@Suppress("SpellCheckingInspection")
class ExtensionsKtTest {

    @Test
    fun testComparingBase64StringWithOrWithoutPadding() {
        
        "NMJyumnhMic".fromBase64().contentEquals("NMJyumnhMic".fromBase64()).shouldBeTrue()
        
        "NMJyumnhMic".fromBase64().contentEquals("NMJyumnhMic=".fromBase64()).shouldBeTrue()
    }

    @Test
    fun testBadBase64() {
        "===".fromBase64Safe().shouldBeNull()
    }
}
