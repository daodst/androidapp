

package org.matrix.android.sdk.api.session

import androidx.annotation.MainThread


interface SessionLifecycleObserver {
    
    @MainThread
    fun onSessionStarted(session: Session) {
        
    }

    
    @MainThread
    fun onClearCache(session: Session) {
        
    }

    
    @MainThread
    fun onSessionStopped(session: Session) {
        
    }
}
