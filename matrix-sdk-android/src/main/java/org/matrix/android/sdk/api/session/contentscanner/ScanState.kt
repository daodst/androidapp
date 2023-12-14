

package org.matrix.android.sdk.api.session.contentscanner

enum class ScanState {
    TRUSTED,
    INFECTED,
    UNKNOWN,
    IN_PROGRESS
}

data class ScanStatusInfo(
        val state: ScanState,
        val scanDateTimestamp: Long?,
        val humanReadableMessage: String?
)
