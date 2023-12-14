
package im.vector.app.features.discovery

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder

@EpoxyModelClass(layout = R2.layout.item_settings_progress)
abstract class SettingsProgressItem : EpoxyModelWithHolder<SettingsProgressItem.Holder>() {

    class Holder : VectorEpoxyHolder()
}
