

package org.matrix.android.sdk.test.shared

import net.lachlanmckee.timberjunit.TimberTestRule

internal fun createTimberTestRule(): TimberTestRule {
    return TimberTestRule.builder()
            .showThread(false)
            .showTimestamp(false)
            .onlyLogWhenTestFails(false)
            .build()
}
