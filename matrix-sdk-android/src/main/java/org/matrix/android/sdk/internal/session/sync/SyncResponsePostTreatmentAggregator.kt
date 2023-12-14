

package org.matrix.android.sdk.internal.session.sync

internal class SyncResponsePostTreatmentAggregator {
    
    val ephemeralFilesToDelete = mutableListOf<String>()

    
    val directChatsToCheck = mutableMapOf<String, String>()

    
    val userIdsToFetch = mutableListOf<String>()
}
