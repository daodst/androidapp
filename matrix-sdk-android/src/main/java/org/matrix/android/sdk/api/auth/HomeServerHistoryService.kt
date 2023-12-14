

package org.matrix.android.sdk.api.auth


interface HomeServerHistoryService {
    
    fun getKnownServersUrls(): List<String>

    
    fun addHomeServerToHistory(url: String)

    
    fun clearHistory()
}
