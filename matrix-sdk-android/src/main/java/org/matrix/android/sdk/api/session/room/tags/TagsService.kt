

package org.matrix.android.sdk.api.session.room.tags


interface TagsService {
    
    suspend fun addTag(tag: String, order: Double?)

    
    suspend fun deleteTag(tag: String)
}
