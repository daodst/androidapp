
package org.matrix.android.sdk.api.session.pushers

import androidx.lifecycle.LiveData
import java.util.UUID

interface PushersService {

    
    fun refreshPushers()

    
    suspend fun addHttpPusher(httpPusher: HttpPusher)

    
    fun enqueueAddHttpPusher(httpPusher: HttpPusher): UUID

    
    suspend fun addEmailPusher(email: String,
                               lang: String,
                               emailBranding: String,
                               appDisplayName: String,
                               deviceDisplayName: String,
                               append: Boolean = true)

    
    suspend fun testPush(url: String,
                         appId: String,
                         pushkey: String,
                         eventId: String)

    
    suspend fun removePusher(pusher: Pusher)

    
    suspend fun removeHttpPusher(pushkey: String, appId: String)

    
    suspend fun removeEmailPusher(email: String)

    
    fun getPushersLive(): LiveData<List<Pusher>>

    
    fun getPushers(): List<Pusher>

    data class HttpPusher(

            
            val pushkey: String,

            
            val appId: String,

            
            val profileTag: String,

            
            val lang: String,

            
            val appDisplayName: String,

            
            val deviceDisplayName: String,

            
            val url: String,

            
            val append: Boolean,

            
            val withEventIdOnly: Boolean
    )
}
