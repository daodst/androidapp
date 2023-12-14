

package im.vector.app.features.home.room.detail.composer

import com.airbnb.mvrx.MavericksState
import im.vector.app.features.home.room.detail.arguments.TimelineArgs
import im.vector.app.features.home.room.detail.composer.voice.VoiceMessageRecorderView
import im.vector.lib.multipicker.entity.MultiPickerAudioType
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent


sealed interface SendMode {
    data class Regular(
            val text: String,
            val fromSharing: Boolean,
            
            private val ts: Long = System.currentTimeMillis()
    ) : SendMode

    data class Quote(val timelineEvent: TimelineEvent, val text: String) : SendMode
    data class Edit(val timelineEvent: TimelineEvent, val text: String) : SendMode
    data class Reply(val timelineEvent: TimelineEvent, val text: String) : SendMode
    data class Voice(val text: String) : SendMode
}

sealed interface CanSendStatus {
    object Allowed : CanSendStatus
    data class NoPermission(val izServerNoticeId: Boolean = false) : CanSendStatus
    data class UnSupportedE2eAlgorithm(val algorithm: String?) : CanSendStatus
}

fun CanSendStatus.boolean(): Boolean {
    return when (this) {
        CanSendStatus.Allowed                    -> true
        is CanSendStatus.NoPermission            -> false
        is CanSendStatus.UnSupportedE2eAlgorithm -> false
    }
}

data class MessageComposerViewState(
        val roomId: String,
        val canSendMessage: CanSendStatus = CanSendStatus.Allowed,
        val isSendButtonVisible: Boolean = false,
        val isTextVoiceVisible: Boolean = false,
        val isChinese: Boolean =true,
        val fromLan: String = "",
        val toLan: String = "",
        val rootThreadEventId: String? = null,
        val originText: String? = null,
        val partialText: String? = null,
        val translate: String? = null,
        val audioType: MultiPickerAudioType? = null,
        val startsThread: Boolean = false,
        val sendMode: SendMode = SendMode.Regular("", false),
        val voiceRecordingUiState: VoiceMessageRecorderView.RecordingUiState = VoiceMessageRecorderView.RecordingUiState.Idle,
        val voiceTextRecordingUiState: MessageTranslateView.TranslateRecordingUiState = MessageTranslateView.TranslateRecordingUiState.Idle
) : MavericksState {

    val isVoiceRecording = when (voiceRecordingUiState) {
        VoiceMessageRecorderView.RecordingUiState.Idle         -> false
        is VoiceMessageRecorderView.RecordingUiState.Locked,
        VoiceMessageRecorderView.RecordingUiState.Draft,
        is VoiceMessageRecorderView.RecordingUiState.Recording -> true
    }

    val isVoiceMessageIdle = !isVoiceRecording

    val isComposerVisible = canSendMessage.boolean() && !isVoiceRecording
    val isVoiceMessageRecorderVisible = canSendMessage.boolean() && !isSendButtonVisible
    val isTextVoiceMessageRecorderVisible = canSendMessage.boolean() && isTextVoiceVisible

    constructor(args: TimelineArgs) : this(
            roomId = args.roomId,
            startsThread = args.threadTimelineArgs?.startsThread.orFalse(),
            rootThreadEventId = args.threadTimelineArgs?.rootThreadEventId
    )

    fun isInThreadTimeline(): Boolean = rootThreadEventId != null
}
