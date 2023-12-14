

package org.matrix.android.sdk.api


interface MatrixCallback<in T> {

    
    fun onSuccess(data: T) {
        
    }

    
    fun onFailure(failure: Throwable) {
        
    }
}


class NoOpMatrixCallback<T> : MatrixCallback<T>
