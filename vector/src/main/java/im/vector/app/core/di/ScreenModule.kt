

package im.vector.app.core.di

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import im.vector.app.core.glide.GlideApp

@Module
@InstallIn(ActivityComponent::class)
object ScreenModule {

    @Provides
    fun providesGlideRequests(context: AppCompatActivity) = GlideApp.with(context)

    @Provides
    @ActivityScoped
    fun providesSharedViewPool() = RecyclerView.RecycledViewPool()
}
