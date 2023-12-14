

package im.vector.app.features.settings.devtools

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import im.vector.app.core.date.DateFormatKind
import im.vector.app.core.date.VectorDateFormatter
import im.vector.app.core.ui.list.GenericItem_
import im.vector.app.core.utils.createUIHandler
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import me.gujun.android.span.span
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import javax.inject.Inject

class IncomingKeyRequestPagedController @Inject constructor(
        private val vectorDateFormatter: VectorDateFormatter
) : PagedListEpoxyController<IncomingRoomKeyRequest>(
        
        modelBuildingHandler = createUIHandler()
) {

    interface InteractionListener {
        
    }

    var interactionListener: InteractionListener? = null

    override fun buildItemModel(currentPosition: Int, item: IncomingRoomKeyRequest?): EpoxyModel<*> {
        val host = this
        val roomKeyRequest = item ?: return GenericItem_().apply { id(currentPosition) }

        return GenericItem_().apply {
            id(roomKeyRequest.requestId)
            title(roomKeyRequest.requestId?.toEpoxyCharSequence())
            description(
                    span {
                        span("From: ") {
                            textStyle = "bold"
                        }
                        span("${roomKeyRequest.userId}")
                        +host.vectorDateFormatter.format(roomKeyRequest.localCreationTimestamp, DateFormatKind.DEFAULT_DATE_AND_TIME)
                        span("\nsessionId:") {
                            textStyle = "bold"
                        }
                        +"${roomKeyRequest.requestBody?.sessionId}"
                        span("\nFrom device:") {
                            textStyle = "bold"
                        }
                        +"${roomKeyRequest.deviceId}"
                        span("\nstate: ") {
                            textStyle = "bold"
                        }
                        +roomKeyRequest.state.name
                    }.toEpoxyCharSequence()
            )
        }
    }
}
