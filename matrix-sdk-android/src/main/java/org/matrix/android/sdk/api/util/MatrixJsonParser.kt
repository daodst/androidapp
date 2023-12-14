

package org.matrix.android.sdk.api.util

import com.squareup.moshi.Moshi
import org.matrix.android.sdk.internal.di.MoshiProvider


object MatrixJsonParser {
    
    fun getMoshi(): Moshi {
        return MoshiProvider.providesMoshi()
    }
}
