

package im.vector.app.features.settings.push

import com.airbnb.epoxy.TypedEpoxyController
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.genericFooterItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import javax.inject.Inject

class PushGateWayController @Inject constructor(
        private val stringProvider: StringProvider
) : TypedEpoxyController<PushGatewayViewState>() {

    var interactionListener: PushGatewayItemInteractions? = null

    override fun buildModels(data: PushGatewayViewState?) {
        val host = this
        data?.pushGateways?.invoke()?.let { pushers ->
            if (pushers.isEmpty()) {
                genericFooterItem {
                    id("footer")
                    text(host.stringProvider.getString(R.string.settings_push_gateway_no_pushers).toEpoxyCharSequence())
                }
            } else {
                pushers.forEach {
                    pushGatewayItem {
                        id("${it.pushKey}_${it.appId}")
                        pusher(it)
                        host.interactionListener?.let {
                            interactions(it)
                        }
                    }
                }
            }
        } ?: run {
            genericFooterItem {
                id("loading")
                text(host.stringProvider.getString(R.string.loading).toEpoxyCharSequence())
            }
        }
    }
}
