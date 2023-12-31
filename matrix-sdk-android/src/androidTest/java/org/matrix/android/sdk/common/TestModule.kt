

package org.matrix.android.sdk.common

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.internal.di.MatrixComponent
import org.matrix.android.sdk.internal.di.MatrixScope
import org.matrix.android.sdk.internal.session.MockHttpInterceptor
import org.matrix.android.sdk.internal.session.TestInterceptor
import org.matrix.android.sdk.internal.util.BackgroundDetectionObserver

@Module
internal abstract class TestModule {
    @Binds
    abstract fun providesMatrixComponent(testMatrixComponent: TestMatrixComponent): MatrixComponent

    @Module
    companion object {

        val interceptors = ArrayList<TestInterceptor>()

        fun interceptorForSession(sessionId: String): TestInterceptor? = interceptors.firstOrNull { it.sessionId == sessionId }

        @Provides
        @JvmStatic
        @MockHttpInterceptor
        fun providesTestInterceptor(): TestInterceptor? {
            return MockOkHttpInterceptor().also {
                interceptors.add(it)
            }
        }

        @Provides
        @JvmStatic
        @MatrixScope
        fun providesBackgroundDetectionObserver(): BackgroundDetectionObserver {
            return TestBackgroundDetectionObserver()
        }
    }
}
