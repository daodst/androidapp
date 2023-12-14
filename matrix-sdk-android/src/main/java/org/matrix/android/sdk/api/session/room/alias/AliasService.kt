

package org.matrix.android.sdk.api.session.room.alias

interface AliasService {
    
    suspend fun getRoomAliases(): List<String>

    
    suspend fun addAlias(aliasLocalPart: String)
}
