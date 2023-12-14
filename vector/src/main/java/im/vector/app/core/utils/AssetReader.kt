

package im.vector.app.core.utils

import android.content.Context
import timber.log.Timber
import javax.inject.Inject


class AssetReader @Inject constructor(private val context: Context) {

    
    private val cache = mutableMapOf<String, String?>()

    
    fun readAssetFile(assetFilename: String): String? {
        return cache.getOrPut(assetFilename, {
            return try {
                context.assets.open(assetFilename)
                        .use { asset ->
                            buildString {
                                var ch = asset.read()
                                while (ch != -1) {
                                    append(ch.toChar())
                                    ch = asset.read()
                                }
                            }
                        }
            } catch (e: Exception) {
                Timber.e(e, "## readAssetFile() failed")
                null
            }
        })
    }

    fun clearCache() {
        cache.clear()
    }
}
