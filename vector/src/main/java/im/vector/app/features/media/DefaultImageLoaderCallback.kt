

package im.vector.app.features.media

import com.github.piasy.biv.loader.ImageLoader
import java.io.File

interface DefaultImageLoaderCallback : ImageLoader.Callback {

    override fun onFinish() {
        
    }

    override fun onSuccess(image: File?) {
        
    }

    override fun onFail(error: Exception?) {
        
    }

    override fun onCacheHit(imageType: Int, image: File?) {
        
    }

    override fun onCacheMiss(imageType: Int, image: File?) {
        
    }

    override fun onProgress(progress: Int) {
        
    }

    override fun onStart() {
        
    }
}
