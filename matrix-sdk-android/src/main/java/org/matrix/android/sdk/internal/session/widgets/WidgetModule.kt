

package org.matrix.android.sdk.internal.session.widgets

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.widgets.WidgetPostAPIMediator
import org.matrix.android.sdk.api.session.widgets.WidgetService
import org.matrix.android.sdk.api.session.widgets.WidgetURLFormatter
import org.matrix.android.sdk.internal.session.widgets.token.DefaultGetScalarTokenTask
import org.matrix.android.sdk.internal.session.widgets.token.GetScalarTokenTask
import retrofit2.Retrofit

@Module
internal abstract class WidgetModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesWidgetsAPI(retrofit: Retrofit): WidgetsAPI {
            return retrofit.create(WidgetsAPI::class.java)
        }
    }

    @Binds
    abstract fun bindWidgetService(service: DefaultWidgetService): WidgetService

    @Binds
    abstract fun bindWidgetURLBuilder(formatter: DefaultWidgetURLFormatter): WidgetURLFormatter

    @Binds
    abstract fun bindWidgetPostAPIMediator(mediator: DefaultWidgetPostAPIMediator): WidgetPostAPIMediator

    @Binds
    abstract fun bindCreateWidgetTask(task: DefaultCreateWidgetTask): CreateWidgetTask

    @Binds
    abstract fun bindGetScalarTokenTask(task: DefaultGetScalarTokenTask): GetScalarTokenTask
}
