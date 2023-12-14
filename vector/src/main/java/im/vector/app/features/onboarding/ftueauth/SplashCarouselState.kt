

package im.vector.app.features.onboarding.ftueauth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence

data class SplashCarouselState(
        val items: List<Item>
) {
    data class Item(
            val title: EpoxyCharSequence,
            @StringRes val body: Int,
            @DrawableRes val image: Int,
            @DrawableRes val pageBackground: Int
    )
}
