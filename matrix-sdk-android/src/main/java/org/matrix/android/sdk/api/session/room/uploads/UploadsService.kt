

package org.matrix.android.sdk.api.session.room.uploads


interface UploadsService {

    
    suspend fun getUploads(numberOfEvents: Int, since: String?): GetUploadsResult
}
