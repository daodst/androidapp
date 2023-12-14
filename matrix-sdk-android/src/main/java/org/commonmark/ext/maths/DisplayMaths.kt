
package org.commonmark.ext.maths

import org.commonmark.node.CustomBlock

internal class DisplayMaths(private val delimiter: DisplayDelimiter) : CustomBlock() {
    enum class DisplayDelimiter {
        DOUBLE_DOLLAR,
        SQUARE_BRACKET_ESCAPED
    }
}
