

package im.vector.app.features.analytics

import kotlinx.coroutines.flow.Flow

interface VectorAnalytics : AnalyticsTracker {
    
    fun getUserConsent(): Flow<Boolean>

    
    suspend fun setUserConsent(userConsent: Boolean)

    
    fun didAskUserConsent(): Flow<Boolean>

    
    suspend fun setDidAskUserConsent()

    
    fun getAnalyticsId(): Flow<String>

    
    suspend fun setAnalyticsId(analyticsId: String)

    
    suspend fun onSignOut()

    
    fun init()
}
