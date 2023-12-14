

package org.matrix.android.sdk

import org.junit.Rule
import org.matrix.android.sdk.test.shared.createTimberTestRule

interface MatrixTest {

    @Rule
    fun timberTestRule() = createTimberTestRule()
}
