

package org.matrix.android.sdk.internal.session.room.send.pills

import javax.inject.Inject

internal class MentionLinkSpecComparator @Inject constructor() : Comparator<MentionLinkSpec> {

    override fun compare(o1: MentionLinkSpec, o2: MentionLinkSpec): Int {
        return when {
            o1.start < o2.start -> -1
            o1.start > o2.start -> 1
            o1.end < o2.end     -> 1
            o1.end > o2.end     -> -1
            else                -> 0
        }
    }
}
