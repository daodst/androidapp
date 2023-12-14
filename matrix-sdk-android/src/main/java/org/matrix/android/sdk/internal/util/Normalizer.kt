

package org.matrix.android.sdk.internal.util

import java.text.Normalizer
import javax.inject.Inject

internal class Normalizer @Inject constructor() {

    fun normalize(input: String): String {
        return Normalizer.normalize(input.lowercase(), Normalizer.Form.NFD)
    }
}
