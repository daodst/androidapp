

package org.matrix.android.sdk.internal.di

import com.squareup.moshi.Moshi
import org.matrix.android.sdk.api.session.room.model.message.MessageAudioContent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusAwardContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusChartContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusClusterContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusDaoContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusDposContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusDposOverContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusJoinContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusLordContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusPledgeAwardContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusPosContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusTxtContent
import org.matrix.android.sdk.api.session.room.model.message.MessageCusVoteContent
import org.matrix.android.sdk.api.session.room.model.message.MessageDefaultContent
import org.matrix.android.sdk.api.session.room.model.message.MessageEmoteContent
import org.matrix.android.sdk.api.session.room.model.message.MessageFileContent
import org.matrix.android.sdk.api.session.room.model.message.MessageGiftsContent
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageLocationContent
import org.matrix.android.sdk.api.session.room.model.message.MessageNoticeContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollResponseContent
import org.matrix.android.sdk.api.session.room.model.message.MessageRedPacketContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.message.MessageVerificationRequestContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVideoContent
import org.matrix.android.sdk.internal.network.parsing.CipherSuiteMoshiAdapter
import org.matrix.android.sdk.internal.network.parsing.ForceToBooleanJsonAdapter
import org.matrix.android.sdk.internal.network.parsing.RuntimeJsonAdapterFactory
import org.matrix.android.sdk.internal.network.parsing.TlsVersionMoshiAdapter
import org.matrix.android.sdk.internal.network.parsing.UriMoshiAdapter
import org.matrix.android.sdk.internal.session.sync.parsing.DefaultLazyRoomSyncEphemeralJsonAdapter

internal object MoshiProvider {

    private val moshi: Moshi = Moshi.Builder()
            .add(UriMoshiAdapter())
            .add(ForceToBooleanJsonAdapter())
            .add(CipherSuiteMoshiAdapter())
            .add(TlsVersionMoshiAdapter())
            
            .addLast(DefaultLazyRoomSyncEphemeralJsonAdapter())
            .add(
                    RuntimeJsonAdapterFactory.of(MessageContent::class.java, "msgtype", MessageDefaultContent::class.java)
                            .registerSubtype(MessageTextContent::class.java, MessageType.MSGTYPE_TEXT)
                            .registerSubtype(MessageCusVoteContent::class.java, MessageType.MSGTYPE_VOTE_TEXT)
                            .registerSubtype(MessageCusDaoContent::class.java, MessageType.MSGTYPE_DAO_TEXT)
                            .registerSubtype(MessageCusTxtContent::class.java, MessageType.MSGTYPE_CUS_TEXT)
                            .registerSubtype(MessageCusPosContent::class.java, MessageType.MSGTYPE_POS_TEXT)
                            .registerSubtype(MessageCusChartContent::class.java, MessageType.MSGTYPE_CHART_TEXT)
                            .registerSubtype(MessageCusJoinContent::class.java, MessageType.MSGTYPE_JOIN_TEXT)
                            .registerSubtype(MessageCusAwardContent::class.java, MessageType.MSGTYPE_AWARD_TEXT)
                            .registerSubtype(MessageCusPledgeAwardContent::class.java, MessageType.MSGTYPE_PLEDGE_AWARD_TEXT)
                            .registerSubtype(MessageCusClusterContent::class.java, MessageType.MSGTYPE_CLUSTER_TEXT)
                            .registerSubtype(MessageCusDposContent::class.java, MessageType.MSGTYPE_DPOS_TEXT)
                            .registerSubtype(MessageCusDposOverContent::class.java, MessageType.MSGTYPE_DPOSOVER_TEXT)
                            .registerSubtype(MessageCusLordContent::class.java, MessageType.MSGTYPE_LORD_TEXT)
                            .registerSubtype(MessageNoticeContent::class.java, MessageType.MSGTYPE_NOTICE)
                            .registerSubtype(MessageEmoteContent::class.java, MessageType.MSGTYPE_EMOTE)
                            .registerSubtype(MessageAudioContent::class.java, MessageType.MSGTYPE_AUDIO)
                            .registerSubtype(MessageImageContent::class.java, MessageType.MSGTYPE_IMAGE)
                            .registerSubtype(MessageVideoContent::class.java, MessageType.MSGTYPE_VIDEO)
                            .registerSubtype(MessageLocationContent::class.java, MessageType.MSGTYPE_LOCATION)
                            .registerSubtype(MessageFileContent::class.java, MessageType.MSGTYPE_FILE)
                            .registerSubtype(MessageVerificationRequestContent::class.java, MessageType.MSGTYPE_VERIFICATION_REQUEST)
                            .registerSubtype(MessagePollResponseContent::class.java, MessageType.MSGTYPE_POLL_RESPONSE)
                            .registerSubtype(MessageRedPacketContent::class.java, MessageType.MSGTYPE_RED_PACKET)
                            .registerSubtype(MessageGiftsContent::class.java, MessageType.MSGTYPE_GIFTS)
            )
            .add(SerializeNulls.JSON_ADAPTER_FACTORY)
            .build()

    fun providesMoshi(): Moshi {
        return moshi
    }
}
