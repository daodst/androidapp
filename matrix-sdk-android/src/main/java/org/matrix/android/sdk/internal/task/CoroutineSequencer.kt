

package org.matrix.android.sdk.internal.task

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit


internal interface CoroutineSequencer {
    
    suspend fun <T> post(block: suspend () -> T): T
}

internal open class SemaphoreCoroutineSequencer : CoroutineSequencer {

    
    private val semaphore = Semaphore(1)

    override suspend fun <T> post(block: suspend () -> T): T {
        return semaphore.withPermit {
            block()
        }
    }
}
