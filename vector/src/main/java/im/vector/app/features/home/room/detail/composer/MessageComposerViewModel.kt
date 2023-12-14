

package im.vector.app.features.home.room.detail.composer

import android.content.Context
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.time.Clock
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.extensions.toAnalyticsComposer
import im.vector.app.features.analytics.extensions.toAnalyticsJoinedRoom
import im.vector.app.features.attachments.toContentAttachmentData
import im.vector.app.features.command.CommandParser
import im.vector.app.features.command.ParsedCommand
import im.vector.app.features.home.room.detail.ChatEffect
import im.vector.app.features.home.room.detail.composer.rainbow.RainbowGenerator
import im.vector.app.features.home.room.detail.composer.voice.VoiceMessageRecorderView
import im.vector.app.features.home.room.detail.toMessageType
import im.vector.app.features.powerlevel.PowerLevelsFlowFactory
import im.vector.app.features.session.coroutineScope
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.voice.VoicePlayerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.izServerNoticeId
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.getRootThreadEventId
import org.matrix.android.sdk.api.session.events.model.isThread
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomAvatarContent
import org.matrix.android.sdk.api.session.room.model.RoomEncryptionAlgorithm
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.shouldRenderInThread
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.send.UserDraft
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.getRelationContent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent
import org.matrix.android.sdk.api.session.space.CreateSpaceParams
import org.matrix.android.sdk.flow.flow
import org.matrix.android.sdk.flow.unwrap
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import timber.log.Timber
import java.io.IOException

