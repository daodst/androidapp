

package org.matrix.android.sdk.internal.di

import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.internal.session.MockHttpInterceptor
import org.matrix.android.sdk.internal.session.TestInterceptor
import org.matrix.android.sdk.internal.util.BackgroundDetectionObserver
import org.matrix.android.sdk.internal.util.DefaultBackgroundDetectionObserver

@Module
internal object NoOpTestModule {

    @Provides
    @JvmStatic
    @MockHttpInterceptor
    fun providesTestInterceptor(): TestInterceptor? = null

    @Provides
    @JvmStatic
    @MatrixScope
    fun providesBackgroundDetectionObserver(): BackgroundDetectionObserver {
        return DefaultBackgroundDetectionObserver()
    }
}
