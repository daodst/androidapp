

package org.matrix.android.sdk

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.matrix.android.sdk.test.shared.createTimberTestRule

interface InstrumentedTest {

    @Rule
    fun timberTestRule() = createTimberTestRule()

    fun context(): Context {
        return ApplicationProvider.getApplicationContext()
    }
}