class MessageComposerViewModel @AssistedInject constructor(@Assisted initialState: MessageComposerViewState, private val clock: Clock, private val session: Session, private val context: Context, private val stringProvider: StringProvider, private val vectorPreferences: VectorPreferences, private val commandParser: CommandParser, private val rainbowGenerator: RainbowGenerator, private val audioMessageHelper: AudioMessageHelper, private val analyticsTracker: AnalyticsTracker, private val voicePlayerHelper: VoicePlayerHelper) : VectorViewModel<MessageComposerViewState, MessageComposerAction, MessageComposerViewEvents>(
        initialState
) {

    private val room = session.getRoom(initialState.roomId)!!

    
    private var currentComposerText: CharSequence = ""
    private val amplitudeList = mutableListOf<Int>()

    init {
        amplitudeList.add(735)
        amplitudeList.add(521)
        amplitudeList.add(116)
        amplitudeList.add(59)
        amplitudeList.add(93)
        amplitudeList.add(85)
        amplitudeList.add(1574)
        amplitudeList.add(790)
        amplitudeList.add(987)
        amplitudeList.add(247)
        amplitudeList.add(1427)
        amplitudeList.add(1150)
        amplitudeList.add(885)
        amplitudeList.add(476)
        amplitudeList.add(138)
        amplitudeList.add(7300)
        amplitudeList.add(4219)
        amplitudeList.add(7716)
        amplitudeList.add(5995)
        amplitudeList.add(6355)
        amplitudeList.add(4903)
        amplitudeList.add(5800)
        amplitudeList.add(6238)
        amplitudeList.add(5055)
        amplitudeList.add(2841)
        amplitudeList.add(3616)
        amplitudeList.add(4271)
        amplitudeList.add(4727)
        amplitudeList.add(5787)
        amplitudeList.add(4776)
        amplitudeList.add(4093)
        amplitudeList.add(6689)
        amplitudeList.add(5598)
        amplitudeList.add(656)
        amplitudeList.add(278)
        amplitudeList.add(164)
        amplitudeList.add(5371)
        amplitudeList.add(7931)
        amplitudeList.add(4155)
        amplitudeList.add(4727)
        amplitudeList.add(5787)
        amplitudeList.add(4776)
        amplitudeList.add(4093)
        amplitudeList.add(6689)
        amplitudeList.add(5598)
        amplitudeList.add(656)
        amplitudeList.add(278)
        amplitudeList.add(164)
        amplitudeList.add(5371)
        amplitudeList.add(7931)
        amplitudeList.add(7590)
        amplitudeList.add(4155)
        amplitudeList.add(8485)
        amplitudeList.add(9232)
        amplitudeList.add(8192)
        amplitudeList.add(5185)
        amplitudeList.add(5943)
        amplitudeList.add(6364)
        amplitudeList.add(4059)
        amplitudeList.add(4049)
        amplitudeList.add(8476)
        amplitudeList.add(4299)
        amplitudeList.add(397)
        amplitudeList.add(287)
        amplitudeList.add(537)

        loadDraftIfAny()
        observePowerLevelAndEncryption()
        subscribeToStateInternal()
    }

    override fun handle(action: MessageComposerAction) {
        when (action) {
            
            is MessageComposerAction.OnVoiceTextRecordingUiStateChanged -> handleOnVoiceTextRecordingUiStateChanged(action)
            
            is MessageComposerAction.CloseTextVoiceCloseActions         -> handleCloseTextVoiceCloseActions()
            
            is MessageComposerAction.StartTextRecordingVoiceMessage     -> handleTextStartRecordingVoiceMessage()
            
            is MessageComposerAction.TextRecordingEnded                 -> handleTextRecordingEnded()
            
            is MessageComposerAction.EndRecordingTextVoiceMessage       -> handleEndRecordingTextVoiceMessage(action)

            is MessageComposerAction.EnterEditMode                      -> handleEnterEditMode(action)
            is MessageComposerAction.OnLangChange                       -> handleOnLangChange(action)
            is MessageComposerAction.EnterQuoteMode                     -> handleEnterQuoteMode(action)
            is MessageComposerAction.EnterRegularMode                   -> handleEnterRegularMode(action)
            is MessageComposerAction.EnterReplyMode                     -> handleEnterReplyMode(action)
            is MessageComposerAction.SendMessage                        -> handleSendMessage(action)
            is MessageComposerAction.UserIsTyping                       -> handleUserIsTyping(action)
            is MessageComposerAction.OnTextChanged                      -> handleOnTextChanged(action)
            is MessageComposerAction.OnVoiceRecordingUiStateChanged     -> handleOnVoiceRecordingUiStateChanged(action)

            is MessageComposerAction.StartRecordingVoiceMessage         -> handleStartRecordingVoiceMessage()

            is MessageComposerAction.EndRecordingVoiceMessage           -> handleEndRecordingVoiceMessage(action.isCancelled, action.rootThreadEventId)

            is MessageComposerAction.PlayOrPauseVoicePlayback           -> handlePlayOrPauseVoicePlayback(action)
            MessageComposerAction.PauseRecordingVoiceMessage            -> handlePauseRecordingVoiceMessage()
            MessageComposerAction.PlayOrPauseRecordingPlayback          -> handlePlayOrPauseRecordingPlayback()
            is MessageComposerAction.EndAllVoiceActions                 -> handleEndAllVoiceActions(action.deleteRecord)
            is MessageComposerAction.EndTextVoiceActions                -> handleEndTextVoiceActions(action.deleteRecord)
            is MessageComposerAction.InitializeVoiceRecorder            -> handleInitializeVoiceRecorder(action.attachmentData)
            is MessageComposerAction.OnEntersBackground                 -> handleEntersBackground(action.composerText)
            is MessageComposerAction.VoiceWaveformTouchedUp             -> handleVoiceWaveformTouchedUp(action)
            is MessageComposerAction.VoiceWaveformMovedTo               -> handleVoiceWaveformMovedTo(action)
            is MessageComposerAction.AudioSeekBarMovedTo                -> handleAudioSeekBarMovedTo(action)
            is MessageComposerAction.SlashCommandConfirmed              -> handleSlashCommandConfirmed(action)
        }
    }

    /
    private fun handleOnVoiceTextRecordingUiStateChanged(action: MessageComposerAction.OnVoiceTextRecordingUiStateChanged) = setState {
        copy(
                isTextVoiceVisible = true, voiceTextRecordingUiState = action.uiState
        )
    }

    
    private fun handleCloseTextVoiceCloseActions() {
        setState {
            copy(
                    voiceTextRecordingUiState = MessageTranslateView.TranslateRecordingUiState.Idle,
                    isTextVoiceVisible = false,
                    originText = null,
                    translate = null,
                    audioType = null,
            )
        }
    }

    private var speechService: SpeechService? = null

    
    private fun initModel() = withState {
        val sourcePath = if (!it.isChinese) "model-en-us" else "model-zh-cn"
        StorageService.unpack(context, sourcePath, "model", { model: Model ->
            getOriginText(model)
        }, { exception: IOException -> Timber.e("Failed to unpack the model " + exception.message) })
    }

    private fun getOriginText(model: Model) {

        
        speechService?.stop()
        speechService = null
        val rec = Recognizer(model, 16000.0f)
        speechService = SpeechService(rec, 16000.0f)
        speechService!!.startListening(object : RecognitionListener {

            
            override fun onPartialResult(hypothesis: String) {
                val text: String = tryOrNull {
                    val obj = JSONObject(hypothesis)
                    obj.getString("partial")
                } ?: ""
                setState {
                    copy(
                            partialText = text,
                    )
                }
            }

            
            override fun onResult(hypothesis: String) {

                val text: String = tryOrNull {
                    val obj = JSONObject(hypothesis)
                    obj.getString("text")
                } ?: ""

                setState {
                    copy(
                            partialText = "",
                            originText = (originText ?: "") + text,
                    )
                }
            }

            
            override fun onFinalResult(hypothesis: String) {
                val text: String = tryOrNull {
                    val obj = JSONObject(hypothesis)
                    obj.getString("text")
                } ?: ""
                setState {
                    copy(
                            partialText = "",
                            originText = (originText ?: "") + text,
                    )
                }
            }

            override fun onError(exception: Exception) {
                Timber.i("-------MessageComposerViewModel----------onError--------------$exception------------")
            }

            override fun onTimeout() {
                Timber.i("-------MessageComposerViewModel----------onTimeout--------------------------")
            }
        })
        handleOnVoiceTextRecordingUiStateChanged(
                MessageComposerAction.OnVoiceTextRecordingUiStateChanged(MessageTranslateView.TranslateRecordingUiState.Recording(clock.epochMillis()))
        )
    }

    
    private fun handleTextStartRecordingVoiceMessage() {
        try {
            initModel()
        } catch (failure: Throwable) {
            _viewEvents.post(MessageComposerViewEvents.VoicePlaybackOrRecordingFailure(failure))
        }
    }

    
    private fun handleTextRecordingEnded() {
        speechService?.stop()
        speechService = null
    }

    
    override fun onCleared() {
        speechService?.stop()
        speechService = null
        super.onCleared()
    }

    
    private fun handleEndRecordingTextVoiceMessage(action: MessageComposerAction.EndRecordingTextVoiceMessage) = withState {
        setState {
            copy(
                    originText = action.content,
                    partialText = "",
                    voiceTextRecordingUiState = MessageTranslateView.TranslateRecordingUiState.Sending
            )
        }
        room.sendCusMedia(
                BuildConfig.APPLICATION_ID,
                action.content,
                it.fromLan,
                it.toLan
        )
        setState {
            
            copy(
                    voiceTextRecordingUiState = MessageTranslateView.TranslateRecordingUiState.Idle,
                    isTextVoiceVisible = false,
                    originText = null,
                    partialText = null,
                    translate = null,
                    audioType = null,
            )
        }
    }

    private fun handleOnLangChange(action: MessageComposerAction.OnLangChange) {
        setState {
            copy(
                    isChinese = action.isChinese,
                    fromLan = action.fromLan,
                    toLan = action.toLan,
            )
        }
    }

    
    private fun handleEndTextVoiceActions(deleteRecord: Boolean) {
        audioMessageHelper.clearTracker()
        audioMessageHelper.stopAllVoiceActions(deleteRecord)
        setState {
            copy(
                    originText = null,
                    translate = null,
                    audioType = null,
            )
        }
    }

    private fun handleOnVoiceRecordingUiStateChanged(action: MessageComposerAction.OnVoiceRecordingUiStateChanged) = setState {
        copy(voiceRecordingUiState = action.uiState)
    }

    private fun handleOnTextChanged(action: MessageComposerAction.OnTextChanged) {
        setState {
            
            currentComposerText = action.text
            this
        }
        updateIsSendButtonVisibility(true)
    }

    private fun subscribeToStateInternal() {
        onEach(MessageComposerViewState::sendMode, MessageComposerViewState::canSendMessage, MessageComposerViewState::isVoiceRecording) { _, _, _ ->
            updateIsSendButtonVisibility(false)
        }
    }

    private fun updateIsSendButtonVisibility(triggerAnimation: Boolean) = setState {
        val isSendButtonVisible = isComposerVisible && (sendMode !is SendMode.Regular || currentComposerText.isNotBlank())
        if (this.isSendButtonVisible != isSendButtonVisible && triggerAnimation) {
            _viewEvents.post(MessageComposerViewEvents.AnimateSendButtonVisibility(isSendButtonVisible))
        }
        copy(isSendButtonVisible = isSendButtonVisible)
    }

    private fun handleEnterRegularMode(action: MessageComposerAction.EnterRegularMode) = setState {
        copy(sendMode = SendMode.Regular(action.text, action.fromSharing))
    }

    private fun handleEnterEditMode(action: MessageComposerAction.EnterEditMode) {
        room.getTimelineEvent(action.eventId)?.let { timelineEvent ->
            setState { copy(sendMode = SendMode.Edit(timelineEvent, timelineEvent.getTextEditableContent())) }
        }
    }

    private fun observePowerLevelAndEncryption() {
        combine(
                PowerLevelsFlowFactory(room).createFlow(), room.flow().liveRoomSummary().unwrap()
        ) { pl, sum ->
            val canSendMessage = PowerLevelsHelper(pl).isUserAllowedToSend(session.myUserId, false, EventType.MESSAGE)
            val izServerNoticeId = izServerNoticeId(room.roomId, session.myUserId)
            if (canSendMessage && !izServerNoticeId) {
                val isE2E = sum.isEncrypted
                if (isE2E) {
                    val roomEncryptionAlgorithm = sum.roomEncryptionAlgorithm
                    if (roomEncryptionAlgorithm is RoomEncryptionAlgorithm.UnsupportedAlgorithm) {
                        CanSendStatus.UnSupportedE2eAlgorithm(roomEncryptionAlgorithm.name)
                    } else {
                        CanSendStatus.Allowed
                    }
                } else {
                    CanSendStatus.Allowed
                }
            } else {
                CanSendStatus.NoPermission(izServerNoticeId)
            }
        }.setOnEach {
            copy(canSendMessage = it)
        }
    }

    private fun handleEnterQuoteMode(action: MessageComposerAction.EnterQuoteMode) {
        room.getTimelineEvent(action.eventId)?.let { timelineEvent ->
            setState { copy(sendMode = SendMode.Quote(timelineEvent, action.text)) }
        }
    }

    private fun handleEnterReplyMode(action: MessageComposerAction.EnterReplyMode) {
        room.getTimelineEvent(action.eventId)?.let { timelineEvent ->
            setState { copy(sendMode = SendMode.Reply(timelineEvent, action.text)) }
        }
    }

    private fun handleSendMessage(action: MessageComposerAction.SendMessage) {
        withState { state ->
            Timber.i("state=$state,state.rootThreadEventId=${state.rootThreadEventId}")
            analyticsTracker.capture(state.toAnalyticsComposer()).also {
                setState { copy(startsThread = false) }
            }
            when (state.sendMode) {
                is SendMode.Regular -> {
                    val parsedCommand = commandParser.parseSlashCommand(
                            textMessage = action.text, isInThreadTimeline = state.isInThreadTimeline()
                    )
                    Timber.i("parsedCommand=$parsedCommand")
                    when (parsedCommand) {
                        is ParsedCommand.ErrorNotACommand                  -> {
                            
                            if (state.rootThreadEventId != null) {
                                room.replyInThread(
                                        rootThreadEventId = state.rootThreadEventId, replyInThreadText = action.text, autoMarkdown = action.autoMarkdown
                                )
                            } else {
                                room.sendTextMessage(action.text, autoMarkdown = action.autoMarkdown)
                            }

                            _viewEvents.post(MessageComposerViewEvents.MessageSent)
                            popDraft()
                        }
                        is ParsedCommand.ErrorSyntax                       -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandError(parsedCommand.command))
                        }
                        is ParsedCommand.ErrorEmptySlashCommand            -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandUnknown("/"))
                        }
                        is ParsedCommand.ErrorUnknownSlashCommand          -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandUnknown(parsedCommand.slashCommand))
                        }
                        is ParsedCommand.ErrorCommandNotSupportedInThreads -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandNotSupportedInThreads(parsedCommand.command))
                        }
                        is ParsedCommand.SendPlainText                     -> {
                            
                            if (state.rootThreadEventId != null) {
                                room.replyInThread(
                                        rootThreadEventId = state.rootThreadEventId, replyInThreadText = parsedCommand.message, autoMarkdown = false
                                )
                            } else {
                                room.sendTextMessage(parsedCommand.message, autoMarkdown = false)
                            }
                            _viewEvents.post(MessageComposerViewEvents.MessageSent)
                            popDraft()
                        }
                        is ParsedCommand.ChangeRoomName                    -> {
                            handleChangeRoomNameSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.Invite                            -> {
                            handleInviteSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.Invite3Pid                        -> {
                            handleInvite3pidSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.SetUserPowerLevel                 -> {
                            handleSetUserPowerLevel(parsedCommand)
                        }
                        is ParsedCommand.ClearScalarToken                  -> {
                            
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandNotImplemented)
                        }
                        is ParsedCommand.SetMarkdown                       -> {
                            vectorPreferences.setMarkdownEnabled(parsedCommand.enable)
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.BanUser                           -> {
                            handleBanSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.UnbanUser                         -> {
                            handleUnbanSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.IgnoreUser                        -> {
                            handleIgnoreSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.UnignoreUser                      -> {
                            handleUnignoreSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.RemoveUser                        -> {
                            handleRemoveSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.JoinRoom                          -> {
                            handleJoinToAnotherRoomSlashCommand(parsedCommand)
                            popDraft()
                        }
                        is ParsedCommand.PartRoom                          -> {
                            handlePartSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.SendEmote                         -> {
                            if (state.rootThreadEventId != null) {
                                room.replyInThread(
                                        rootThreadEventId = state.rootThreadEventId,
                                        replyInThreadText = parsedCommand.message,
                                        msgType = MessageType.MSGTYPE_EMOTE,
                                        autoMarkdown = action.autoMarkdown
                                )
                            } else {
                                room.sendTextMessage(parsedCommand.message, msgType = MessageType.MSGTYPE_EMOTE, autoMarkdown = action.autoMarkdown)
                            }
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.SendRainbow                       -> {
                            val message = parsedCommand.message.toString()
                            if (state.rootThreadEventId != null) {
                                room.replyInThread(
                                        rootThreadEventId = state.rootThreadEventId,
                                        replyInThreadText = parsedCommand.message,
                                        formattedText = rainbowGenerator.generate(message)
                                )
                            } else {
                                room.sendFormattedTextMessage(message, rainbowGenerator.generate(message))
                            }
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.SendRainbowEmote                  -> {
                            val message = parsedCommand.message.toString()
                            if (state.rootThreadEventId != null) {
                                room.replyInThread(
                                        rootThreadEventId = state.rootThreadEventId,
                                        replyInThreadText = parsedCommand.message,
                                        msgType = MessageType.MSGTYPE_EMOTE,
                                        formattedText = rainbowGenerator.generate(message)
                                )
                            } else {
                                room.sendFormattedTextMessage(message, rainbowGenerator.generate(message), MessageType.MSGTYPE_EMOTE)
                            }

                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.SendSpoiler                       -> {
                            val text = "[${stringProvider.getString(R.string.spoiler)}](${parsedCommand.message})"
                            val formattedText = "<span data-mx-spoiler>${parsedCommand.message}</span>"
                            if (state.rootThreadEventId != null) {
                                room.replyInThread(
                                        rootThreadEventId = state.rootThreadEventId, replyInThreadText = text, formattedText = formattedText
                                )
                            } else {
                                room.sendFormattedTextMessage(
                                        text, formattedText
                                )
                            }
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.SendShrug                         -> {
                            sendPrefixedMessage("¯\\_(ツ)_/¯", parsedCommand.message, state.rootThreadEventId)
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.SendLenny                         -> {
                            sendPrefixedMessage("( ͡° ͜ʖ ͡°)", parsedCommand.message, state.rootThreadEventId)
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.SendChatEffect                    -> {
                            sendChatEffect(parsedCommand)
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                        is ParsedCommand.ChangeTopic                       -> {
                            handleChangeTopicSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.ChangeDisplayName                 -> {
                            handleChangeDisplayNameSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.ChangeDisplayNameForRoom          -> {
                            handleChangeDisplayNameForRoomSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.ChangeRoomAvatar                  -> {
                            handleChangeRoomAvatarSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.ChangeAvatarForRoom               -> {
                            handleChangeAvatarForRoomSlashCommand(parsedCommand)
                        }
                        is ParsedCommand.ShowUser                          -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            handleWhoisSlashCommand(parsedCommand)
                            popDraft()
                        }
                        is ParsedCommand.DiscardSession                    -> {
                            if (room.isEncrypted()) {
                                session.cryptoService().discardOutboundSession(room.roomId)
                                _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                                popDraft()
                            } else {
                                _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                                _viewEvents.post(
                                        MessageComposerViewEvents.ShowMessage(stringProvider.getString(R.string.command_description_discard_session_not_handled))
                                )
                            }
                        }
                        is ParsedCommand.CreateSpace                       -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandLoading)
                            viewModelScope.launch(Dispatchers.IO) {
                                try {
                                    val params = CreateSpaceParams().apply {
                                        name = parsedCommand.name
                                        invitedUserIds.addAll(parsedCommand.invitees)
                                    }
                                    val spaceId = session.spaceService().createSpace(params)
                                    session.spaceService().getSpace(spaceId)?.addChildren(
                                            state.roomId, null, null, true
                                    )
                                    popDraft()
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                                } catch (failure: Throwable) {
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultError(failure))
                                }
                            }
                            Unit
                        }
                        is ParsedCommand.AddToSpace                        -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandLoading)
                            viewModelScope.launch(Dispatchers.IO) {
                                try {
                                    session.spaceService().getSpace(parsedCommand.spaceId)?.addChildren(
                                            room.roomId, null, null, false
                                    )
                                    popDraft()
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                                } catch (failure: Throwable) {
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultError(failure))
                                }
                            }
                            Unit
                        }
                        is ParsedCommand.JoinSpace                         -> {
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandLoading)
                            viewModelScope.launch(Dispatchers.IO) {
                                try {
                                    session.spaceService().joinSpace(parsedCommand.spaceIdOrAlias)
                                    popDraft()
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                                } catch (failure: Throwable) {
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultError(failure))
                                }
                            }
                            Unit
                        }
                        is ParsedCommand.LeaveRoom                         -> {
                            viewModelScope.launch(Dispatchers.IO) {
                                try {
                                    session.leaveRoom(parsedCommand.roomId)
                                    popDraft()
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                                } catch (failure: Throwable) {
                                    _viewEvents.post(MessageComposerViewEvents.SlashCommandResultError(failure))
                                }
                            }
                            Unit
                        }
                        is ParsedCommand.UpgradeRoom                       -> {
                            _viewEvents.post(
                                    MessageComposerViewEvents.ShowRoomUpgradeDialog(
                                            parsedCommand.newVersion, room.roomSummary()?.isPublic ?: false
                                    )
                            )
                            _viewEvents.post(MessageComposerViewEvents.SlashCommandResultOk(parsedCommand))
                            popDraft()
                        }
                    }
                }
                is SendMode.Edit    -> {
                    
                    val relationContent = state.sendMode.timelineEvent.getRelationContent()
                    val inReplyTo = if (state.rootThreadEventId != null) {
                        
                        if (relationContent?.shouldRenderInThread() == true) {
                            
                            relationContent.inReplyTo?.eventId
                        } else {
                            
                            null
                        }
                    } else {
                        
                        relationContent?.inReplyTo?.eventId
                    }

                    if (inReplyTo != null) {
                        
                        room.getTimelineEvent(inReplyTo)?.let {
                            room.editReply(state.sendMode.timelineEvent, it, action.text.toString())
                        }
                    } else {
                        val messageContent = state.sendMode.timelineEvent.getLastMessageContent()
                        val existingBody = messageContent?.body ?: ""
                        if (existingBody != action.text) {
                            room.editTextMessage(
                                    state.sendMode.timelineEvent, messageContent?.msgType ?: MessageType.MSGTYPE_TEXT, action.text, action.autoMarkdown
                            )
                        } else {
                            Timber.w("Same message content, do not send edition")
                        }
                    }
                    _viewEvents.post(MessageComposerViewEvents.MessageSent)
                    popDraft()
                }
                is SendMode.Quote   -> {
                    room.sendQuotedTextMessage(
                            quotedEvent = state.sendMode.timelineEvent,
                            text = action.text.toString(),
                            autoMarkdown = action.autoMarkdown,
                            rootThreadEventId = state.rootThreadEventId
                    )
                    _viewEvents.post(MessageComposerViewEvents.MessageSent)
                    popDraft()
                }
                is SendMode.Reply   -> {
                    val timelineEvent = state.sendMode.timelineEvent
                    val showInThread = state.sendMode.timelineEvent.root.isThread() && state.rootThreadEventId == null
                    
                    val rootThreadEventId = if (showInThread) timelineEvent.root.getRootThreadEventId() else null
                    state.rootThreadEventId?.let {
                        room.replyInThread(
                                rootThreadEventId = it,
                                replyInThreadText = action.text.toString(),
                                autoMarkdown = action.autoMarkdown,
                                eventReplied = timelineEvent
                        )
                    } ?: room.replyToMessage(
                            eventReplied = timelineEvent,
                            replyText = action.text.toString(),
                            autoMarkdown = action.autoMarkdown,
                            showInThread = showInThread,
                            rootThreadEventId = rootThreadEventId
                    )

                    _viewEvents.post(MessageComposerViewEvents.MessageSent)
                    popDraft()
                }
                is SendMode.Voice   -> {
                    
                }
            }
        }
    }

    private fun popDraft() = withState {
        if (it.sendMode is SendMode.Regular && it.sendMode.fromSharing) {
            
            loadDraftIfAny()
        } else {
            
            setState { copy(sendMode = SendMode.Regular("", false)) }
            viewModelScope.launch {
                room.deleteDraft()
            }
        }
    }

    private fun loadDraftIfAny() {
        val currentDraft = room.getDraft()
        setState {
            copy(
                    
                    sendMode = when (currentDraft) {
                        is UserDraft.Regular -> SendMode.Regular(currentDraft.content, false)
                        is UserDraft.Quote   -> {
                            room.getTimelineEvent(currentDraft.linkedEventId)?.let { timelineEvent ->
                                SendMode.Quote(timelineEvent, currentDraft.content)
                            }
                        }
                        is UserDraft.Reply   -> {
                            room.getTimelineEvent(currentDraft.linkedEventId)?.let { timelineEvent ->
                                SendMode.Reply(timelineEvent, currentDraft.content)
                            }
                        }
                        is UserDraft.Edit    -> {
                            room.getTimelineEvent(currentDraft.linkedEventId)?.let { timelineEvent ->
                                SendMode.Edit(timelineEvent, currentDraft.content)
                            }
                        }
                        is UserDraft.Voice   -> SendMode.Voice(currentDraft.content)
                        else                 -> null
                    } ?: SendMode.Regular("", fromSharing = false))
        }
    }

    private fun handleUserIsTyping(action: MessageComposerAction.UserIsTyping) {
        if (vectorPreferences.sendTypingNotifs()) {
            if (action.isTyping) {
                room.userIsTyping()
            } else {
                room.userStopsTyping()
            }
        }
    }

    private fun sendChatEffect(sendChatEffect: ParsedCommand.SendChatEffect) {
        
        if (sendChatEffect.message.isBlank()) {
            val defaultMessage = stringProvider.getString(
                    when (sendChatEffect.chatEffect) {
                        ChatEffect.CONFETTI -> R.string.default_message_emote_confetti
                        ChatEffect.SNOWFALL -> R.string.default_message_emote_snow
                    }
            )
            room.sendTextMessage(defaultMessage, MessageType.MSGTYPE_EMOTE)
        } else {
            room.sendTextMessage(sendChatEffect.message, sendChatEffect.chatEffect.toMessageType())
        }
    }

    private fun handleJoinToAnotherRoomSlashCommand(command: ParsedCommand.JoinRoom) {
        viewModelScope.launch {
            try {
                session.joinRoom(command.roomAlias, command.reason, emptyList())
            } catch (failure: Throwable) {
                _viewEvents.post(MessageComposerViewEvents.SlashCommandResultError(failure))
                return@launch
            }
            session.getRoomSummary(command.roomAlias)?.also { analyticsTracker.capture(it.toAnalyticsJoinedRoom()) }?.roomId?.let {
                _viewEvents.post(MessageComposerViewEvents.JoinRoomCommandSuccess(it))
            }
        }
    }

    private fun legacyRiotQuoteText(quotedText: String?, myText: String): String {
        val messageParagraphs = quotedText?.split("\n\n".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        return buildString {
            if (messageParagraphs != null) {
                for (i in messageParagraphs.indices) {
                    if (messageParagraphs[i].isNotBlank()) {
                        append("> ")
                        append(messageParagraphs[i])
                    }

                    if (i != messageParagraphs.lastIndex) {
                        append("\n\n")
                    }
                }
            }
            append("\n\n")
            append(myText)
        }
    }

    private fun handleChangeTopicSlashCommand(changeTopic: ParsedCommand.ChangeTopic) {
        launchSlashCommandFlowSuspendable(changeTopic) {
            room.updateTopic(changeTopic.topic)
        }
    }

    private fun handleInviteSlashCommand(invite: ParsedCommand.Invite) {
        launchSlashCommandFlowSuspendable(invite) {
            room.invite(invite.userId, invite.reason)
        }
    }

    private fun handleInvite3pidSlashCommand(invite: ParsedCommand.Invite3Pid) {
        launchSlashCommandFlowSuspendable(invite) {
            room.invite3pid(invite.threePid)
        }
    }

    private fun handleSetUserPowerLevel(setUserPowerLevel: ParsedCommand.SetUserPowerLevel) {
        val newPowerLevelsContent = room.getStateEvent(EventType.STATE_ROOM_POWER_LEVELS)?.content?.toModel<PowerLevelsContent>()?.setUserPowerLevel(
                setUserPowerLevel.userId, setUserPowerLevel.powerLevel
        )?.toContent() ?: return

        launchSlashCommandFlowSuspendable(setUserPowerLevel) {
            room.sendStateEvent(EventType.STATE_ROOM_POWER_LEVELS, stateKey = "", newPowerLevelsContent)
        }
    }

    private fun handleChangeDisplayNameSlashCommand(changeDisplayName: ParsedCommand.ChangeDisplayName) {
        launchSlashCommandFlowSuspendable(changeDisplayName) {
            session.setDisplayName(session.myUserId, changeDisplayName.displayName)
        }
    }

    private fun handlePartSlashCommand(command: ParsedCommand.PartRoom) {
        launchSlashCommandFlowSuspendable(command) {
            if (command.roomAlias == null) {
                
                room
            } else {
                session.getRoomSummary(roomIdOrAlias = command.roomAlias)?.roomId?.let { session.getRoom(it) }
            }?.let {
                session.leaveRoom(it.roomId)
            }
        }
    }

    private fun handleRemoveSlashCommand(removeUser: ParsedCommand.RemoveUser) {
        launchSlashCommandFlowSuspendable(removeUser) {
            room.remove(removeUser.userId, removeUser.reason)
        }
    }

    private fun handleBanSlashCommand(ban: ParsedCommand.BanUser) {
        launchSlashCommandFlowSuspendable(ban) {
            room.ban(ban.userId, ban.reason)
        }
    }

    private fun handleUnbanSlashCommand(unban: ParsedCommand.UnbanUser) {
        launchSlashCommandFlowSuspendable(unban) {
            room.unban(unban.userId, unban.reason)
        }
    }

    private fun handleChangeRoomNameSlashCommand(changeRoomName: ParsedCommand.ChangeRoomName) {
        launchSlashCommandFlowSuspendable(changeRoomName) {
            room.updateName(changeRoomName.name)
        }
    }

    private fun getMyRoomMemberContent(): RoomMemberContent? {
        return room.getStateEvent(EventType.STATE_ROOM_MEMBER, QueryStringValue.Equals(session.myUserId))?.content?.toModel<RoomMemberContent>()
    }

    private fun handleChangeDisplayNameForRoomSlashCommand(changeDisplayName: ParsedCommand.ChangeDisplayNameForRoom) {
        launchSlashCommandFlowSuspendable(changeDisplayName) {
            getMyRoomMemberContent()?.copy(displayName = changeDisplayName.displayName)?.toContent()?.let {
                room.sendStateEvent(EventType.STATE_ROOM_MEMBER, session.myUserId, it)
            }
        }
    }

    private fun handleChangeRoomAvatarSlashCommand(changeAvatar: ParsedCommand.ChangeRoomAvatar) {
        launchSlashCommandFlowSuspendable(changeAvatar) {
            room.sendStateEvent(EventType.STATE_ROOM_AVATAR, stateKey = "", RoomAvatarContent(changeAvatar.url).toContent())
        }
    }

    private fun handleChangeAvatarForRoomSlashCommand(changeAvatar: ParsedCommand.ChangeAvatarForRoom) {
        launchSlashCommandFlowSuspendable(changeAvatar) {
            getMyRoomMemberContent()?.copy(avatarUrl = changeAvatar.url)?.toContent()?.let {
                room.sendStateEvent(EventType.STATE_ROOM_MEMBER, session.myUserId, it)
            }
        }
    }

    private fun handleIgnoreSlashCommand(ignore: ParsedCommand.IgnoreUser) {
        launchSlashCommandFlowSuspendable(ignore) {
            session.ignoreUserIds(listOf(ignore.userId))
        }
    }

    private fun handleUnignoreSlashCommand(unignore: ParsedCommand.UnignoreUser) {
        _viewEvents.post(MessageComposerViewEvents.SlashCommandConfirmationRequest(unignore))
    }

    private fun handleSlashCommandConfirmed(action: MessageComposerAction.SlashCommandConfirmed) {
        when (action.parsedCommand) {
            is ParsedCommand.UnignoreUser -> handleUnignoreSlashCommandConfirmed(action.parsedCommand)
            else                          -> TODO("Not handled yet")
        }
    }

    private fun handleUnignoreSlashCommandConfirmed(unignore: ParsedCommand.UnignoreUser) {
        launchSlashCommandFlowSuspendable(unignore) {
            session.unIgnoreUserIds(listOf(unignore.userId))
        }
    }

    private fun handleWhoisSlashCommand(whois: ParsedCommand.ShowUser) {
        _viewEvents.post(MessageComposerViewEvents.OpenRoomMemberProfile(whois.userId))
    }

    private fun sendPrefixedMessage(prefix: String, message: CharSequence, rootThreadEventId: String?) {
        val sequence = buildString {
            append(prefix)
            if (message.isNotEmpty()) {
                append(" ")
                append(message)
            }
        }
        rootThreadEventId?.let {
            room.replyInThread(it, sequence)
        } ?: room.sendTextMessage(sequence)
    }

    
    private fun handleSaveTextDraft(draft: String) = withState {
        session.coroutineScope.launch {
            when {
                it.sendMode is SendMode.Regular && !it.sendMode.fromSharing -> {
                    setState { copy(sendMode = it.sendMode.copy(text = draft)) }
                    room.saveDraft(UserDraft.Regular(draft))
                }
                it.sendMode is SendMode.Reply                               -> {
                    setState { copy(sendMode = it.sendMode.copy(text = draft)) }
                    room.saveDraft(UserDraft.Reply(it.sendMode.timelineEvent.root.eventId!!, draft))
                }
                it.sendMode is SendMode.Quote                               -> {
                    setState { copy(sendMode = it.sendMode.copy(text = draft)) }
                    room.saveDraft(UserDraft.Quote(it.sendMode.timelineEvent.root.eventId!!, draft))
                }
                it.sendMode is SendMode.Edit                                -> {
                    setState { copy(sendMode = it.sendMode.copy(text = draft)) }
                    room.saveDraft(UserDraft.Edit(it.sendMode.timelineEvent.root.eventId!!, draft))
                }
            }
        }
    }

    private fun handleStartRecordingVoiceMessage() {
        try {
            audioMessageHelper.startRecording(room.roomId)
        } catch (failure: Throwable) {
            _viewEvents.post(MessageComposerViewEvents.VoicePlaybackOrRecordingFailure(failure))
        }
    }

    
    private fun handleEndRecordingVoiceMessage(isCancelled: Boolean, rootThreadEventId: String? = null) {
        audioMessageHelper.stopPlayback()
        if (isCancelled) {
            audioMessageHelper.deleteRecording()
        } else {
            audioMessageHelper.stopRecording(convertForSending = true)?.let { audioType ->
                

                if (audioType.duration > 1000) {
                    room.sendMedia(
                            attachment = audioType.toContentAttachmentData(isVoiceMessage = true),
                            compressBeforeSending = false,
                            roomIds = emptySet(),
                            rootThreadEventId = rootThreadEventId
                    )
                } else {
                    audioMessageHelper.deleteRecording()
                }
            }
        }
        handleEnterRegularMode(MessageComposerAction.EnterRegularMode(text = "", fromSharing = false))
    }

    private fun handlePlayOrPauseVoicePlayback(action: MessageComposerAction.PlayOrPauseVoicePlayback) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                
                val audioFile = session.fileService().downloadFile(action.messageAudioContent)
                
                val convertedFile = voicePlayerHelper.convertFile(audioFile) ?: audioFile
                
                audioMessageHelper.startOrPausePlayback(action.eventId, convertedFile)
            } catch (failure: Throwable) {
                _viewEvents.post(MessageComposerViewEvents.VoicePlaybackOrRecordingFailure(failure))
            }
        }
    }

    private fun handlePlayOrPauseRecordingPlayback() {
        audioMessageHelper.startOrPauseRecordingPlayback()
    }

    private fun handleEndAllVoiceActions(deleteRecord: Boolean) {
        audioMessageHelper.clearTracker()
        audioMessageHelper.stopAllVoiceActions(deleteRecord)
    }

    private fun handleInitializeVoiceRecorder(attachmentData: ContentAttachmentData) {
        audioMessageHelper.initializeRecorder(attachmentData)
        setState { copy(voiceRecordingUiState = VoiceMessageRecorderView.RecordingUiState.Draft) }
    }

    private fun handlePauseRecordingVoiceMessage() {
        audioMessageHelper.pauseRecording()
    }

    private fun handleVoiceWaveformTouchedUp(action: MessageComposerAction.VoiceWaveformTouchedUp) {
        audioMessageHelper.movePlaybackTo(action.eventId, action.percentage, action.duration)
    }

    private fun handleVoiceWaveformMovedTo(action: MessageComposerAction.VoiceWaveformMovedTo) {
        audioMessageHelper.movePlaybackTo(action.eventId, action.percentage, action.duration)
    }

    private fun handleAudioSeekBarMovedTo(action: MessageComposerAction.AudioSeekBarMovedTo) {
        audioMessageHelper.movePlaybackTo(action.eventId, action.percentage, action.duration)
    }

    private fun handleEntersBackground(composerText: String) {
        
        val playingAudioContent = audioMessageHelper.stopAllVoiceActions(deleteRecord = false)

        val isVoiceRecording = com.airbnb.mvrx.withState(this) { it.isVoiceRecording }
        if (isVoiceRecording) {
            viewModelScope.launch {
                playingAudioContent?.toContentAttachmentData()?.let { voiceDraft ->
                    val content = voiceDraft.toJsonString()
                    room.saveDraft(UserDraft.Voice(content))
                    setState { copy(sendMode = SendMode.Voice(content)) }
                }
            }
        } else {
            handleSaveTextDraft(draft = composerText)
        }
    }

    private fun launchSlashCommandFlowSuspendable(parsedCommand: ParsedCommand, block: suspend () -> Unit) {
        _viewEvents.post(MessageComposerViewEvents.SlashCommandLoading)
        viewModelScope.launch {
            val event = try {
                block()
                popDraft()
                MessageComposerViewEvents.SlashCommandResultOk(parsedCommand)
            } catch (failure: Throwable) {
                MessageComposerViewEvents.SlashCommandResultError(failure)
            }
            _viewEvents.post(event)
        }
    }

    @AssistedFactory interface Factory : MavericksAssistedViewModelFactory<MessageComposerViewModel, MessageComposerViewState> {
        override fun create(initialState: MessageComposerViewState): MessageComposerViewModel
    }

    companion object : MavericksViewModelFactory<MessageComposerViewModel, MessageComposerViewState> by hiltMavericksViewModelFactory()
}
