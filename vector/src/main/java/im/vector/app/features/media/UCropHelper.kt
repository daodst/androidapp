

package im.vector.app.features.media

import android.graphics.Color
import android.net.Uri
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import im.vector.app.R
import im.vector.app.core.resources.ColorProvider

fun createUCropWithDefaultSettings(colorProvider: ColorProvider,
                                   source: Uri,
                                   destination: Uri,
                                   toolbarTitle: String?): UCrop {
    return UCrop.of(source, destination)
            .withOptions(
                    UCrop.Options()
                            .apply {
                                setAllowedGestures(
                                         UCropActivity.SCALE,
                                         UCropActivity.ALL,
                                         UCropActivity.SCALE
                                )
                                setToolbarTitle(toolbarTitle)
                                
                                
                                
                                setToolbarColor(colorProvider.getColorFromAttribute(android.R.attr.colorBackground))
                                setToolbarWidgetColor(colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
                                
                                setRootViewBackgroundColor(colorProvider.getColorFromAttribute(android.R.attr.colorBackground))
                                
                                setStatusBarColor(colorProvider.getColor(R.color.android_status_bar_background_light))
                                setActiveControlsWidgetColor(colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                                
                                setLogoColor(Color.TRANSPARENT)
                            }
            )
}
