

package org.matrix.android.sdk.common

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.internal.auth.AuthModule
import org.matrix.android.sdk.internal.di.MatrixComponent
import org.matrix.android.sdk.internal.di.MatrixModule
import org.matrix.android.sdk.internal.di.MatrixScope
import org.matrix.android.sdk.internal.di.NetworkModule
import org.matrix.android.sdk.internal.raw.RawModule
import org.matrix.android.sdk.internal.settings.SettingsModule
import org.matrix.android.sdk.internal.util.system.SystemModule

@Component(modules = [
    TestModule::class,
    MatrixModule::class,
    NetworkModule::class,
    AuthModule::class,
    RawModule::class,
    SettingsModule::class,
    SystemModule::class
])
@MatrixScope
internal interface TestMatrixComponent : MatrixComponent {

    fun inject(matrix: TestMatrix)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context,
                   @BindsInstance matrixConfiguration: MatrixConfiguration): TestMatrixComponent
    }
}
