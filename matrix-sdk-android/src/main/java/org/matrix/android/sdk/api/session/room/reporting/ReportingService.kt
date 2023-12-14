

package org.matrix.android.sdk.api.session.room.reporting


interface ReportingService {

    
    suspend fun reportContent(eventId: String, score: Int, reason: String)
}
