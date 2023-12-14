

package org.matrix.android.sdk.internal.network

import dagger.Module
import dagger.Provides

@Module
internal object RequestModule {

    @Provides
    fun providesRequestExecutor(): RequestExecutor {
        return DefaultRequestExecutor
    }
}
