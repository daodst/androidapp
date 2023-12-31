

package im.vector.app.features.home.room.detail.timeline.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.glide.GlideApp
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.home.AvatarRenderer
import org.matrix.android.sdk.api.util.toMatrixItem
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationPinProvider @Inject constructor(
        private val context: Context,
        private val activeSessionHolder: ActiveSessionHolder,
        private val dimensionConverter: DimensionConverter,
        private val avatarRenderer: AvatarRenderer,
        private val matrixItemColorProvider: MatrixItemColorProvider
) {
    private val cache = mutableMapOf<String, Drawable>()

    private val glideRequests by lazy {
        GlideApp.with(context)
    }

    
    fun create(userId: String?, callback: (Drawable) -> Unit) {
        if (userId == null) {
            callback(ContextCompat.getDrawable(context, R.drawable.ic_location_pin)!!)
            return
        }

        if (cache.contains(userId)) {
            callback(cache[userId]!!)
            return
        }

        activeSessionHolder
                .getActiveSession()
                .getUser(userId)
                ?.toMatrixItem()
                ?.let { userItem ->
                    val size = dimensionConverter.dpToPx(44)
                    val bgTintColor = matrixItemColorProvider.getColor(userItem)
                    avatarRenderer.render(glideRequests, userItem, object : CustomTarget<Drawable>(size, size) {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            Timber.d("## Location: onResourceReady")
                            val pinDrawable = createPinDrawable(resource, bgTintColor)
                            cache[userId] = pinDrawable
                            callback(pinDrawable)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            
                            
                            Timber.d("## Location: onLoadCleared")
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            Timber.w("## Location: onLoadFailed")
                            errorDrawable ?: return
                            val pinDrawable = createPinDrawable(errorDrawable, bgTintColor)
                            cache[userId] = pinDrawable
                            callback(pinDrawable)
                        }
                    })
                }
    }

    private fun createPinDrawable(drawable: Drawable, @ColorInt bgTintColor: Int): Drawable {
        val bgUserPin = ContextCompat.getDrawable(context, R.drawable.bg_map_user_pin)!!
        
        DrawableCompat.setTint(bgUserPin.mutate(), bgTintColor)
        val layerDrawable = LayerDrawable(arrayOf(bgUserPin, drawable))
        val horizontalInset = dimensionConverter.dpToPx(4)
        val topInset = dimensionConverter.dpToPx(4)
        val bottomInset = dimensionConverter.dpToPx(8)
        layerDrawable.setLayerInset(1, horizontalInset, topInset, horizontalInset, bottomInset)
        return layerDrawable
    }
}
