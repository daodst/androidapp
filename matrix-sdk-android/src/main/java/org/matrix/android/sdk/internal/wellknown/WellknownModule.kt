

package org.matrix.android.sdk.internal.wellknown

import dagger.Binds
import dagger.Module

@Module
internal abstract class WellknownModule {

    @Binds
    abstract fun bindGetWellknownTask(task: DefaultGetWellknownTask): GetWellknownTask
}
