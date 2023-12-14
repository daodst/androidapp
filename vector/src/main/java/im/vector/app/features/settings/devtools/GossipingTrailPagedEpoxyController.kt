

package im.vector.app.features.settings.devtools

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import im.vector.app.R
import im.vector.app.core.date.DateFormatKind
import im.vector.app.core.date.VectorDateFormatter
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.ui.list.GenericItem_
import im.vector.app.core.utils.createUIHandler
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import me.gujun.android.span.span
import org.matrix.android.sdk.api.session.crypto.model.ForwardedRoomKeyContent
import org.matrix.android.sdk.api.session.crypto.model.GossipingToDeviceObject
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyShareRequest
import org.matrix.android.sdk.api.session.crypto.model.SecretShareRequest
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.OlmEventContent
import org.matrix.android.sdk.api.session.events.model.content.SecretSendEventContent
import org.matrix.android.sdk.api.session.events.model.toModel
import javax.inject.Inject

class GossipingTrailPagedEpoxyController @Inject constructor(
        private val vectorDateFormatter: VectorDateFormatter,
        private val colorProvider: ColorProvider
) : PagedListEpoxyController<Event>(
        
        modelBuildingHandler = createUIHandler()
) {

    interface InteractionListener {
        fun didTap(event: Event)
    }

    var interactionListener: InteractionListener? = null

    override fun buildItemModel(currentPosition: Int, item: Event?): EpoxyModel<*> {
        val host = this
        val event = item ?: return GenericItem_().apply { id(currentPosition) }
        return GenericItem_().apply {
            id(event.hashCode())
            itemClickAction { host.interactionListener?.didTap(event) }
            title(
                    if (event.isEncrypted()) {
                        "${event.getClearType()} [encrypted]"
                    } else {
                        event.type
                    }?.toEpoxyCharSequence()
            )
            description(
                    span {
                        +host.vectorDateFormatter.format(event.ageLocalTs, DateFormatKind.DEFAULT_DATE_AND_TIME)
                        span("\nfrom: ") {
                            textStyle = "bold"
                        }
                        +"${event.senderId}"
                        apply {
                            if (event.getClearType() == EventType.ROOM_KEY_REQUEST) {
                                val content = event.getClearContent().toModel<RoomKeyShareRequest>()
                                span("\nreqId:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.requestId}"
                                span("\naction:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.action}"
                                if (content?.action == GossipingToDeviceObject.ACTION_SHARE_REQUEST) {
                                    span("\nsessionId:") {
                                        textStyle = "bold"
                                    }
                                    +" ${content.body?.sessionId}"
                                }
                                span("\nrequestedBy: ") {
                                    textStyle = "bold"
                                }
                                +"${content?.requestingDeviceId}"
                            } else if (event.getClearType() == EventType.FORWARDED_ROOM_KEY) {
                                val encryptedContent = event.content.toModel<OlmEventContent>()
                                val content = event.getClearContent().toModel<ForwardedRoomKeyContent>()
                                if (event.mxDecryptionResult == null) {
                                    span("**Failed to Decrypt** ${event.mCryptoError}") {
                                        textColor = host.colorProvider.getColorFromAttribute(R.attr.colorError)
                                    }
                                }
                                span("\nsessionId:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.sessionId}"
                                span("\nFrom Device (sender key):") {
                                    textStyle = "bold"
                                }
                                +" ${encryptedContent?.senderKey}"
                            } else if (event.getClearType() == EventType.ROOM_KEY) {
                                
                                val content = event.getClearContent()
                                span("\nsessionId:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.get("session_id")}"
                                span("\nroomId:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.get("room_id")}"
                                span("\nTo :") {
                                    textStyle = "bold"
                                }
                                +" ${content?.get("_dest") ?: "me"}"
                            } else if (event.getClearType() == EventType.SEND_SECRET) {
                                val content = event.getClearContent().toModel<SecretSendEventContent>()

                                span("\nrequestId:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.requestId}"
                                span("\nFrom Device:") {
                                    textStyle = "bold"
                                }
                                +" ${event.mxDecryptionResult?.payload?.get("sender_device")}"
                            } else if (event.getClearType() == EventType.REQUEST_SECRET) {
                                val content = event.getClearContent().toModel<SecretShareRequest>()
                                span("\nreqId:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.requestId}"
                                span("\naction:") {
                                    textStyle = "bold"
                                }
                                +" ${content?.action}"
                                if (content?.action == GossipingToDeviceObject.ACTION_SHARE_REQUEST) {
                                    span("\nsecretName:") {
                                        textStyle = "bold"
                                    }
                                    +" ${content.secretName}"
                                }
                                span("\nrequestedBy: ") {
                                    textStyle = "bold"
                                }
                                +"${content?.requestingDeviceId}"
                            } else if (event.getClearType() == EventType.ENCRYPTED) {
                                span("**Failed to Decrypt** ${event.mCryptoError}") {
                                    textColor = host.colorProvider.getColorFromAttribute(R.attr.colorError)
                                }
                            }
                        }
                    }.toEpoxyCharSequence()
            )
        }
    }
}
