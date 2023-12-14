

package im.vector.app.core.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import im.vector.app.ActiveSessionDataSource
import im.vector.app.core.glide.FactoryUrl
import org.matrix.android.sdk.api.session.Session
import java.io.InputStream
import javax.inject.Inject


class ImageManager @Inject constructor(
        private val context: Context,
        private val activeSessionDataSource: ActiveSessionDataSource
) {

    fun onSessionStarted(session: Session) {
        
        BigImageViewer.initialize(GlideImageLoader.with(context, session.getOkHttpClient()))

        val glide = Glide.get(context)

        
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, FactoryUrl(activeSessionDataSource))
    }
}
