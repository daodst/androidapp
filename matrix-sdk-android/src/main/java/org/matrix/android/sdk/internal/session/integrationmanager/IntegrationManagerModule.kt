

package org.matrix.android.sdk.internal.session.integrationmanager

import dagger.Binds
import dagger.Module
import org.matrix.android.sdk.api.session.integrationmanager.IntegrationManagerService

@Module
internal abstract class IntegrationManagerModule {

    @Binds
    abstract fun bindIntegrationManagerService(service: DefaultIntegrationManagerService): IntegrationManagerService
}
