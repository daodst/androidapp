

package org.matrix.android.sdk.api.session.sync

var initialSyncStrategy: InitialSyncStrategy = InitialSyncStrategy.Optimized()

sealed class InitialSyncStrategy {
    
    object Legacy : InitialSyncStrategy()

    
    data class Optimized(
            
            val minSizeToSplit: Long = 1_048_576, 
            
            val minSizeToStoreInFile: Long = 1024,
            
            val maxRoomsToInsert: Int = 100
    ) : InitialSyncStrategy()
}
