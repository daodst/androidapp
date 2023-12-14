

package org.matrix.android.sdk.internal.crypto.util

import java.util.UUID

internal object RequestIdHelper {
    fun createUniqueRequestId() = UUID.randomUUID().toString()
}
