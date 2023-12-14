

package org.matrix.android.sdk.api.session.space.model

import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.MatrixPatterns


@JsonClass(generateAdapter = true)
data class SpaceOrderContent(
        val order: String? = null
) {
    fun safeOrder(): String? {
        return order?.takeIf { MatrixPatterns.isValidOrderString(it) }
    }
}
