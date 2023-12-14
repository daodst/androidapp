

package org.matrix.android.sdk.internal.crypto.algorithms

internal interface IMXGroupEncryption {

    
    fun discardSessionKey()

    suspend fun preshareKey(userIds: List<String>)

    
    suspend fun reshareKey(groupSessionId: String,
                           userId: String,
                           deviceId: String,
                           senderKey: String): Boolean
}
