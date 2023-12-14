

package org.matrix.android.sdk.api.session.room.model.message

object MessageType {
    
    const val MSGTYPE_TEXT_CREATE_CLUSTER = "m.room.create_cluster"

    
    const val MSGTYPE_TEXT_WELCOME = "u.welcome_register"

    
    const val MSGTYPE_TEXT = "m.text"
    const val MSGTYPE_JOIN_TEXT = "m.join.text"
    const val MSGTYPE_CUS_TEXT = "m.cus.txt.text"
    const val MSGTYPE_AWARD_TEXT = "m.award.text"
    const val MSGTYPE_PLEDGE_AWARD_TEXT = "m.pledge.award.text"
    const val MSGTYPE_CLUSTER_TEXT = "m.cluster.text"
    const val MSGTYPE_DPOS_TEXT = "m.dpos.text"
    const val MSGTYPE_DPOSOVER_TEXT = "m.dposover.text"
    const val MSGTYPE_LORD_TEXT = "m.lord.text"
    const val MSGTYPE_VOTE_TEXT = "m.vote.text"
    const val MSGTYPE_DAO_TEXT = "m.dao.text"
    const val MSGTYPE_POS_TEXT = "m.pos.text"
    const val MSGTYPE_CHART_TEXT = "m.chart.text"
    const val MSGTYPE_EMOTE = "m.emote"
    const val MSGTYPE_NOTICE = "m.notice"
    const val MSGTYPE_IMAGE = "m.image"
    const val MSGTYPE_AUDIO = "m.audio"
    const val MSGTYPE_VIDEO = "m.video"
    const val MSGTYPE_LOCATION = "m.location"
    const val MSGTYPE_FILE = "m.file"
    const val MSGTYPE_RED_PACKET = "m.red.packet"
    const val MSGTYPE_GIFTS = "m.gifts"

    const val MSGTYPE_VERIFICATION_REQUEST = "m.key.verification.request"

    
    
    const val MSGTYPE_STICKER_LOCAL = "org.matrix.android.sdk.sticker"

    
    
    const val MSGTYPE_POLL_START = "org.matrix.android.sdk.poll.start"
    const val MSGTYPE_POLL_RESPONSE = "org.matrix.android.sdk.poll.response"

    const val MSGTYPE_CONFETTI = "nic.custom.confetti"
    const val MSGTYPE_SNOWFALL = "io.element.effect.snowfall"

    
    const val MSGTYPE_LIVE_LOCATION_STATE = "org.matrix.android.sdk.livelocation.state"
    const val MSGTYPE_LIVE_LOCATION = "org.matrix.android.sdk.livelocation"
}
