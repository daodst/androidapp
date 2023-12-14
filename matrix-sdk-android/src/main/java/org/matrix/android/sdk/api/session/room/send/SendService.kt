

package org.matrix.android.sdk.api.session.room.send

import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.message.GIFTS_NUM_DEFAULT
import org.matrix.android.sdk.api.session.room.model.message.GIFTS_TYPE_FLOWERS
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.message.PollType
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.util.Cancelable

const val CUS_TEXT_TYPE_NORMAL = 0;
const val CUS_TEXT_TYPE_WARNING = 1;


const val CUS_AWARD_DEFAULT = 0


const val CUS_AWARD_RECEIVED = 1


const val CUS_AWARD_INVALID = 2


const val CUS_AWARD_TAKE_BACK = 3


interface SendService {

    
    fun sendEvent(eventType: String, content: Content?): Cancelable

    
    fun sendTextMessage(text: CharSequence, msgType: String = MessageType.MSGTYPE_TEXT, autoMarkdown: Boolean = false): Cancelable
    fun sendThanks(text: CharSequence, msgType: String = MessageType.MSGTYPE_TEXT, autoMarkdown: Boolean = false): Cancelable

    
    fun sendCusTxtMessage(listTitle: String = "", nickName: String = "", title: String = "", content: String = "", forHtml: Boolean = false, type: Int = CUS_TEXT_TYPE_NORMAL)

    
    fun sendCusPledgeAwardMessage()

    
    fun sendCusAwardMessage(type: Int)

    
    
    fun sendCusVoteMessage(param: String = "", title: String = "", time: String = ""): Cancelable

    
    fun sendCusChartMessage(
            deviceRate: String = "",
            deviceRateSmall: String = "",
            deviceRateUp: Boolean = false,
            connRate: String = "",
            connRateSmall: String = "",
            connRateUp: Boolean = false,
            newDeviceNum: String = "",
            dvmNum: String = "",
            posNum: String = "",
            day3Pos: String = "",
            day3Active: String = "",
            day3Lg: String = "",
            day7Pos: String = "",
            day7Active: String = "",
            day7Lg: String = "",
            file: String = ""
    )

    
    fun sendCusDaoMessage()

    
    fun sendCusPosMessage(listTitle: String, nickName: String, title: String, content: String, btStr: String, type: String, param: String)

    
    fun sendCusClusterMessage(content: String = "")

    
    fun sendCusDposMessage(content: String = "")

    
    fun sendCusDposOverMessage(content: String = "")

    
    fun sendCusLordMessage(content: String = "")

    
    fun sendFormattedTextMessage(text: String, formattedText: String, msgType: String = MessageType.MSGTYPE_TEXT): Cancelable

    
    fun sendQuotedTextMessage(quotedEvent: TimelineEvent, text: String, autoMarkdown: Boolean, rootThreadEventId: String? = null): Cancelable

    
    fun sendMedia(attachment: ContentAttachmentData,
                  compressBeforeSending: Boolean,
                  roomIds: Set<String>,
                  rootThreadEventId: String? = null): Cancelable

    fun sendCusMedia(appid: String,
                     content: String,
                     fromLan: String,
                     toLan: String
    ): Cancelable

    
    fun sendMedias(attachments: List<ContentAttachmentData>,
                   compressBeforeSending: Boolean,
                   roomIds: Set<String>,
                   rootThreadEventId: String? = null): Cancelable

    
    fun sendPoll(pollType: PollType, question: String, options: List<String>): Cancelable

    
    fun voteToPoll(pollEventId: String, answerId: String): Cancelable

    
    fun endPoll(pollEventId: String): Cancelable

    
    fun redactEvent(event: Event, reason: String?): Cancelable

    
    fun resendTextMessage(localEcho: TimelineEvent): Cancelable

    
    fun resendMediaMessage(localEcho: TimelineEvent): Cancelable

    
    fun sendLocation(latitude: Double, longitude: Double, uncertainty: Double?, isUserLocation: Boolean): Cancelable

    
    fun sendRedPacket(redPacketId: String?, transferNum: String, symbol: String): Cancelable

    
    fun sendGifts(text: CharSequence = "", giftsNum: Int = GIFTS_NUM_DEFAULT, giftsType: Int = GIFTS_TYPE_FLOWERS): Cancelable

    
    fun sendLiveLocation(beaconInfoEventId: String, latitude: Double, longitude: Double, uncertainty: Double?): Cancelable

    
    fun deleteFailedEcho(localEcho: TimelineEvent)

    
    fun cancelSend(eventId: String)

    
    fun resendAllFailedMessages()

    
    fun cancelAllFailedMessages()
}
