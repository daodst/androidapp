

package im.vector.app.features.home.room.detail.composer

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.command.ParsedCommand
import im.vector.app.features.home.room.detail.composer.voice.VoiceMessageRecorderView
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.session.room.model.message.MessageAudioContent

sealed class MessageComposerAction : VectorViewModelAction {
    data class SendMessage(val text: CharSequence, val autoMarkdown: Boolean) : MessageComposerAction()
    data class EnterEditMode(val eventId: String, val text: String) : MessageComposerAction()
    data class EnterQuoteMode(val eventId: String, val text: String) : MessageComposerAction()
    data class EnterReplyMode(val eventId: String, val text: String) : MessageComposerAction()
    data class EnterRegularMode(val text: String, val fromSharing: Boolean) : MessageComposerAction()
    data class UserIsTyping(val isTyping: Boolean) : MessageComposerAction()
    data class OnTextChanged(val text: CharSequence) : MessageComposerAction()
    data class OnEntersBackground(val composerText: String) : MessageComposerAction()
    data class SlashCommandConfirmed(val parsedCommand: ParsedCommand) : MessageComposerAction()

    
    data class InitializeVoiceRecorder(val attachmentData: ContentAttachmentData) : MessageComposerAction()
    data class OnVoiceRecordingUiStateChanged(val uiState: VoiceMessageRecorderView.RecordingUiState) : MessageComposerAction()
    data class OnVoiceTextRecordingUiStateChanged(val uiState: MessageTranslateView.TranslateRecordingUiState) : MessageComposerAction()
    data class OnLangChange(val isChinese: Boolean, val fromLan: String, val toLan: String) : MessageComposerAction()
    object StartRecordingVoiceMessage : MessageComposerAction()
    object StartTextRecordingVoiceMessage : MessageComposerAction()
    object TextRecordingEnded : MessageComposerAction()
    data class EndRecordingVoiceMessage(val isCancelled: Boolean, val rootThreadEventId: String?) : MessageComposerAction()
    data class  EndRecordingTextVoiceMessage(val content: String) : MessageComposerAction()
    object PauseRecordingVoiceMessage : MessageComposerAction()
    data class PlayOrPauseVoicePlayback(val eventId: String, val messageAudioContent: MessageAudioContent) : MessageComposerAction()
    object PlayOrPauseRecordingPlayback : MessageComposerAction()
    data class EndAllVoiceActions(val deleteRecord: Boolean = true) : MessageComposerAction()
    data class EndTextVoiceActions(val deleteRecord: Boolean = true) : MessageComposerAction()
    object CloseTextVoiceCloseActions : MessageComposerAction()
    data class VoiceWaveformTouchedUp(val eventId: String, val duration: Int, val percentage: Float) : MessageComposerAction()
    data class VoiceWaveformMovedTo(val eventId: String, val duration: Int, val percentage: Float) : MessageComposerAction()
    data class AudioSeekBarMovedTo(val eventId: String, val duration: Int, val percentage: Float) : MessageComposerAction()
}
