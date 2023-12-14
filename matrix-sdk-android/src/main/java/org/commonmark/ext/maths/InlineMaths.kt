
package org.commonmark.ext.maths

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

internal class InlineMaths(private val delimiter: InlineDelimiter) : CustomNode(), Delimited {
    enum class InlineDelimiter {
        SINGLE_DOLLAR,
        ROUND_BRACKET_ESCAPED
    }

    override fun getOpeningDelimiter(): String {
        return when (delimiter) {
            InlineDelimiter.SINGLE_DOLLAR         -> "$"
            InlineDelimiter.ROUND_BRACKET_ESCAPED -> "\\("
        }
    }

    override fun getClosingDelimiter(): String {
        return when (delimiter) {
            InlineDelimiter.SINGLE_DOLLAR         -> "$"
            InlineDelimiter.ROUND_BRACKET_ESCAPED -> "\\)"
        }
    }
}
