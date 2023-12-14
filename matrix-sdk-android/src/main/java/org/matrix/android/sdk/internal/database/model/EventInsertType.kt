

package org.matrix.android.sdk.internal.database.model

internal enum class EventInsertType {
    INITIAL_SYNC,
    INCREMENTAL_SYNC,
    PAGINATION,
    LOCAL_ECHO
}
