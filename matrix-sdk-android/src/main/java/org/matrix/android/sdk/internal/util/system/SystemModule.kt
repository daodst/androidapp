

package org.matrix.android.sdk.internal.util.system

import dagger.Binds
import dagger.Module

@Module
internal abstract class SystemModule {

    @Binds
    abstract fun bindBuildVersionSdkIntProvider(provider: DefaultBuildVersionSdkIntProvider): BuildVersionSdkIntProvider
}
