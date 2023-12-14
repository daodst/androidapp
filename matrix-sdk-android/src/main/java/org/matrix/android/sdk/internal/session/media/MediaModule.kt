

package org.matrix.android.sdk.internal.session.media

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.media.MediaService
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class MediaModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesMediaAPI(retrofit: Retrofit): MediaAPI {
            return retrofit.create(MediaAPI::class.java)
        }
    }

    @Binds
    abstract fun bindMediaService(service: DefaultMediaService): MediaService

    @Binds
    abstract fun bindGetRawPreviewUrlTask(task: DefaultGetRawPreviewUrlTask): GetRawPreviewUrlTask

    @Binds
    abstract fun bindGetPreviewUrlTask(task: DefaultGetPreviewUrlTask): GetPreviewUrlTask

    @Binds
    abstract fun bindClearMediaCacheTask(task: DefaultClearPreviewUrlCacheTask): ClearPreviewUrlCacheTask
}
