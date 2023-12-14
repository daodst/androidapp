

package org.matrix.android.sdk.api.session.call

import java.util.UUID

object CallIdGenerator {
    fun generate() = UUID.randomUUID().toString()
}
