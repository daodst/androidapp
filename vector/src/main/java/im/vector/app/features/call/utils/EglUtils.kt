
package im.vector.app.features.call.utils

import org.webrtc.EglBase
import timber.log.Timber


object EglUtils {

    

    
    @get:Synchronized var rootEglBase: EglBase? = null
        get() {
            if (field == null) {
                val configAttributes = EglBase.CONFIG_PLAIN
                try {
                    field = EglBase.createEgl14(configAttributes)
                            ?: EglBase.createEgl10(configAttributes) 
                } catch (ex: Throwable) {
                    Timber.e(ex, "Failed to create EglBase")
                }
            }
            return field
        }
        private set

    val rootEglBaseContext: EglBase.Context?
        get() {
            val eglBase = rootEglBase
            return eglBase?.eglBaseContext
        }
}
