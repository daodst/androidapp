
package org.commonmark.ext.maths.internal

import org.commonmark.ext.maths.DisplayMaths
import org.commonmark.ext.maths.InlineMaths
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

internal class DollarMathsDelimiterProcessor : DelimiterProcessor {
    override fun getOpeningCharacter() = '$'

    override fun getClosingCharacter() = '$'

    override fun getMinLength() = 1

    override fun getDelimiterUse(opener: DelimiterRun, closer: DelimiterRun): Int {
        return if (opener.length() == 1 && closer.length() == 1) 1 
        else if (opener.length() == 2 && closer.length() == 2) 2 
        else 0
    }

    override fun process(opener: Text, closer: Text, delimiterUse: Int) {
        val maths = if (delimiterUse == 1) {
            InlineMaths(InlineMaths.InlineDelimiter.SINGLE_DOLLAR)
        } else {
            DisplayMaths(DisplayMaths.DisplayDelimiter.DOUBLE_DOLLAR)
        }
        var tmp = opener.next
        while (tmp != null && tmp !== closer) {
            val next = tmp.next
            maths.appendChild(tmp)
            tmp = next
        }
        opener.insertAfter(maths)
    }
}
