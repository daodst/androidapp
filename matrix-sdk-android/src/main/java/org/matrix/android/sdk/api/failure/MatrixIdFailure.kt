

package org.matrix.android.sdk.api.failure

sealed class MatrixIdFailure : Failure.FeatureFailure() {
    object InvalidMatrixId : MatrixIdFailure()
}
