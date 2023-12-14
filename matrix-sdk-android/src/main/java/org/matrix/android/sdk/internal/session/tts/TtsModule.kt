

package org.matrix.android.sdk.internal.session.tts

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.tts.model.DefaultTranslateStateTask
import org.matrix.android.sdk.internal.session.tts.model.DefaultTranslateTask
import org.matrix.android.sdk.internal.session.tts.model.TranslateStateTask
import org.matrix.android.sdk.internal.session.tts.model.TranslateTask
import retrofit2.Retrofit

@Module
internal abstract class TtsModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesTtsAPI(retrofit: Retrofit): TtsAPI {
            return retrofit.create(TtsAPI::class.java)
        }
    }

    @Binds
    abstract fun bindTtsService(service: DefaultTtsService): TtsService

    @Binds
    abstract fun bindTranslateTask(service: DefaultTranslateTask): TranslateTask

    @Binds
    abstract fun bindTranslateStateTask(service: DefaultTranslateStateTask): TranslateStateTask
}
