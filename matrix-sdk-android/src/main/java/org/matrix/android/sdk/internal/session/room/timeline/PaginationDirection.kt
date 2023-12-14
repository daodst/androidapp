

package org.matrix.android.sdk.internal.session.room.timeline

internal enum class PaginationDirection(val value: String) {
    
    FORWARDS("f"),

    
    BACKWARDS("b");

    fun reversed(): PaginationDirection {
        return when (this) {
            FORWARDS  -> BACKWARDS
            BACKWARDS -> FORWARDS
        }
    }
}
