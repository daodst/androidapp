

package org.matrix.android.sdk.api.util


interface Cancelable {

    
    fun cancel() {
        
    }
}

object NoOpCancellable : Cancelable
