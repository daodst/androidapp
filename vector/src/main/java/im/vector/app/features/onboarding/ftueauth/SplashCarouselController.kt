

package im.vector.app.features.onboarding.ftueauth

import com.airbnb.epoxy.TypedEpoxyController
import javax.inject.Inject

class SplashCarouselController @Inject constructor() : TypedEpoxyController<SplashCarouselState>() {
    override fun buildModels(data: SplashCarouselState) {
        data.items.forEachIndexed { index, item ->
            splashCarouselItem {
                id(index)
                item(item)
            }
        }
    }
}
