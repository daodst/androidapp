

package org.matrix.android.sdk.api.session.room.uploads

data class GetUploadsResult(
        
        val uploadEvents: List<UploadEvent>,
        
        val nextToken: String,
        
        val hasMore: Boolean
)
