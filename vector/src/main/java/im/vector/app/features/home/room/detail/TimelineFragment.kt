

package im.vector.app.features.home.room.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.core.text.buildSpannedString
import androidx.core.text.toSpannable
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.OnModelBuildFinishedListener
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.vanniktech.emoji.EmojiPopup
import common.app.ActivityRouter
import common.app.base.them.Eyes
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.WalletHandler
import im.vector.app.core.animations.play
import im.vector.app.core.dialogs.ConfirmationDialogBuilder
import im.vector.app.core.dialogs.GalleryOrCameraDialogHelper
import im.vector.app.core.epoxy.LayoutManagerStateRestorer
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.*
import im.vector.app.core.glide.GlideApp
import im.vector.app.core.glide.GlideRequests
import im.vector.app.core.hardware.vibrate
import im.vector.app.core.intent.getFilenameFromUri
import im.vector.app.core.intent.getMimeTypeFromUri
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.platform.lifecycleAwareLazy
import im.vector.app.core.platform.showOptimizedSnackbar
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.UserPreferencesProvider
import im.vector.app.core.time.Clock
import im.vector.app.core.ui.views.*
import im.vector.app.core.utils.*
import im.vector.app.databinding.DialogReportContentBinding
import im.vector.app.databinding.FragmentTimelineBinding
import im.vector.app.features.analytics.extensions.toAnalyticsInteraction
import im.vector.app.features.analytics.plan.Interaction
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.attachments.AttachmentTypeSelectorView
import im.vector.app.features.attachments.AttachmentsHelper
import im.vector.app.features.attachments.ContactAttachment
import im.vector.app.features.attachments.preview.AttachmentsPreviewActivity
import im.vector.app.features.attachments.preview.AttachmentsPreviewArgs
import im.vector.app.features.attachments.toGroupedContentAttachmentData
import im.vector.app.features.call.SharedKnownCallsViewModel
import im.vector.app.features.call.VectorCallActivity
import im.vector.app.features.call.conference.ConferenceEvent
import im.vector.app.features.call.conference.ConferenceEventEmitter
import im.vector.app.features.call.conference.ConferenceEventObserver
import im.vector.app.features.call.conference.JitsiCallViewModel
import im.vector.app.features.call.webrtc.WebRtcCallManager
import im.vector.app.features.command.Command
import im.vector.app.features.command.ParsedCommand
import im.vector.app.features.createdirect.DirectRoomHelper
import im.vector.app.features.createdirect.cluster.ClusterActivity
import im.vector.app.features.createdirect.migrate.MigrateGroupActivity
import im.vector.app.features.crypto.keysbackup.restore.KeysBackupRestoreActivity
import im.vector.app.features.crypto.verification.VerificationBottomSheet
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.detail.arguments.TimelineArgs
import im.vector.app.features.home.room.detail.composer.*
import im.vector.app.features.home.room.detail.composer.voice.VoiceMessageRecorderView
import im.vector.app.features.home.room.detail.composer.voice.VoiceMessageRecorderView.RecordingUiState
import im.vector.app.features.home.room.detail.readreceipts.DisplayReadReceiptsBottomSheet
import im.vector.app.features.home.room.detail.timeline.TimelineEventController
import im.vector.app.features.home.room.detail.timeline.action.EventSharedAction
import im.vector.app.features.home.room.detail.timeline.action.MessageActionsBottomSheet
import im.vector.app.features.home.room.detail.timeline.action.MessageSharedActionViewModel
import im.vector.app.features.home.room.detail.timeline.edithistory.ViewEditHistoryBottomSheet
import im.vector.app.features.home.room.detail.timeline.helper.AudioMessagePlaybackTracker
import im.vector.app.features.home.room.detail.timeline.helper.MatrixItemColorProvider
import im.vector.app.features.home.room.detail.timeline.image.buildImageContentRendererData
import im.vector.app.features.home.room.detail.timeline.item.*
import im.vector.app.features.home.room.detail.timeline.reactions.ViewReactionsBottomSheet
import im.vector.app.features.home.room.detail.timeline.url.PreviewUrlRetriever
import im.vector.app.features.home.room.detail.upgrade.MigrateRoomBottomSheet
import im.vector.app.features.home.room.detail.views.RoomDetailLazyLoadedViews
import im.vector.app.features.home.room.detail.widget.RoomWidgetsBottomSheet
import im.vector.app.features.home.room.list.UnreadCounterBadgeView
import im.vector.app.features.home.room.threads.ThreadsManager
import im.vector.app.features.home.room.threads.arguments.ThreadTimelineArgs
import im.vector.app.features.html.EventHtmlRenderer
import im.vector.app.features.html.PillImageSpan
import im.vector.app.features.html.PillsPostProcessor
import im.vector.app.features.invite.VectorInviteView
import im.vector.app.features.location.LocationSharingMode
import im.vector.app.features.location.toLocationData
import im.vector.app.features.media.AttachmentData
import im.vector.app.features.media.ImageContentRenderer
import im.vector.app.features.media.VideoContentRenderer
import im.vector.app.features.notifications.NotificationDrawerManager
import im.vector.app.features.notifications.NotificationUtils
import im.vector.app.features.permalink.NavigationInterceptor
import im.vector.app.features.permalink.PermalinkHandler
import im.vector.app.features.poll.PollMode
import im.vector.app.features.reactions.EmojiReactionPickerActivity
import im.vector.app.features.roomprofile.RoomProfileActivity
import im.vector.app.features.session.coroutineScope
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.settings.VectorSettingsActivity
import im.vector.app.features.share.SharedData
import im.vector.app.features.spaces.share.ShareSpaceBottomSheet
import im.vector.app.features.themes.ThemeUtils
import im.vector.app.features.voice.VoiceFailure
import im.vector.app.features.widgets.WidgetActivity
import im.vector.app.features.widgets.WidgetArgs
import im.vector.app.features.widgets.WidgetKind
import im.vector.app.features.widgets.permissions.RoomWidgetPermissionBottomSheet
import im.vector.app.provide.ChatStatusProvide
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import im.wallet.router.wallet.IWalletPay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.fenfei.anim.create.CreateAnimFragmentDialog
import me.fenfei.anim.create.en.CreateAnimFragmentOther
import me.fenfei.anim.enter.EnterAnimFragmentDialog
import me.fenfei.anim.enter.en.EnterAnimFragmentOther
import org.billcarsonfr.jsonviewer.JSonViewerDialog
import org.commonmark.parser.Parser
import org.matrix.android.sdk.api.failure.Failure.ServerError
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.izServerNoticeId
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.events.model.content.WithHeldCode
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.message.*
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.widgets.model.Widget
import org.matrix.android.sdk.api.session.widgets.model.WidgetType
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.MimeTypes
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.internal.network.GlobalErrorInteractHandler
import reactivecircus.flowbinding.android.view.focusChanges
import reactivecircus.flowbinding.android.widget.textChanges
import timber.log.Timber
import java.net.URL
import java.util.*
import javax.inject.Inject

const val TIMELINE_AWARD_TYPE_POS = 0
const val TIMELINE_AWARD_TYPE_DVM = 1
const val TIMELINE_AWARD_TYPE_DAO = 2
const val TIMELINE_AWARD_TYPE_AIRDROP = 3

class TimelineFragment @Inject constructor(
        private val directRoomHelper: DirectRoomHelper,
        private val session: Session,
        private val avatarRenderer: AvatarRenderer,
        private val timelineEventController: TimelineEventController,
        private val autoCompleterFactory: AutoCompleter.Factory,
        private val permalinkHandler: PermalinkHandler,
        private val notificationDrawerManager: NotificationDrawerManager,
        private val eventHtmlRenderer: EventHtmlRenderer,
        private val vectorPreferences: VectorPreferences,
        private val threadsManager: ThreadsManager,
        private val colorProvider: ColorProvider,
        private val dimensionConverter: DimensionConverter,
        private val userPreferencesProvider: UserPreferencesProvider,
        private val notificationUtils: NotificationUtils,
        private val matrixItemColorProvider: MatrixItemColorProvider,
        private val imageContentRenderer: ImageContentRenderer,
        private val roomDetailPendingActionStore: RoomDetailPendingActionStore,
        private val pillsPostProcessorFactory: PillsPostProcessor.Factory,
        private val callManager: WebRtcCallManager,
        private val audioMessagePlaybackTracker: AudioMessagePlaybackTracker,
        private val clock: Clock) :
        VectorBaseFragment<FragmentTimelineBinding>(),
        TimelineEventController.Callback,
        VectorInviteView.Callback,
        AttachmentTypeSelectorView.Callback,
        AttachmentsHelper.Callback,
        GalleryOrCameraDialogHelper.Listener,
        CurrentCallsView.Callback {

    companion object {

        
        private fun sanitizeDisplayName(displayName: String): String {
            if (displayName.endsWith(ircPattern)) {
                return displayName.substring(0, displayName.length - ircPattern.length)
            }

            return displayName
        }

        const val MAX_TYPING_MESSAGE_USERS_COUNT = 4
        private const val ircPattern = " (IRC)"
    }

    private val galleryOrCameraDialogHelper = GalleryOrCameraDialogHelper(this, colorProvider)

    private val timelineArgs: TimelineArgs by args()
    private val glideRequests by lazy {
        GlideApp.with(this)
    }
    private val pillsPostProcessor by lazy {
        pillsPostProcessorFactory.create(timelineArgs.roomId)
    }

    private val autoCompleter: AutoCompleter by lazy {
        autoCompleterFactory.create(timelineArgs.roomId, isThreadTimeLine())
    }

    private val timelineViewModel: TimelineViewModel by fragmentViewModel()
    private val messageComposerViewModel: MessageComposerViewModel by fragmentViewModel()
    private val debouncer = Debouncer(createUIHandler())

    private lateinit var scrollOnNewMessageCallback: ScrollOnNewMessageCallback
    private lateinit var scrollOnHighlightedEventCallback: ScrollOnHighlightedEventCallback

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTimelineBinding {
        return FragmentTimelineBinding.inflate(inflater, container, false)
    }

    override fun getMenuRes() = R.menu.menu_timeline

    private lateinit var sharedActionViewModel: MessageSharedActionViewModel
    private lateinit var sharedActivityActionViewModel: RoomDetailSharedActionViewModel

    private lateinit var knownCallsViewModel: SharedKnownCallsViewModel

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var jumpToBottomViewVisibilityManager: JumpToBottomViewVisibilityManager
    private var modelBuildListener: OnModelBuildFinishedListener? = null

    private lateinit var attachmentsHelper: AttachmentsHelper
    private lateinit var keyboardStateUtils: KeyboardStateUtils
    private lateinit var callActionsHandler: StartCallActionsHandler

    private lateinit var attachmentTypeSelector: AttachmentTypeSelectorView

    private var lockSendButton = false
    private val currentCallsViewPresenter = CurrentCallsViewPresenter()

    private val lazyLoadedViews = RoomDetailLazyLoadedViews()
    private val emojiPopup: EmojiPopup by lifecycleAwareLazy {
        createEmojiPopup()
    }

    
    private lateinit var searchResultListener: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.Room
        setFragmentResultListener(MigrateRoomBottomSheet.REQUEST_KEY) { _, bundle ->
            bundle.getString(MigrateRoomBottomSheet.BUNDLE_KEY_REPLACEMENT_ROOM)?.let { replacementRoomId ->
                timelineViewModel.handle(RoomDetailAction.RoomUpgradeSuccess(replacementRoomId))
            }
        }

        AndroidBug5497Workaround.assistActivity(vectorBaseActivity)
    }

    private lateinit var errorInteract: GlobalErrorInteractHandler
    private val createLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val joinLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycle.addObserver(ConferenceEventObserver(vectorBaseActivity, this::onBroadcastJitsiEvent))
        super.onViewCreated(view, savedInstanceState)

        
        Eyes.setTranslucent(vectorBaseActivity)
        views.timeLineStatusBar.updateLayoutParams<ViewGroup.LayoutParams> {
            height = Eyes.getStatusBarHeight(vectorBaseActivity)
        }
        views.timeLineStatusBar.setBackgroundColor(colorProvider.getColor(R.color.transparent))

        
        ThemeUtils.getAttribute(vectorBaseActivity, android.R.attr.actionBarSize)?.data?.let {
            val actionBarHeight: Int = TypedValue.complexToDimensionPixelSize(it, resources.displayMetrics)
            views.contentLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = actionBarHeight + Eyes.getStatusBarHeight(vectorBaseActivity)
            }
        }

        sharedActionViewModel = activityViewModelProvider.get(MessageSharedActionViewModel::class.java)
        sharedActivityActionViewModel = activityViewModelProvider.get(RoomDetailSharedActionViewModel::class.java)
        knownCallsViewModel = activityViewModelProvider.get(SharedKnownCallsViewModel::class.java)
        attachmentsHelper = AttachmentsHelper(requireContext(), this).register()
        callActionsHandler = StartCallActionsHandler(
                roomId = timelineArgs.roomId,
                fragment = this,
                vectorPreferences = vectorPreferences,
                timelineViewModel = timelineViewModel,
                callManager = callManager,
                startCallActivityResultLauncher = startCallActivityResultLauncher,
                showDialogWithMessage = ::showDialogWithMessage,
                onTapToReturnToCall = ::onTapToReturnToCall
        )

        createLiveData.observe(this) {
            if (isEnglish()) {
                CreateAnimFragmentOther()
            } else {
                CreateAnimFragmentDialog()
            }.show(childFragmentManager, javaClass.simpleName)
        }

        joinLiveData.observe(vectorBaseActivity) {
            if (izShowAnim) {
                return@observe
            }
            izShowAnim = true
            if (isEnglish()) {
                EnterAnimFragmentOther().apply {
                    listener = {
                        izShowAnim = false
                        session.getRoom(timelineArgs.roomId)?.setRoomSummaryUpgradeAnimShow(false)
                    }
                }
            } else {
                EnterAnimFragmentDialog().apply {
                    listener = {
                        izShowAnim = false
                        session.getRoom(timelineArgs.roomId)?.setRoomSummaryUpgradeAnimShow(false)
                    }
                }
            }.show(childFragmentManager, javaClass.simpleName)
        }

        if (timelineArgs.izCreate) {
            refreshGroupInfo()
            createLiveData.postValue(true)
        }

        keyboardStateUtils = KeyboardStateUtils(requireActivity())
        lazyLoadedViews.bind(views)
        setupToolbar(views.roomToolbar).allowBack()
        setupRecyclerView()
        setupComposer()
        setupNotificationView()
        setupJumpToReadMarkerView()
        setupActiveCallView()
        setupJumpToBottomView()
        setupEmojiButton()
        setupRemoveJitsiWidgetView()
        setupVoiceMessageView()
        setupVoiceTextMessageView()
        setupLiveLocationIndicator()
        searchResultListener = setUpSearchResultListener()
        

        
        views.includeRoomToolbar.roomToolbarContentView.debouncedClicks {
            val izServerNotice = izServerNoticeId(timelineArgs.roomId, session.myUserId)
            if (izServerNotice) {
                return@debouncedClicks
            }
            navigator.openRoomProfile(requireActivity(), timelineArgs.roomId)
        }

        sharedActionViewModel.stream().onEach {
            handleActions(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        knownCallsViewModel.liveKnownCalls.observe(viewLifecycleOwner) {
            currentCallsViewPresenter.updateCall(callManager.getCurrentCall(), it)
            invalidateOptionsMenu()
        }

        timelineViewModel.onEach(RoomDetailViewState::canShowJumpToReadMarker, RoomDetailViewState::unreadState) { _, _ ->
            updateJumpToReadMarkerViewVisibility()
        }

        timelineViewModel.onEach(RoomDetailViewState::asyncRoomSummary) { asyncRoomSummary ->
            val summary = asyncRoomSummary() ?: return@onEach
            if (summary.isGroup) {
                views.timeLineBg.setBackgroundResource(R.drawable.timeline_bg)
            } else {
                views.timeLineBg.setBackgroundColor(colorProvider.getColor(R.color.timeline_bg))
            }
        }

        messageComposerViewModel.onEach(MessageComposerViewState::sendMode, MessageComposerViewState::canSendMessage) { mode, canSend ->
            if (!canSend.boolean()) {
                return@onEach
            }
            when (mode) {
                is SendMode.Regular -> renderRegularMode(mode.text)
                is SendMode.Edit    -> renderSpecialMode(mode.timelineEvent, R.drawable.ic_edit, R.string.edit, mode.text)
                is SendMode.Quote   -> renderSpecialMode(mode.timelineEvent, R.drawable.ic_quote, R.string.action_quote, mode.text)
                is SendMode.Reply   -> renderSpecialMode(mode.timelineEvent, R.drawable.ic_reply, R.string.reply, mode.text)
                is SendMode.Voice   -> renderVoiceMessageMode(mode.text)
            }
        }

        timelineViewModel.onEach(
                RoomDetailViewState::syncState, RoomDetailViewState::incrementalSyncStatus, RoomDetailViewState::pushCounter
        ) { syncState, incrementalSyncStatus, pushCounter ->

            Timber.i("-----TimelineFragment------------------------------${syncState}--")
            views.syncStateView.render(
                    syncState, incrementalSyncStatus, pushCounter, vectorPreferences.developerShowDebugInfo()
            )
        }


        messageComposerViewModel.observeViewEvents {
            when (it) {
                is MessageComposerViewEvents.JoinRoomCommandSuccess          -> handleJoinedToAnotherRoom(it)
                is MessageComposerViewEvents.SlashCommandConfirmationRequest -> handleSlashCommandConfirmationRequest(it)
                is MessageComposerViewEvents.SendMessageResult               -> renderSendMessageResult(it)
                is MessageComposerViewEvents.ShowMessage                     -> showSnackWithMessage(it.message)
                is MessageComposerViewEvents.ShowRoomUpgradeDialog           -> handleShowRoomUpgradeDialog(it)
                is MessageComposerViewEvents.AnimateSendButtonVisibility     -> handleSendButtonVisibilityChanged(it)
                is MessageComposerViewEvents.OpenRoomMemberProfile           -> openRoomMemberProfile(it.userId)
                is MessageComposerViewEvents.VoicePlaybackOrRecordingFailure -> {
                    if (it.throwable is VoiceFailure.UnableToRecord) {
                        onCannotRecord()
                    }
                    showErrorInSnackbar(it.throwable)
                }
            }
        }

        timelineViewModel.observeViewEvents {
            when (it) {
                is RoomDetailViewEvents.CheckPermission                  -> handleCheckPermission(it.event)
                is RoomDetailViewEvents.CallStatus                       -> handleCallStatus(it)
                is RoomDetailViewEvents.Loading                          -> displayLoading(it)
                is RoomDetailViewEvents.Failure                          -> displayErrorMessage(it)
                is RoomDetailViewEvents.OnNewTimelineEvents              -> scrollOnNewMessageCallback.addNewTimelineEventIds(it.eventIds)
                is RoomDetailViewEvents.ActionSuccess                    -> displayRoomDetailActionSuccess(it)
                is RoomDetailViewEvents.ActionFailure                    -> displayRoomDetailActionFailure(it)
                is RoomDetailViewEvents.ShowMessage                      -> showSnackWithMessage(it.message)
                is RoomDetailViewEvents.NavigateToEvent                  -> navigateToEvent(it)
                is RoomDetailViewEvents.DownloadFileState                -> handleDownloadFileState(it)
                is RoomDetailViewEvents.ShowE2EErrorMessage              -> displayE2eError(it.withHeldCode)
                RoomDetailViewEvents.DisplayPromptForIntegrationManager  -> displayPromptForIntegrationManager()
                is RoomDetailViewEvents.OpenStickerPicker                -> openStickerPicker(it)
                is RoomDetailViewEvents.DisplayEnableIntegrationsWarning -> displayDisabledIntegrationDialog()
                is RoomDetailViewEvents.OpenIntegrationManager           -> openIntegrationManager()
                is RoomDetailViewEvents.OpenFile                         -> startOpenFileIntent(it)
                RoomDetailViewEvents.OpenActiveWidgetBottomSheet         -> onViewWidgetsClicked()
                is RoomDetailViewEvents.ShowInfoOkDialog                 -> showDialogWithMessage(it.message)
                is RoomDetailViewEvents.JoinJitsiConference              -> joinJitsiRoom(it.widget, it.withVideo)
                RoomDetailViewEvents.LeaveJitsiConference                -> leaveJitsiConference()
                RoomDetailViewEvents.ShowWaitingView                     -> vectorBaseActivity.showWaitingView()
                RoomDetailViewEvents.HideWaitingView                     -> vectorBaseActivity.hideWaitingView()
                is RoomDetailViewEvents.RequestNativeWidgetPermission    -> requestNativeWidgetPermission(it)
                is RoomDetailViewEvents.OpenRoom                         -> handleOpenRoom(it)
                RoomDetailViewEvents.OpenInvitePeople                    -> navigator.openInviteUsersToRoom(requireContext(), timelineArgs.roomId)
                RoomDetailViewEvents.OpenSetRoomAvatarDialog             -> galleryOrCameraDialogHelper.show()
                RoomDetailViewEvents.OpenRoomSettings                    -> handleOpenRoomSettings(RoomProfileActivity.EXTRA_DIRECT_ACCESS_ROOM_SETTINGS)
                RoomDetailViewEvents.OpenRoomProfile                     -> handleOpenRoomSettings()
                is RoomDetailViewEvents.ShowRoomAvatarFullScreen         -> it.matrixItem?.let { item ->
                    navigator.openBigImageViewer(requireActivity(), it.view, item)
                }
                is RoomDetailViewEvents.StartChatEffect                  -> handleChatEffect(it.type)
                RoomDetailViewEvents.StopChatEffects                     -> handleStopChatEffects()
                is RoomDetailViewEvents.DisplayAndAcceptCall             -> acceptIncomingCall(it)
                RoomDetailViewEvents.RoomReplacementStarted              -> handleRoomReplacement()
                is RoomDetailViewEvents.ChangeLocationIndicator          -> handleChangeLocationIndicator(it)
            }
        }

        if (savedInstanceState == null) {
            handleShareData()
            handleSpaceShare()
        }
        errorInteract = object : GlobalErrorInteractHandler {
            override fun handlerThrowable(throwable: Throwable, roomId: String) {
                if (!TextUtils.equals(timelineArgs.roomId, roomId)) {
                    return
                }
                if (TextUtils.equals(throwable.message, "over limit")) {
                    vectorBaseActivity.runOnUiThread {
                        vectorBaseActivity.toast(getString(R.string.over_limit))
                    }
                } else if (throwable is ServerError && TextUtils.equals(
                                "M_USER_NOT_MORTGAGED", throwable.error.code
                        )) {

                    vectorBaseActivity.runOnUiThread {
                        val create = getErrorDialog()
                        if (create.isShowing) {
                            return@runOnUiThread
                        }
                        create.show()
                    }
                }
            }

            override fun handlerThrowable(code: String, throwable: Throwable, roomId: String) {
                if (!TextUtils.equals(timelineArgs.roomId, roomId)) {
                    return
                }
                if (code == MatrixError.M_TOO_LARGE) {
                    vectorBaseActivity.runOnUiThread {
                        vectorBaseActivity.run { toast("${getString(R.string.upload_files_larger_than)}${throwable.message}M") }
                    }
                }
            }
        }
        session.addGlobalErrorListener(errorInteract)
    }

    
    private val storeActivityResultLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {

            val event = reSendTimelineEvent
            
            reSendTimelineEvent = null
            if (null != event) {
                
                timelineViewModel.handle(RoomDetailAction.ReSendMedia(event))
            }
        } else {
            if (deniedPermanently) {
                activity?.onPermissionDeniedDialog(R.string.denied_permission_generic)
            }
            
            reSendTimelineEvent = null
        }
    }

    val PERMISSIONS_FOR_STORE = listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    var reSendTimelineEvent: TimelineEvent? = null
    fun handleCheckPermission(event: TimelineEvent) {
        if (checkPermissions(PERMISSIONS_FOR_STORE, requireActivity(), storeActivityResultLauncher)) {
            
            timelineViewModel.handle(RoomDetailAction.ReSendMedia(event))
        } else {
            reSendTimelineEvent = event
        }
    }

    private fun displayLoading(loading: RoomDetailViewEvents.Loading) {
        if (loading.show) {
            showLoading("")
        } else {
            dismissLoadingDialog()
        }
    }

    private fun handleCallStatus(callStatus: RoomDetailViewEvents.CallStatus) {
        if (!callStatus.hasUserInfo) {
            getErrorDialog().show()
            return
        }
        if (callStatus.isVedio) {
            callActionsHandler.onVideoCallClicked()
        } else {
            callActionsHandler.onVoiceCallClicked()
        }
    }

    private var needUserInfoRefersh: Boolean = false

    private lateinit var errorDialog: AlertDialog
    private fun getErrorDialog(): AlertDialog {

        if (this::errorDialog.isInitialized) {
            return errorDialog
        }
        errorDialog =
                MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.dialog_title_tips).setMessage(getString(R.string.pelease_do_palge)).setNegativeButton(
                        R.string.do_palge_cancel, null
                ).setPositiveButton(R.string.do_palge_ok, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        needUserInfoRefersh = true
                        
                        val address = ChatStatusProvide.getAddress(vectorBaseActivity)
                        val intent = Intent()
                        intent.setClassName(vectorBaseActivity, SM_NEWME_FRAGMENT)
                        intent.putExtra(KEY_ADDRESS, address)
                        intent.putExtra(KEY_NICKNAME, address)
                        startActivity(intent)
                    }
                }).create()
        return errorDialog
    }

    private val KEY_ADDRESS = "address"
    private val KEY_NICKNAME = "nikeName"

    
    private val SM_NEWME_FRAGMENT = "com.app.me.destory_group.DestoryGroupActivity"
    private fun handleSlashCommandConfirmationRequest(action: MessageComposerViewEvents.SlashCommandConfirmationRequest) {
        when (action.parsedCommand) {
            is ParsedCommand.UnignoreUser -> promptUnignoreUser(action.parsedCommand)
            else                          -> TODO("Add case for ${action.parsedCommand.javaClass.simpleName}")
        }
        lockSendButton = false
    }

    private fun promptUnignoreUser(command: ParsedCommand.UnignoreUser) {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.room_participants_action_unignore_title).setMessage(
                getString(
                        R.string.settings_unignore_user, command.userId
                )
        ).setPositiveButton(R.string.unignore) { _, _ ->
            messageComposerViewModel.handle(MessageComposerAction.SlashCommandConfirmed(command))
        }.setNegativeButton(R.string.action_cancel, null).show()
    }

    private fun renderVoiceMessageMode(content: String) {
        ContentAttachmentData.fromJsonString(content)?.let { audioAttachmentData ->
            views.voiceMessageRecorderView.isVisible = true
            messageComposerViewModel.handle(MessageComposerAction.InitializeVoiceRecorder(audioAttachmentData))
        }
    }

    private fun handleSendButtonVisibilityChanged(event: MessageComposerViewEvents.AnimateSendButtonVisibility) {
        if (event.isVisible) {
            views.voiceMessageRecorderView.isVisible = false
            views.composerLayout.views.sendButton.alpha = 0f
            views.composerLayout.views.sendButton.isVisible = true
            views.composerLayout.views.sendButton.animate().alpha(1f).setDuration(150).start()
        } else {
            views.composerLayout.views.sendButton.isInvisible = true
            views.voiceMessageRecorderView.alpha = 0f
            views.voiceMessageRecorderView.isVisible = true
            views.voiceMessageRecorderView.animate().alpha(1f).setDuration(150).start()
        }
    }

    private fun setupRemoveJitsiWidgetView() {
        views.removeJitsiWidgetView.onCompleteSliding = {
            withState(timelineViewModel) {
                val jitsiWidgetId = it.jitsiState.widgetId ?: return@withState
                if (it.jitsiState.hasJoined) {
                    leaveJitsiConference()
                }
                timelineViewModel.handle(RoomDetailAction.RemoveWidget(jitsiWidgetId))
            }
        }
    }

    private fun leaveJitsiConference() {
        ConferenceEventEmitter(vectorBaseActivity).emitConferenceEnded()
    }

    private fun onBroadcastJitsiEvent(conferenceEvent: ConferenceEvent) {
        timelineViewModel.handle(RoomDetailAction.UpdateJoinJitsiCallStatus(conferenceEvent))
    }

    private fun onCannotRecord() {
        
        messageComposerViewModel.handle(MessageComposerAction.OnVoiceRecordingUiStateChanged(RecordingUiState.Idle))
    }

    private fun acceptIncomingCall(event: RoomDetailViewEvents.DisplayAndAcceptCall) {
        val intent = VectorCallActivity.newIntent(
                context = vectorBaseActivity, call = event.call, mode = VectorCallActivity.INCOMING_ACCEPT
        )
        startActivity(intent)
    }

    private fun handleRoomReplacement() {
        
        
        val tag = JoinReplacementRoomBottomSheet::javaClass.name
        JoinReplacementRoomBottomSheet().show(childFragmentManager, tag)
    }

    private fun handleShowRoomUpgradeDialog(roomDetailViewEvents: MessageComposerViewEvents.ShowRoomUpgradeDialog) {
        val tag = MigrateRoomBottomSheet::javaClass.name
        MigrateRoomBottomSheet.newInstance(timelineArgs.roomId, roomDetailViewEvents.newVersion).show(parentFragmentManager, tag)
    }

    private fun handleChatEffect(chatEffect: ChatEffect) {
        when (chatEffect) {
            ChatEffect.CONFETTI -> {
                views.viewKonfetti.isVisible = true
                views.viewKonfetti.play()
            }
            ChatEffect.SNOWFALL -> {
                views.viewSnowFall.isVisible = true
                views.viewSnowFall.restartFalling()
            }
        }
    }

    private fun handleStopChatEffects() {
        TransitionManager.beginDelayedTransition(views.rootConstraintLayout)
        views.viewSnowFall.isVisible = false
        
        views.viewKonfetti.isInvisible = true
    }

    override fun onImageReady(uri: Uri?) {
        uri ?: return
        timelineViewModel.handle(
                RoomDetailAction.SetAvatarAction(
                        newAvatarUri = uri, newAvatarFileName = getFilenameFromUri(requireContext(), uri) ?: UUID.randomUUID().toString()
                )
        )
    }

    private fun handleOpenRoomSettings(directAccess: Int? = null) {
        navigator.openRoomProfile(
                requireContext(), timelineArgs.roomId, directAccess
        )
    }

    private fun handleOpenRoom(openRoom: RoomDetailViewEvents.OpenRoom) {
        navigator.openRoom(requireContext(), openRoom.roomId, null)
        if (openRoom.closeCurrentRoom) {
            requireActivity().finish()
        }
    }

    private fun handleShowLocationPreview(locationContent: MessageLocationContent, senderId: String) {
        val isSelfLocation = locationContent.isSelfLocation()
        navigator.openLocationSharing(
                context = requireContext(),
                roomId = timelineArgs.roomId,
                mode = LocationSharingMode.PREVIEW,
                initialLocationData = locationContent.toLocationData(),
                locationOwnerId = if (isSelfLocation) senderId else null
        )
    }

    private fun handleChangeLocationIndicator(event: RoomDetailViewEvents.ChangeLocationIndicator) {
        views.locationLiveStatusIndicator.isVisible = event.isVisible
    }

    private fun displayErrorMessage(error: RoomDetailViewEvents.Failure) {
        if (error.showInDialog) displayErrorDialog(error.throwable) else showErrorInSnackbar(error.throwable)
    }

    private fun requestNativeWidgetPermission(it: RoomDetailViewEvents.RequestNativeWidgetPermission) {
        val tag = RoomWidgetPermissionBottomSheet::class.java.name
        val dFrag = childFragmentManager.findFragmentByTag(tag) as? RoomWidgetPermissionBottomSheet
        if (dFrag != null && dFrag.dialog?.isShowing == true && !dFrag.isRemoving) {
            return
        } else {
            RoomWidgetPermissionBottomSheet.newInstance(
                    WidgetArgs(
                            baseUrl = it.domain, kind = WidgetKind.ROOM, roomId = timelineArgs.roomId, widgetId = it.widget.widgetId
                    )
            ).apply {
                directListener = { granted ->
                    if (granted) {
                        timelineViewModel.handle(
                                RoomDetailAction.EnsureNativeWidgetAllowed(
                                        widget = it.widget, userJustAccepted = true, grantedEvents = it.grantedEvents
                                )
                        )
                    }
                }
            }.show(childFragmentManager, tag)
        }
    }

    private val integrationManagerActivityResultLauncher = registerStartForActivityResult {
        
    }

    private fun openIntegrationManager(screen: String? = null) {
        navigator.openIntegrationManager(
                context = requireContext(),
                activityResultLauncher = integrationManagerActivityResultLauncher,
                roomId = timelineArgs.roomId,
                integId = null,
                screen = screen
        )
    }

    private fun setupEmojiButton() {
        views.composerLayout.views.composerEmojiButton.debouncedClicks {
            messageComposerViewModel.handle(MessageComposerAction.CloseTextVoiceCloseActions)
            emojiPopup.toggle()
        }
    }

    private fun createEmojiPopup(): EmojiPopup {
        return EmojiPopup.Builder.fromRootView(views.rootConstraintLayout).setKeyboardAnimationStyle(R.style.emoji_fade_animation_style).setOnEmojiPopupShownListener {
            views.composerLayout.views.composerEmojiButton.apply {
                contentDescription = getString(R.string.a11y_close_emoji_picker)
                setImageResource(R.drawable.ic_keyboard)
            }
        }.setOnEmojiPopupDismissListenerLifecycleAware {
            views.composerLayout.views.composerEmojiButton.apply {
                contentDescription = getString(R.string.a11y_open_emoji_picker)
                setImageResource(R.drawable.ic_insert_emoji)
            }
        }.build(views.composerLayout.views.composerEditText)
    }

    
    private fun EmojiPopup.Builder.setOnEmojiPopupDismissListenerLifecycleAware(action: () -> Unit): EmojiPopup.Builder {
        return setOnEmojiPopupDismissListener {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                action()
            }
        }
    }

    private val permissionVoiceMessageLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            
        } else if (deniedPermanently) {
            vectorBaseActivity.onPermissionDeniedSnackbar(R.string.denied_permission_voice_message)
        }
    }

    private fun createFailedMessagesWarningCallback(): FailedMessagesWarningView.Callback {
        return object : FailedMessagesWarningView.Callback {
            override fun onDeleteAllClicked() {
                MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.event_status_delete_all_failed_dialog_title).setMessage(getString(R.string.event_status_delete_all_failed_dialog_message)).setNegativeButton(
                        R.string.no, null
                ).setPositiveButton(R.string.yes) { _, _ ->
                    timelineViewModel.handle(RoomDetailAction.RemoveAllFailedMessages)
                }.show()
            }

            override fun onRetryClicked() {
                if (checkPermissions(PERMISSIONS_FOR_STORE, requireActivity(), storeResendAllActivityResultLauncher)) {
                    
                    timelineViewModel.handle(RoomDetailAction.ResendAll)
                }
            }
        }
    }

    
    private val storeResendAllActivityResultLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            timelineViewModel.handle(RoomDetailAction.ResendAll)
        } else {
            if (deniedPermanently) {
                activity?.onPermissionDeniedDialog(R.string.denied_permission_generic)
            }
        }
    }

    private fun setupVoiceTextMessageView() {
        views.messageTranslateView.callback = object : MessageTranslateView.Callback {
            override fun onLangChange(isChinese: Boolean) {
                vibrate(requireContext())
                val form = if (isChinese) {
                    "zh"
                } else {
                    "en"
                }
                val to = if (isChinese) {
                    "en"
                } else {
                    "zh"
                }
                messageComposerViewModel.handle(MessageComposerAction.OnLangChange(isChinese, form, to))
            }

            override fun onVoiceRecordingStarted() {
                vibrate(requireContext())
                messageComposerViewModel.handle(MessageComposerAction.StartTextRecordingVoiceMessage)
            }

            override fun onVoiceRecordingEnded() {
                vibrate(requireContext())
                updateRecordingUiState(MessageTranslateView.TranslateRecordingUiState.Edit)
                
                messageComposerViewModel.handle(MessageComposerAction.TextRecordingEnded)
            }

            override fun onVoiceRecordingTransEnded() {
                
            }

            
            override fun onRecordingLimitReached() {
                vibrate(requireContext())
                
            }

            override fun onVoiceRecordingTransClear() {
                messageComposerViewModel.handle(MessageComposerAction.EndTextVoiceActions(true))
                vibrate(requireContext())
                updateRecordingUiState(MessageTranslateView.TranslateRecordingUiState.Idle)
            }

            override fun onSendVoiceMessage(content: String) {
                
                messageComposerViewModel.handle(MessageComposerAction.EndRecordingTextVoiceMessage(content))
                
            }

            
            override fun onDeleteVoiceMessage() {
                vibrate(requireContext())
                messageComposerViewModel.handle(MessageComposerAction.CloseTextVoiceCloseActions)
            }

            private fun updateRecordingUiState(state: MessageTranslateView.TranslateRecordingUiState) {
                messageComposerViewModel.handle(
                        MessageComposerAction.OnVoiceTextRecordingUiStateChanged(state)
                )
            }
        }
    }

    private fun setupVoiceMessageView() {
        audioMessagePlaybackTracker.track(AudioMessagePlaybackTracker.RECORDING_ID, views.voiceMessageRecorderView)
        views.voiceMessageRecorderView.callback = object : VoiceMessageRecorderView.Callback {

            override fun onVoiceRecordingStarted() {
                messageComposerViewModel.handle(MessageComposerAction.CloseTextVoiceCloseActions)
                if (checkPermissions(PERMISSIONS_FOR_VOICE_MESSAGE, requireActivity(), permissionVoiceMessageLauncher)) {
                    messageComposerViewModel.handle(MessageComposerAction.StartRecordingVoiceMessage)
                    vibrate(requireContext())
                    updateRecordingUiState(RecordingUiState.Recording(clock.epochMillis()))
                }
            }

            override fun onVoicePlaybackButtonClicked() {
                messageComposerViewModel.handle(MessageComposerAction.PlayOrPauseRecordingPlayback)
            }

            
            override fun onVoiceRecordingCancelled() {
                messageComposerViewModel.handle(MessageComposerAction.EndRecordingVoiceMessage(isCancelled = true, rootThreadEventId = getRootThreadEventId()))
                vibrate(requireContext())
                updateRecordingUiState(RecordingUiState.Idle)
            }

            override fun onVoiceRecordingLocked() {
                val startedState = withState(messageComposerViewModel) { it.voiceRecordingUiState as? RecordingUiState.Recording }
                val startTime = startedState?.recordingStartTimestamp ?: clock.epochMillis()
                updateRecordingUiState(RecordingUiState.Locked(startTime))
            }

            override fun onVoiceRecordingEnded() {
                onSendVoiceMessage()
                jumpToBottom()
            }

            
            override fun onSendVoiceMessage() {
                messageComposerViewModel.handle(
                        MessageComposerAction.EndRecordingVoiceMessage(isCancelled = false, rootThreadEventId = getRootThreadEventId())
                )
                updateRecordingUiState(RecordingUiState.Idle)
            }

            override fun onDeleteVoiceMessage() {
                messageComposerViewModel.handle(
                        MessageComposerAction.EndRecordingVoiceMessage(isCancelled = true, rootThreadEventId = getRootThreadEventId())
                )
                updateRecordingUiState(RecordingUiState.Idle)
            }

            override fun onRecordingLimitReached() {
                messageComposerViewModel.handle(
                        MessageComposerAction.PauseRecordingVoiceMessage
                )
                updateRecordingUiState(RecordingUiState.Draft)
            }

            override fun onRecordingWaveformClicked() {
                messageComposerViewModel.handle(
                        MessageComposerAction.PauseRecordingVoiceMessage
                )
                updateRecordingUiState(RecordingUiState.Draft)
            }

            override fun onVoiceWaveformTouchedUp(percentage: Float, duration: Int) {
                messageComposerViewModel.handle(
                        MessageComposerAction.VoiceWaveformTouchedUp(AudioMessagePlaybackTracker.RECORDING_ID, duration, percentage)
                )
            }

            override fun onVoiceWaveformMoved(percentage: Float, duration: Int) {
                messageComposerViewModel.handle(
                        MessageComposerAction.VoiceWaveformTouchedUp(AudioMessagePlaybackTracker.RECORDING_ID, duration, percentage)
                )
            }

            private fun updateRecordingUiState(state: RecordingUiState) {
                messageComposerViewModel.handle(
                        MessageComposerAction.OnVoiceRecordingUiStateChanged(state)
                )
            }
        }
    }

    private fun setupLiveLocationIndicator() {
        views.locationLiveStatusIndicator.stopButton.debouncedClicks {
            timelineViewModel.handle(RoomDetailAction.StopLiveLocationSharing)
        }
    }

    private fun joinJitsiRoom(jitsiWidget: Widget, enableVideo: Boolean) {
        navigator.openRoomWidget(requireContext(), timelineArgs.roomId, jitsiWidget, mapOf(JitsiCallViewModel.ENABLE_VIDEO_OPTION to enableVideo))
    }

    private fun openStickerPicker(event: RoomDetailViewEvents.OpenStickerPicker) {
        navigator.openStickerPicker(requireContext(), stickerActivityResultLauncher, timelineArgs.roomId, event.widget)
    }

    private fun startOpenFileIntent(action: RoomDetailViewEvents.OpenFile) {
        if (action.mimeType == MimeTypes.Apk) {
            installApk(action)
        } else {
            openFile(action)
        }
    }

    private fun openFile(action: RoomDetailViewEvents.OpenFile) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndTypeAndNormalize(action.uri, action.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        requireActivity().safeStartActivity(intent)
    }

    private fun installApk(action: RoomDetailViewEvents.OpenFile) {
        val safeContext = context ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!safeContext.packageManager.canRequestPackageInstalls()) {
                timelineViewModel.pendingEvent = action
                startInstallFromSourceIntent(safeContext, installApkActivityResultLauncher)
            } else {
                openFile(action)
            }
        } else {
            openFile(action)
        }
    }

    private val installApkActivityResultLauncher = registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            timelineViewModel.pendingEvent?.let {
                if (it is RoomDetailViewEvents.OpenFile) {
                    openFile(it)
                }
            }
        } else {
            
        }
        timelineViewModel.pendingEvent = null
    }

    private fun displayPromptForIntegrationManager() {
        
        val builder = MaterialAlertDialogBuilder(requireContext())
        val v: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_no_sticker_pack, null)
        builder.setView(v).setPositiveButton(R.string.yes) { _, _ ->
            
            openIntegrationManager(
                    screen = WidgetType.StickerPicker.preferred
            )
        }.setNegativeButton(R.string.no, null).show()
    }

    private fun handleJoinedToAnotherRoom(action: MessageComposerViewEvents.JoinRoomCommandSuccess) {
        views.composerLayout.setTextIfDifferent("")
        lockSendButton = false
        navigator.openRoom(vectorBaseActivity, action.roomId)
    }

    private fun handleShareData() {
        when (val sharedData = timelineArgs.sharedData) {
            is SharedData.Text        -> {
                messageComposerViewModel.handle(MessageComposerAction.EnterRegularMode(sharedData.text, fromSharing = true))
            }
            is SharedData.Attachments -> {
                
                onContentAttachmentsReady(sharedData.attachmentData)
            }
            null                      -> Timber.v("No share data to process")
        }
    }

    private fun handleSpaceShare() {
        timelineArgs.openShareSpaceForId?.let { spaceId ->
            ShareSpaceBottomSheet.show(childFragmentManager, spaceId, true)
            view?.post {
                handleChatEffect(ChatEffect.CONFETTI)
            }
        }
    }

    override fun onDestroyView() {
        audioMessagePlaybackTracker.makeAllPlaybacksIdle()
        lazyLoadedViews.unBind()
        timelineEventController.callback = null
        timelineEventController.removeModelBuildListener(modelBuildListener)
        currentCallsViewPresenter.unBind()
        modelBuildListener = null
        autoCompleter.clear()
        debouncer.cancelAll()
        views.timelineRecyclerView.cleanup()
        session.removeGlobalErrorListener(errorInteract)
        super.onDestroyView()
    }

    override fun onDestroy() {
        timelineViewModel.handle(RoomDetailAction.ExitTrackingUnreadMessagesState)
        super.onDestroy()
    }

    private fun onJumpToAtClicked() = withState(timelineViewModel) {
        val summary = it.asyncRoomSummary() ?: return@withState

        if (summary.atMsg.isEmpty()) {
            return@withState
        }
        val msg = summary.atMsg.get(0)
        timelineViewModel.handle(RoomDetailAction.NavigateToEvent(msg.eventId, false))
    }

    private fun setupJumpToBottomView() {
        
        views.jumpToAtView.onClick {
            onJumpToAtClicked()
        }
        views.jumpToBottomView.visibility = View.INVISIBLE
        views.jumpToBottomView.debouncedClicks {
            timelineViewModel.handle(RoomDetailAction.ExitTrackingUnreadMessagesState)
            views.jumpToBottomView.visibility = View.INVISIBLE
            if (!timelineViewModel.timeline.isLive) {
                scrollOnNewMessageCallback.forceScrollOnNextUpdate()
                timelineViewModel.timeline.restartWithEventId(null)
            } else {
                layoutManager.scrollToPosition(0)
            }
        }

        jumpToBottomViewVisibilityManager = JumpToBottomViewVisibilityManager(
                views.jumpToBottomView, debouncer, views.timelineRecyclerView, layoutManager
        )
    }

    private fun setupJumpToReadMarkerView() {
        views.jumpToReadMarkerView.debouncedClicks {
            onJumpToReadMarkerClicked()
        }
        views.jumpToReadMarkerView.setOnCloseIconClickListener {
            timelineViewModel.handle(RoomDetailAction.MarkAllAsRead)
        }
    }

    private fun setupActiveCallView() {
        currentCallsViewPresenter.bind(views.currentCallsView, this)
    }

    private fun navigateToEvent(action: RoomDetailViewEvents.NavigateToEvent) {
        val scrollPosition = timelineEventController.searchPositionOfEvent(action.eventId)
        if (scrollPosition == null) {
            scrollOnHighlightedEventCallback.scheduleScrollTo(action.eventId)
        } else {
            views.timelineRecyclerView.stopScroll()
            layoutManager.scrollToPosition(scrollPosition)
        }
    }

    private fun handleDownloadFileState(action: RoomDetailViewEvents.DownloadFileState) {
        val activity = requireActivity()
        if (action.throwable != null) {
            activity.toast(errorFormatter.toHumanReadable(action.throwable))
        }
    }

    private fun setupNotificationView() {
        views.notificationAreaView.delegate = object : NotificationAreaView.Delegate {
            override fun onTombstoneEventClicked() {
                timelineViewModel.handle(RoomDetailAction.JoinAndOpenReplacementRoom)
            }

            override fun onMisconfiguredEncryptionClicked() {
                timelineViewModel.handle(RoomDetailAction.OnClickMisconfiguredEncryption)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isThreadTimeLine()) {
            if (menu is MenuBuilder) menu.setOptionalIconsVisible(true)
        }
        super.onCreateOptionsMenu(menu, inflater)
        
        menu.findItem(R.id.open_matrix_apps)?.let { menuItem ->
            menuItem.actionView.debouncedClicks {
                onOptionsItemSelected(menuItem)
            }
        }
        val joinConfItem = menu.findItem(R.id.join_conference)
        (joinConfItem.actionView as? JoinConferenceView)?.onJoinClicked = {
            timelineViewModel.handle(RoomDetailAction.JoinJitsiCall)
        }

        
        menu.findItem(R.id.menu_timeline_thread_list)?.let { menuItem ->
            menuItem.actionView.setOnClickListener {
                onOptionsItemSelected(menuItem)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.forEach {
            it.isVisible = timelineViewModel.isMenuItemVisible(it.itemId)
        }
        withState(timelineViewModel) { state ->
            
            val hasCallInRoom = callManager.getCallsByRoomId(state.roomId).isNotEmpty() || state.jitsiState.hasJoined
            val callButtonsEnabled = !hasCallInRoom && when (state.asyncRoomSummary.invoke()?.joinedMembersCount) {
                1    -> false
                2    -> state.isAllowedToStartWebRTCCall
                else -> state.isAllowedToManageWidgets
            }
            setOf(R.id.voice_call, R.id.video_call).forEach {
                menu.findItem(it).icon?.alpha = if (callButtonsEnabled) 0xFF else 0x40
            }

            val matrixAppsMenuItem = menu.findItem(R.id.open_matrix_apps)
            val widgetsCount = state.activeRoomWidgets.invoke()?.size ?: 0
            val hasOnlyJitsiWidget = widgetsCount == 1 && state.hasActiveJitsiWidget()
            if (widgetsCount == 0 || hasOnlyJitsiWidget) {
                
                val actionView = matrixAppsMenuItem.actionView
                actionView.findViewById<ImageView>(R.id.action_view_icon_image).setColorFilter(
                        ThemeUtils.getColor(
                                requireContext(), R.attr.vctr_content_secondary
                        )
                )
                actionView.findViewById<TextView>(R.id.cart_badge).isVisible = false
                matrixAppsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            } else {
                val actionView = matrixAppsMenuItem.actionView
                actionView.findViewById<ImageView>(R.id.action_view_icon_image).setColorFilter(colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                actionView.findViewById<TextView>(R.id.cart_badge).setTextOrHide("$widgetsCount")
                matrixAppsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }

            
            updateMenuThreadNotificationBadge(menu, state)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.timeline_test                     -> {
                
                timelineViewModel.handle(RoomDetailAction.SendCusText(""))
                true
            }
            R.id.groupRoomSetting                  -> {
                settingRouter()
                true
            }
            R.id.upgradeRoom                       -> {
                upgradeRoom()
                true
            }
            R.id.invite                            -> {
                navigator.openInviteUsersToRoom(requireActivity(), timelineArgs.roomId)
                true
            }
            R.id.timeline_setting                  -> {
                navigator.openRoomProfile(requireActivity(), timelineArgs.roomId)

                true
            }
            R.id.open_matrix_apps                  -> {
                timelineViewModel.handle(RoomDetailAction.ManageIntegrations)
                true
            }
            R.id.voice_call                        -> {
                if (!BuildConfig.NEED_PAY) {
                    callActionsHandler.onVoiceCallClicked()
                } else {
                    timelineViewModel.handle(RoomDetailAction.CheckCallUserInfoStatus(false, needUserInfoRefersh))
                }
                true
            }
            R.id.video_call                        -> {
                if (!BuildConfig.NEED_PAY) {
                    callActionsHandler.onVideoCallClicked()
                } else {
                    timelineViewModel.handle(RoomDetailAction.CheckCallUserInfoStatus(true, needUserInfoRefersh))
                }
                true
            }
            R.id.menu_timeline_thread_list         -> {
                navigateToThreadList()
                true
            }
            R.id.search                            -> {
                handleSearchAction()
                true
            }
            R.id.dev_tools                         -> {
                navigator.openDevTools(requireContext(), timelineArgs.roomId)
                true
            }
            R.id.menu_thread_timeline_copy_link    -> {
                getRootThreadEventId()?.let {
                    val permalink = session.permalinkService().createPermalink(timelineArgs.roomId, it)
                    copyToClipboard(requireContext(), permalink, false)
                    showSnackWithMessage(getString(R.string.copied_to_clipboard))
                }
                true
            }
            R.id.menu_thread_timeline_view_in_room -> {
                handleViewInRoomAction()
                true
            }
            R.id.menu_thread_timeline_share        -> {
                getRootThreadEventId()?.let {
                    val permalink = session.permalinkService().createPermalink(timelineArgs.roomId, it)
                    shareText(requireContext(), permalink)
                }
                true
            }
            else                                   -> super.onOptionsItemSelected(item)
        }
    }

    private val clusterLauncher = registerStartForActivityResult { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            
            refreshGroupInfo()
            joinLiveData.postValue(true)
        }
    }

    private fun refreshGroupInfo() {
        session.getRoom(timelineArgs.roomId)?.setRoomSummaryGroupStatus(true)
        lifecycleScope.launch {
            var walletPay: IWalletPay? = null
            vectorBaseActivity.applicationContext?.takeAs<IApplication>()?.apply {
                walletPay = getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay
            }
            val data = walletPay?.httpGetDeviceGroupData(timelineArgs.roomId) ?: return@launch
            data.data?.let {
                session.getRoom(timelineArgs.roomId)?.setRoomSummaryOwner(it.cluster_owner)
                it.level_info?.let {
                    session.getRoom(timelineArgs.roomId)?.setRoomSummaryLevel(it.level)
                }
            }
        }
    }

    private fun upgradeRoom() = withState(timelineViewModel) { mainState ->
        val summary = mainState.asyncRoomSummary() ?: return@withState
        ClusterActivity.newIntent(requireContext(), 0, timelineArgs.roomId, summary.displayName,summary.avatarUrl).let {
            clusterLauncher.launch(it)
        }
    }

    private fun settingRouter() = withState(timelineViewModel) { state ->
        val summary = state.asyncRoomSummary() ?: return@withState
        
        val intent = Intent()
        intent.setClassName(vectorBaseActivity, "com.app.view.groupinformation.GroupInformationActivity")
        intent.putExtra("groupId", timelineArgs.roomId)
        intent.putExtra("isGroupLeader", session.myOriginUId == summary.owner)
        startActivity(intent)
    }

    
    private fun updateMenuThreadNotificationBadge(menu: Menu, state: RoomDetailViewState) {
        val menuThreadList = menu.findItem(R.id.menu_timeline_thread_list).actionView
        val badgeFrameLayout = menuThreadList.findViewById<FrameLayout>(R.id.threadNotificationBadgeFrameLayout)
        val badgeTextView = menuThreadList.findViewById<TextView>(R.id.threadNotificationBadgeTextView)

        val unreadThreadMessages = state.threadNotificationBadgeState.numberOfLocalUnreadThreads
        val userIsMentioned = state.threadNotificationBadgeState.isUserMentioned

        if (unreadThreadMessages > 0) {
            badgeFrameLayout.isVisible = true
            badgeTextView.text = unreadThreadMessages.toString()
            val badgeDrawable = DrawableCompat.wrap(badgeFrameLayout.background)
            val color = ContextCompat.getColor(requireContext(), if (userIsMentioned) R.color.palette_vermilion else R.color.palette_gray_200)
            DrawableCompat.setTint(badgeDrawable, color)
            badgeFrameLayout.background = badgeDrawable
        } else {
            badgeFrameLayout.isVisible = false
        }
    }

    
    private fun handleViewInRoomAction() {
        getRootThreadEventId()?.let {
            val newRoom = timelineArgs.copy(threadTimelineArgs = null, eventId = it)
            context?.let { con ->
                val int = RoomDetailActivity.newIntent(con, newRoom)
                int.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                con.startActivity(int)
            }
        }
    }

    private fun handleSearchAction() {
        Timber.tag("jues_search").d("timelineArgs:\n${Gson().toJson(timelineArgs)}")
        navigator.openSearchForResult(
                context = requireContext(),
                roomId = timelineArgs.roomId,
                eventId = null,
                rootThreadEventId = null,
                roomDisplayName = timelineViewModel.getRoomSummary()?.displayName,
                roomAvatarUrl = timelineViewModel.getRoomSummary()?.avatarUrl,
                launch = searchResultListener
        )
    }

    
    private fun setUpSearchResultListener(): ActivityResultLauncher<Intent> {
        return registerStartForActivityResult {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { it1 -> handleSearchResult(it1) }
            }
        }
    }

    private fun handleSearchResult(intent: Intent) {
        
        intent.extras?.getParcelable<TimelineArgs>(RoomDetailActivity.EXTRA_ROOM_DETAIL_ARGS)?.let { it1 ->
            it1.eventId?.let { it ->
                timelineViewModel.handle(RoomDetailAction.SearchResultListener(it))
                
            }
        }
    }

    private fun displayDisabledIntegrationDialog() {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.disabled_integration_dialog_title).setMessage(R.string.disabled_integration_dialog_content).setPositiveButton(
                R.string.settings
        ) { _, _ ->
            navigator.openSettings(requireActivity(), VectorSettingsActivity.EXTRA_DIRECT_ACCESS_GENERAL)
        }.setNegativeButton(R.string.action_cancel, null).show()
    }

    private fun renderRegularMode(content: String) {
        autoCompleter.exitSpecialMode()
        views.composerLayout.collapse()
        views.composerLayout.setTextIfDifferent(content)
        views.composerLayout.views.sendButton.contentDescription = getString(R.string.action_send)
    }

    private fun renderSpecialMode(event: TimelineEvent, @DrawableRes iconRes: Int, @StringRes descriptionRes: Int, defaultContent: String) {
        autoCompleter.enterSpecialMode()
        
        views.composerLayout.views.composerRelatedMessageTitle.apply {
            text = event.senderInfo.disambiguatedDisplayName
            setTextColor(matrixItemColorProvider.getColor(MatrixItem.UserItem(event.root.senderId ?: "@")))
        }

        val messageContent: MessageContent? = event.getLastMessageContent()
        val nonFormattedBody = when (messageContent) {
            is MessageAudioContent -> getAudioContentBodyText(messageContent)
            is MessagePollContent  -> messageContent.getBestPollCreationInfo()?.question?.getBestQuestion()
            else                   -> messageContent?.body.orEmpty()
        }
        var formattedBody: CharSequence? = null
        if (messageContent is MessageTextContent && messageContent.format == MessageFormat.FORMAT_MATRIX_HTML) {
            val parser = Parser.builder().build()
            val document = parser.parse(messageContent.formattedBody ?: messageContent.body)
            formattedBody = eventHtmlRenderer.render(document, pillsPostProcessor)
        }
        views.composerLayout.views.composerRelatedMessageContent.text = (formattedBody ?: nonFormattedBody)

        
        val data = event.buildImageContentRendererData(dimensionConverter.dpToPx(66))
        val isImageVisible = if (data != null) {
            imageContentRenderer.render(data, ImageContentRenderer.Mode.THUMBNAIL, views.composerLayout.views.composerRelatedMessageImage)
            true
        } else {
            imageContentRenderer.clear(views.composerLayout.views.composerRelatedMessageImage)
            false
        }

        views.composerLayout.views.composerRelatedMessageImage.isVisible = isImageVisible

        views.composerLayout.setTextIfDifferent(defaultContent)

        views.composerLayout.views.composerRelatedMessageActionIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes))
        views.composerLayout.views.sendButton.contentDescription = getString(descriptionRes)

        avatarRenderer.render(event.senderInfo.toMatrixItem(), views.composerLayout.views.composerRelatedMessageAvatar)

        views.composerLayout.expand {
            if (isAdded) {
                
                focusComposerAndShowKeyboard()
                views.composerLayout.views.composerRelatedMessageImage.isVisible = isImageVisible
            }
        }
        focusComposerAndShowKeyboard()
    }

    private fun getAudioContentBodyText(messageContent: MessageAudioContent): String {
        val formattedDuration = DateUtils.formatElapsedTime(((messageContent.audioInfo?.duration ?: 0) / 1000).toLong())
        return if (messageContent.voiceMessageIndicator != null) {
            getString(R.string.voice_message_reply_content, formattedDuration)
        } else {
            getString(R.string.audio_message_reply_content, messageContent.body, formattedDuration)
        }
    }

    override fun onResume() {

        super.onResume()
        notificationDrawerManager.setCurrentRoom(timelineArgs.roomId)
        roomDetailPendingActionStore.data?.let { handlePendingAction(it) }
        roomDetailPendingActionStore.data = null

        
        setupVoiceMessageView()
    }

    private fun handlePendingAction(roomDetailPendingAction: RoomDetailPendingAction) {
        when (roomDetailPendingAction) {
            is RoomDetailPendingAction.JumpToReadReceipt -> timelineViewModel.handle(RoomDetailAction.JumpToReadReceipt(roomDetailPendingAction.userId))
            is RoomDetailPendingAction.MentionUser       -> insertUserDisplayNameInTextEditor(roomDetailPendingAction.userId)
            is RoomDetailPendingAction.OpenRoom          -> handleOpenRoom(
                    RoomDetailViewEvents.OpenRoom(
                            roomDetailPendingAction.roomId, roomDetailPendingAction.closeCurrentRoom
                    )
            )
        }
    }

    override fun onPause() {
        super.onPause()
        notificationDrawerManager.setCurrentRoom(null)
        audioMessagePlaybackTracker.pauseAllPlaybacks()

        if (withState(messageComposerViewModel) { it.isVoiceRecording } && requireActivity().isChangingConfigurations) {
            
        } else {
            messageComposerViewModel.handle(MessageComposerAction.OnEntersBackground(views.composerLayout.text.toString()))
        }
    }

    private val attachmentFileActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            attachmentsHelper.onFileResult(it.data)
        }
    }

    
    private val attachmentRedPacketActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            val redPacketID = it.data?.getStringExtra("redPacketId")
            val transferNum = it.data?.getStringExtra("transferNum")
            val transferSymbol = it.data?.getStringExtra("symbol")
            if (null != transferNum && null != transferSymbol) {
                timelineViewModel.handle(RoomDetailAction.SendRedPacket(redPacketID, transferNum, transferSymbol))
                jumpToBottom()
            }
        }
    }

    private val attachmentContactActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            attachmentsHelper.onContactResult(it.data)
        }
    }

    private val attachmentMediaActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            attachmentsHelper.onMediaResult(it.data)
        }
    }

    private val attachmentCameraActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            attachmentsHelper.onCameraResult()
        }
    }

    private val attachmentCameraVideoActivityResultLauncher = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {
            attachmentsHelper.onCameraVideoResult()
        }
    }

    private val contentAttachmentActivityResultLauncher = registerStartForActivityResult { activityResult ->
        val data = activityResult.data ?: return@registerStartForActivityResult
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val sendData = AttachmentsPreviewActivity.getOutput(data)
            val keepOriginalSize = AttachmentsPreviewActivity.getKeepOriginalSize(data)
            timelineViewModel.handle(RoomDetailAction.SendMedia(sendData, !keepOriginalSize))
            jumpToBottom()
        }
    }

    private val emojiActivityResultLauncher = registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val eventId = EmojiReactionPickerActivity.getOutputEventId(activityResult.data)
            val reaction = EmojiReactionPickerActivity.getOutputReaction(activityResult.data)
            if (eventId != null && reaction != null) {
                timelineViewModel.handle(RoomDetailAction.SendReaction(eventId, reaction))
            }
        }
    }

    private val stickerActivityResultLauncher = registerStartForActivityResult { activityResult ->
        val data = activityResult.data ?: return@registerStartForActivityResult
        if (activityResult.resultCode == Activity.RESULT_OK) {
            WidgetActivity.getOutput(data).toModel<MessageStickerContent>()?.let { content ->
                timelineViewModel.handle(RoomDetailAction.SendSticker(content))
            }
        }
    }

    private val startCallActivityResultLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            (timelineViewModel.pendingAction as? RoomDetailAction.StartCall)?.let {
                timelineViewModel.pendingAction = null
                timelineViewModel.handle(it)
            }
        } else {
            if (deniedPermanently) {
                activity?.onPermissionDeniedDialog(R.string.denied_permission_generic)
            }
            cleanUpAfterPermissionNotGranted()
        }
    }

    

    private fun setupRecyclerView() {
        timelineEventController.callback = this
        timelineEventController.timeline = timelineViewModel.timeline

        views.timelineRecyclerView.trackItemsVisibilityChange()
        layoutManager = object : LinearLayoutManager(context, RecyclerView.VERTICAL, true) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                updateJumpToReadMarkerViewVisibility()
                jumpToBottomViewVisibilityManager.maybeShowJumpToBottomViewVisibilityWithDelay()
            }
        }
        val stateRestorer = LayoutManagerStateRestorer(layoutManager).register()
        scrollOnNewMessageCallback = ScrollOnNewMessageCallback(layoutManager, timelineEventController)
        scrollOnHighlightedEventCallback = ScrollOnHighlightedEventCallback(views.timelineRecyclerView, layoutManager, timelineEventController)
        views.timelineRecyclerView.layoutManager = layoutManager
        views.timelineRecyclerView.itemAnimator = null
        views.timelineRecyclerView.setHasFixedSize(true)
        modelBuildListener = OnModelBuildFinishedListener {
            it.dispatchTo(stateRestorer)
            it.dispatchTo(scrollOnNewMessageCallback)
            it.dispatchTo(scrollOnHighlightedEventCallback)
        }
        timelineEventController.addModelBuildListener(modelBuildListener)


        if (vectorPreferences.swipeToReplyIsEnabled()) {
            val quickReplyHandler = object : RoomMessageTouchHelperCallback.QuickReplayHandler {
                override fun performQuickReplyOnHolder(model: EpoxyModel<*>) {
                    (model as? AbsMessageItem)?.attributes?.informationData?.let {
                        val eventId = it.eventId
                        messageComposerViewModel.handle(MessageComposerAction.EnterReplyMode(eventId, views.composerLayout.text.toString()))
                    }
                }

                override fun canSwipeModel(model: EpoxyModel<*>): Boolean {
                    val canSendMessage = withState(messageComposerViewModel) {
                        it.canSendMessage
                    }
                    if (!canSendMessage.boolean()) {
                        return false
                    }
                    return when (model) {
                        is MessageFileItem, is MessageAudioItem, is MessageVoiceItem, is MessageImageVideoItem, is MessageTextItem -> {
                            return (model as AbsMessageItem).attributes.informationData.sendState == SendState.SYNCED
                        }
                        else                                                                                                       -> false
                    }
                }
            }
            val swipeCallback = RoomMessageTouchHelperCallback(requireContext(), R.drawable.ic_reply, quickReplyHandler)
            val touchHelper = ItemTouchHelper(swipeCallback)
            touchHelper.attachToRecyclerView(views.timelineRecyclerView)
        }
        views.timelineRecyclerView.adapter = timelineEventController.adapter
        views.timelineRecyclerView.addGlidePreloader(epoxyController = timelineEventController,
                requestManager = GlideApp.with(this),
                preloader = glidePreloader { requestManager, epoxyModel: MessageImageVideoItem, _ ->
                    imageContentRenderer.createGlideRequest(
                            epoxyModel.mediaData, ImageContentRenderer.Mode.THUMBNAIL, requestManager as GlideRequests
                    )
                })
    }

    private fun updateJumpToReadMarkerViewVisibility() {
        if (isThreadTimeLine()) return
        var positionOfReadMarker: Int?
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val state = timelineViewModel.awaitState()
            val showJumpToUnreadBanner = when (state.unreadState) {
                UnreadState.Unknown, UnreadState.HasNoUnread -> false
                
                is UnreadState.ReadMarkerNotLoaded           -> {
                    true
                }
                is UnreadState.HasUnread                     -> {
                    if (state.canShowJumpToReadMarker) {
                        
                        val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                        positionOfReadMarker = withContext(Dispatchers.Default) {
                            timelineEventController.getPositionOfReadMarker()
                        }
                        if (positionOfReadMarker == null) {
                            false
                        } else {
                            positionOfReadMarker!! > lastVisibleItem
                        }
                    } else {
                        false
                    }
                }

            }
            views.jumpToReadMarkerView.isVisible = showJumpToUnreadBanner
        }
    }

    private fun setupComposer() {
        val composerEditText = views.composerLayout.views.composerEditText
        autoCompleter.setup(composerEditText)

        observerUserTyping()

        if (vectorPreferences.sendMessageWithEnter()) {
            
            composerEditText.inputType = composerEditText.inputType and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE.inv()
            composerEditText.imeOptions = EditorInfo.IME_ACTION_SEND
        }

        composerEditText.setOnEditorActionListener { v, actionId, keyEvent ->
            val imeActionId = actionId and EditorInfo.IME_MASK_ACTION
            if (EditorInfo.IME_ACTION_DONE == imeActionId || EditorInfo.IME_ACTION_SEND == imeActionId) {
                sendTextMessage(v.text)
                true
            }
            
            else if (null != keyEvent && !keyEvent.isShiftPressed && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS) {
                sendTextMessage(v.text)
                true
            } else false
        }

        views.composerLayout.views.composerEmojiButton.isVisible = vectorPreferences.showEmojiKeyboard()

        if (isThreadTimeLine() && timelineArgs.threadTimelineArgs?.startsThread == true) {
            
            views.composerLayout.views.composerEditText.showKeyboard(andRequestFocus = true)
        }
        views.composerLayout.callback = object : MessageComposerView.Callback {
            override fun onAddAttachment() {
                if (!::attachmentTypeSelector.isInitialized) {
                    attachmentTypeSelector = AttachmentTypeSelectorView(vectorBaseActivity, vectorBaseActivity.layoutInflater, this@TimelineFragment)
                    attachmentTypeSelector.setAttachmentVisibility(
                            AttachmentTypeSelectorView.Type.LOCATION, vectorPreferences.isLocationSharingEnabled()
                    )
                    attachmentTypeSelector.setAttachmentVisibility(
                            AttachmentTypeSelectorView.Type.POLL, !isThreadTimeLine()
                    )

                    
                    withState(timelineViewModel) { state ->
                        val joineMember = state.asyncRoomSummary.invoke()?.joinedMembersCount
                        attachmentTypeSelector.setAttachmentVisibility(
                                AttachmentTypeSelectorView.Type.RED_PACKET, joineMember == 2
                        )
                    }
                }
                attachmentTypeSelector.show(views.composerLayout.views.attachmentButton)
            }

            override fun onSendMessage(text: CharSequence) {
                sendTextMessage(text)
            }

            override fun onCloseRelatedMessage() {
                messageComposerViewModel.handle(MessageComposerAction.EnterRegularMode(views.composerLayout.text.toString(), false))
            }

            override fun onRichContentSelected(contentUri: Uri): Boolean {
                return sendUri(contentUri)
            }

            override fun onTextChanged(text: CharSequence) {
                messageComposerViewModel.handle(MessageComposerAction.OnTextChanged(text))
            }
        }
    }

    private fun sendTextMessage(text: CharSequence) {
        if (lockSendButton) {
            Timber.w("Send button is locked")
            return
        }
        if (text.isNotBlank()) {
            
            views.composerLayout.collapse(true)
            lockSendButton = true
            messageComposerViewModel.handle(MessageComposerAction.SendMessage(text, vectorPreferences.isMarkdownEnabled()))
            
            jumpToBottom()
            emojiPopup.dismiss()
        }
    }

    
    private fun jumpToBottom() {
        if (!timelineViewModel.timeline.isLive) {
            scrollOnNewMessageCallback.forceScrollOnNextUpdate()
            timelineViewModel.timeline.restartWithEventId(null)
        } else {
            layoutManager.scrollToPosition(0)
        }
    }

    private fun observerUserTyping() {
        if (isThreadTimeLine()) return
        views.composerLayout.views.composerEditText.textChanges().skipInitialValue().debounce(300).map { it.isNotEmpty() }.onEach {
            Timber.d("Typing: User is typing: $it")
            messageComposerViewModel.handle(MessageComposerAction.UserIsTyping(it))
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        views.composerLayout.views.composerEditText.focusChanges().onEach {
            timelineViewModel.handle(RoomDetailAction.ComposerFocusChange(it))
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun sendUri(uri: Uri): Boolean {
        val shareIntent = Intent(Intent.ACTION_SEND, uri)
        val isHandled = attachmentsHelper.handleShareIntent(requireContext(), shareIntent)
        if (!isHandled) {
            Toast.makeText(requireContext(), R.string.error_handling_incoming_share, Toast.LENGTH_SHORT).show()
        }
        return isHandled
    }

    private fun getLevelDrawableRes(level: Int): Int {
        val resName = "sm_pledge$level"
        val packageName = vectorBaseActivity.packageName
        return resources.getIdentifier(resName, "mipmap", packageName)
    }

    private fun isEnglish(): Boolean {
        val localeNow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }

        return localeNow == Locale.ENGLISH
    }

    var izShowAnim = false
    override fun invalidate() = withState(timelineViewModel, messageComposerViewModel) { mainState, messageComposerState ->

        invalidateOptionsMenu()
        val summary = mainState.asyncRoomSummary()

        val count = summary?.atMsg?.size ?: 0
        views.jumpToAtView.isVisible = count != 0
        views.roomUnreadAtView.text = "@"
        views.roomUnreadCounterBadgeView.render(UnreadCounterBadgeView.State(count, false))
        renderToolbar(summary)
        renderTypingMessageNotification(summary, mainState)
        views.removeJitsiWidgetView.render(mainState)
        if (mainState.hasFailedSending) {
            lazyLoadedViews.failedMessagesWarningView(inflateIfNeeded = true, createFailedMessagesWarningCallback())?.isVisible = true
        } else {
            lazyLoadedViews.failedMessagesWarningView(inflateIfNeeded = false)?.isVisible = false
        }
        val inviter = mainState.asyncInviter()

        views.smPledge.isVisible = summary?.isGroup == true
        summary?.let {
            views.smPledge.setImageResource(getLevelDrawableRes(it.level ?: 0))
            if (summary.izUpgrade && !izShowAnim) {
                
                refreshGroupInfo()
                joinLiveData.postValue(true)
            }
        }

        if (summary?.membership == Membership.JOIN) {
            views.jumpToBottomView.count = summary.notificationCount
            views.jumpToBottomView.drawBadge = summary.hasUnreadMessages
            timelineEventController.update(mainState)
            lazyLoadedViews.inviteView(false)?.isVisible = false

            if (mainState.tombstoneEvent == null) {
                views.composerLayout.isInvisible = !messageComposerState.isComposerVisible
                views.voiceMessageRecorderView.isVisible = messageComposerState.isVoiceMessageRecorderVisible
                views.composerLayout.views.sendButton.isInvisible = !messageComposerState.isSendButtonVisible
                views.voiceMessageRecorderView.render(messageComposerState.voiceRecordingUiState)
                views.messageTranslateView.isVisible = messageComposerState.isTextVoiceMessageRecorderVisible
                views.messageTranslateView.render(messageComposerState.voiceTextRecordingUiState)
                views.messageTranslateView.renderText(messageComposerState.isChinese)

                views.messageTranslateView.renderContent((messageComposerState.originText ?: "") + (messageComposerState.partialText ?: ""))
                views.composerLayout.setRoomEncrypted(summary.isEncrypted)
                
                when (messageComposerState.canSendMessage) {
                    CanSendStatus.Allowed                    -> {
                        NotificationAreaView.State.Hidden
                    }
                    is CanSendStatus.NoPermission            -> {
                        if (messageComposerState.canSendMessage.izServerNoticeId) {
                            
                            views.hideComposerViews()
                            
                            NotificationAreaView.State.Hidden
                        } else {
                            NotificationAreaView.State.NoPermissionToPost
                        }
                    }
                    is CanSendStatus.UnSupportedE2eAlgorithm -> {
                        NotificationAreaView.State.UnsupportedAlgorithm(mainState.isAllowedToSetupEncryption)
                    }
                }.let {
                    views.notificationAreaView.render(it)
                }
            } else {
                views.hideComposerViews()
                views.notificationAreaView.render(NotificationAreaView.State.Tombstone(mainState.tombstoneEvent))
            }
        } else if (summary?.membership == Membership.INVITE && inviter != null) {
            views.hideComposerViews()
            lazyLoadedViews.inviteView(true)?.apply {
                callback = this@TimelineFragment
                isVisible = true
                render(inviter, VectorInviteView.Mode.LARGE, mainState.changeMembershipState)
                setOnClickListener(null)
            }
            Unit
        } else if (mainState.asyncInviter.complete) {
            vectorBaseActivity.finish()
        }
    }

    private fun FragmentTimelineBinding.hideComposerViews() {
        composerLayout.isVisible = false
        voiceMessageRecorderView.isVisible = false
    }

    private fun renderTypingMessageNotification(roomSummary: RoomSummary?, state: RoomDetailViewState) {
        if (!isThreadTimeLine() && roomSummary != null) {
            views.typingMessageView.isInvisible = state.typingUsers.isNullOrEmpty()
            state.typingUsers?.take(MAX_TYPING_MESSAGE_USERS_COUNT)?.let { senders -> views.typingMessageView.render(senders, avatarRenderer) }
        } else {
            views.typingMessageView.isInvisible = true
        }
    }

    private fun renderToolbar(roomSummary: RoomSummary?) {

        val izGroup = roomSummary?.isGroup ?: false

        if (!isThreadTimeLine()) {
            if (izGroup) {
                views.timeLineStatusBar.setBackgroundColor(colorProvider.getColor(R.color.default_theme_color))
                Eyes.setStatusBarTextColor(vectorBaseActivity, false)

                views.includeRoomToolbar.roomToolbarTitleView.setTextColor(colorProvider.getColor(R.color.white))
                views.includeRoomToolbar.roomToolbarPresenceImageView.setBackgroundResource(R.drawable.background_circle_cus)
                views.appBarLayout.setBackgroundColor(colorProvider.getColor(R.color.default_theme_color))
                views.roomToolbar.setBackgroundColor(colorProvider.getColor(R.color.default_theme_color))
                views.roomToolbar.setNavigationIconTint(colorProvider.getColor(R.color.white))
                views.roomToolbar.overflowIcon?.let {
                    DrawableCompat.setTint(it, colorProvider.getColor(R.color.white))
                }
            } else {

                views.timeLineStatusBar.setBackgroundColor(colorProvider.getColor(R.color.transparent))
                Eyes.setStatusBarTextColor(vectorBaseActivity, true)
                
                views.appBarLayout.setBackgroundResource(R.drawable.bg_timeline_app_barlayout)
                views.roomToolbar.setBackgroundColor(colorProvider.getColor(R.color.transparent))
            }

            views.includeRoomToolbar.roomToolbarContentView.isVisible = true
            views.includeThreadToolbar.roomToolbarThreadConstraintLayout.isVisible = false
            if (roomSummary == null) {
                views.includeRoomToolbar.roomToolbarContentView.isClickable = false
            } else {
                views.includeRoomToolbar.roomToolbarContentView.isClickable = roomSummary.membership == Membership.JOIN
                var displayName = roomSummary.displayName
                val izServerNotice = izServerNoticeId(timelineArgs.roomId, session.myUserId)
                if (izServerNotice) {
                    
                    displayName = vectorBaseActivity.getString(R.string.server_notice_name)
                    views.includeRoomToolbar.roomToolbarAvatarImageView.setImageResource(R.drawable.cus_server_ico)
                } else {
                    avatarRenderer.render(roomSummary.toMatrixItem(), views.includeRoomToolbar.roomToolbarAvatarImageView)
                }
                views.includeRoomToolbar.roomToolbarTitleView.text = displayName

                val showPresence = roomSummary.isDirect
                views.includeRoomToolbar.roomToolbarPresenceImageView.render(showPresence, roomSummary.directUserPresence)
                val shieldView = if (showPresence) views.includeRoomToolbar.roomToolbarTitleShield else views.includeRoomToolbar.roomToolbarAvatarShield
                shieldView.render(roomSummary.roomEncryptionTrustLevel)
                views.includeRoomToolbar.roomToolbarPublicImageView.isVisible = roomSummary.isPublic && !roomSummary.isDirect
            }
        } else {
            views.includeRoomToolbar.roomToolbarContentView.isVisible = false
            views.includeThreadToolbar.roomToolbarThreadConstraintLayout.isVisible = true
            timelineArgs.threadTimelineArgs?.let {
                val matrixItem = MatrixItem.RoomItem(it.roomId, it.displayName, it.avatarUrl)
                avatarRenderer.render(matrixItem, views.includeThreadToolbar.roomToolbarThreadImageView)
                views.includeThreadToolbar.roomToolbarThreadShieldImageView.render(it.roomEncryptionTrustLevel)
                views.includeThreadToolbar.roomToolbarThreadSubtitleTextView.text = it.displayName
            }
            views.includeThreadToolbar.roomToolbarThreadTitleTextView.text = resources.getText(R.string.thread_timeline_title)
        }
    }

    private fun renderSendMessageResult(sendMessageResult: MessageComposerViewEvents.SendMessageResult) {
        when (sendMessageResult) {
            is MessageComposerViewEvents.SlashCommandLoading               -> {
                showLoading(null)
            }
            is MessageComposerViewEvents.SlashCommandError                 -> {
                displayCommandError(getString(R.string.command_problem_with_parameters, sendMessageResult.command.command))
            }
            is MessageComposerViewEvents.SlashCommandUnknown               -> {
                displayCommandError(getString(R.string.unrecognized_command, sendMessageResult.command))
            }
            is MessageComposerViewEvents.SlashCommandResultOk              -> {
                handleSlashCommandResultOk(sendMessageResult.parsedCommand)
            }
            is MessageComposerViewEvents.SlashCommandResultError           -> {
                dismissLoadingDialog()
                displayCommandError(errorFormatter.toHumanReadable(sendMessageResult.throwable))
            }
            is MessageComposerViewEvents.SlashCommandNotImplemented        -> {
                displayCommandError(getString(R.string.not_implemented))
            }
            is MessageComposerViewEvents.SlashCommandNotSupportedInThreads -> {
                displayCommandError(getString(R.string.command_not_supported_in_threads, sendMessageResult.command.command))
            }
        }

        lockSendButton = false
    }

    private fun handleSlashCommandResultOk(parsedCommand: ParsedCommand) {
        dismissLoadingDialog()
        views.composerLayout.setTextIfDifferent("")
        when (parsedCommand) {
            is ParsedCommand.SetMarkdown -> {
                showSnackWithMessage(getString(if (parsedCommand.enable) R.string.markdown_has_been_enabled else R.string.markdown_has_been_disabled))
            }
            else                         -> Unit
        }
    }

    private fun displayCommandError(message: String) {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.command_error).setMessage(message).setPositiveButton(R.string.ok, null).show()
    }

    private fun displayE2eError(withHeldCode: WithHeldCode?) {
        val msgId = when (withHeldCode) {
            WithHeldCode.BLACKLISTED                            -> R.string.crypto_error_withheld_blacklisted
            WithHeldCode.UNVERIFIED                             -> R.string.crypto_error_withheld_unverified
            WithHeldCode.UNAUTHORISED, WithHeldCode.UNAVAILABLE -> R.string.crypto_error_withheld_generic
            else                                                -> R.string.notice_crypto_unable_to_decrypt_friendly_desc
        }
        MaterialAlertDialogBuilder(requireActivity()).setMessage(msgId).setPositiveButton(R.string.ok, null).show()
    }

    private fun promptReasonToReportContent(action: EventSharedAction.ReportContentCustom) {
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_report_content, null)
        val views = DialogReportContentBinding.bind(layout)

        MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.report_content_custom_title).setView(layout).setPositiveButton(R.string.report_content_custom_submit) { _, _ ->
            val reason = views.dialogReportContentInput.text.toString()
            timelineViewModel.handle(RoomDetailAction.ReportContent(action.eventId, action.senderId, reason))
        }.setNegativeButton(R.string.action_cancel, null).show()
    }

    private fun promptConfirmationToRedactEvent(action: EventSharedAction.Redact) {
        ConfirmationDialogBuilder.show(
                activity = requireActivity(),
                askForReason = action.askForReason,
                confirmationRes = action.dialogDescriptionRes,
                positiveRes = R.string.action_remove,
                reasonHintRes = R.string.delete_event_dialog_reason_hint,
                titleRes = action.dialogTitleRes
        ) { reason ->
            timelineViewModel.handle(RoomDetailAction.RedactAction(action.eventId, reason))
        }
    }

    private fun displayRoomDetailActionFailure(result: RoomDetailViewEvents.ActionFailure) {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.dialog_title_error).setMessage(errorFormatter.toHumanReadable(result.throwable)).setPositiveButton(
                R.string.ok, null
        ).show()
    }

    private fun displayRoomDetailActionSuccess(result: RoomDetailViewEvents.ActionSuccess) {
        when (val data = result.action) {
            is RoomDetailAction.ReportContent             -> {
                when {
                    data.spam          -> {
                        MaterialAlertDialogBuilder(
                                requireActivity(), R.style.ThemeOverlay_Vector_MaterialAlertDialog_NegativeDestructive
                        ).setTitle(R.string.content_reported_as_spam_title).setMessage(R.string.content_reported_as_spam_content).setPositiveButton(
                                R.string.ok, null
                        ).setNegativeButton(R.string.block_user) { _, _ ->
                            timelineViewModel.handle(RoomDetailAction.IgnoreUser(data.senderId))
                        }.show()
                    }
                    data.inappropriate -> {
                        MaterialAlertDialogBuilder(
                                requireActivity(), R.style.ThemeOverlay_Vector_MaterialAlertDialog_NegativeDestructive
                        ).setTitle(R.string.content_reported_as_inappropriate_title).setMessage(R.string.content_reported_as_inappropriate_content).setPositiveButton(
                                R.string.ok, null
                        ).setNegativeButton(R.string.block_user) { _, _ ->
                            timelineViewModel.handle(RoomDetailAction.IgnoreUser(data.senderId))
                        }.show()
                    }
                    else               -> {
                        MaterialAlertDialogBuilder(
                                requireActivity(), R.style.ThemeOverlay_Vector_MaterialAlertDialog_NegativeDestructive
                        ).setTitle(R.string.content_reported_title).setMessage(R.string.content_reported_content).setPositiveButton(
                                R.string.ok, null
                        ).setNegativeButton(R.string.block_user) { _, _ ->
                            timelineViewModel.handle(RoomDetailAction.IgnoreUser(data.senderId))
                        }.show()
                    }
                }
            }
            is RoomDetailAction.RequestVerification       -> {
                Timber.v("## SAS RequestVerification action")
                VerificationBottomSheet.withArgs(
                        timelineArgs.roomId, data.userId
                ).show(parentFragmentManager, "REQ")
            }
            is RoomDetailAction.AcceptVerificationRequest -> {
                Timber.v("## SAS AcceptVerificationRequest action")
                VerificationBottomSheet.withArgs(
                        timelineArgs.roomId, data.otherUserId, data.transactionId
                ).show(parentFragmentManager, "REQ")
            }
            is RoomDetailAction.ResumeVerification        -> {
                val otherUserId = data.otherUserId ?: return
                VerificationBottomSheet.withArgs(
                        roomId = timelineArgs.roomId,
                        otherUserId = otherUserId,
                        transactionId = data.transactionId,
                ).show(parentFragmentManager, "REQ")
            }
            else                                          -> Unit
        }
    }

    
    override fun onUrlClicked(url: String, title: String): Boolean {
        viewLifecycleOwner.lifecycleScope.launch {
            val isManaged = permalinkHandler.launch(requireActivity(), url, object : NavigationInterceptor {
                override fun navToRoom(roomId: String?, eventId: String?, deepLink: Uri?, rootThreadEventId: String?): Boolean {
                    
                    if (roomId != timelineArgs.roomId) return false
                    
                    if (!isThreadTimeLine()) {
                        if (rootThreadEventId != null && userPreferencesProvider.areThreadMessagesEnabled()) {
                            
                            return false
                        }
                        return if (eventId == null) {
                            showSnackWithMessage(getString(R.string.navigate_to_room_when_already_in_the_room))
                            true
                        } else {
                            
                            timelineViewModel.handle(RoomDetailAction.NavigateToEvent(eventId, true))
                            true
                        }
                    } else {
                        return if (rootThreadEventId == getRootThreadEventId() && eventId == null) {
                            showSnackWithMessage(getString(R.string.navigate_to_thread_when_already_in_the_thread))
                            true
                        } else if (rootThreadEventId == getRootThreadEventId() && eventId != null) {
                            
                            timelineViewModel.handle(RoomDetailAction.NavigateToEvent(eventId, true))
                            true
                        } else {
                            false
                        }
                    }
                }

                override fun navToMemberProfile(userId: String, deepLink: Uri): Boolean {
                    openRoomMemberProfile(userId)
                    return true
                }
            })
            if (!isManaged) {
                if (title.isValidUrl() && url.isValidUrl() && URL(title).host != URL(url).host) {
                    MaterialAlertDialogBuilder(
                            requireActivity(), R.style.ThemeOverlay_Vector_MaterialAlertDialog_NegativeDestructive
                    ).setTitle(R.string.external_link_confirmation_title).setMessage(
                            getString(R.string.external_link_confirmation_message, title, url).toSpannable().colorizeMatchingText(
                                    url, colorProvider.getColorFromAttribute(R.attr.vctr_content_tertiary)
                            ).colorizeMatchingText(title, colorProvider.getColorFromAttribute(R.attr.vctr_content_tertiary))
                    ).setPositiveButton(R.string._continue) { _, _ ->
                        openUrlInExternalBrowser(requireContext(), url)
                    }.setNegativeButton(R.string.action_cancel, null).show()
                } else {
                    
                    openUrlInExternalBrowser(requireContext(), url)
                }
            }
        }
        
        return true
    }

    override fun onUrlLongClicked(url: String): Boolean {
        if (url != getString(R.string.edited_suffix) && url.isValidUrl()) {
            
            copyToClipboard(requireContext(), url, true, R.string.link_copied_to_clipboard)
        }
        return true
    }

    override fun onEventVisible(event: TimelineEvent) {
        timelineViewModel.handle(RoomDetailAction.TimelineEventTurnsVisible(event))
    }

    override fun onEventInvisible(event: TimelineEvent) {
        timelineViewModel.handle(RoomDetailAction.TimelineEventTurnsInvisible(event))
    }

    override fun onEncryptedMessageClicked(informationData: MessageInformationData, view: View) {
        vectorBaseActivity.notImplemented("encrypted message click")
    }

    override fun onImageMessageClicked(messageImageContent: MessageImageInfoContent, mediaData: ImageContentRenderer.Data, view: View, inMemory: List<AttachmentData>) {
        navigator.openMediaViewer(
                activity = requireActivity(), roomId = timelineArgs.roomId, mediaData = mediaData, view = view, inMemory = inMemory
        ) { pairs ->
            pairs.add(Pair(views.roomToolbar, ViewCompat.getTransitionName(views.roomToolbar) ?: ""))
            pairs.add(Pair(views.composerLayout, ViewCompat.getTransitionName(views.composerLayout) ?: ""))
        }
    }

    override fun onVideoMessageClicked(messageVideoContent: MessageVideoContent, mediaData: VideoContentRenderer.Data, view: View) {
        
        navigator.openMediaViewer(
                activity = requireActivity(), roomId = timelineArgs.roomId, mediaData = mediaData, view = view
        ) { pairs ->
            pairs.add(Pair(views.roomToolbar, ViewCompat.getTransitionName(views.roomToolbar) ?: ""))
            pairs.add(Pair(views.composerLayout, ViewCompat.getTransitionName(views.composerLayout) ?: ""))
        }
    }

    private fun cleanUpAfterPermissionNotGranted() {
        
        timelineViewModel.pendingAction = null
        attachmentsHelper.pendingType = null
    }

    override fun onLoadMore(direction: Timeline.Direction) {
        timelineViewModel.handle(RoomDetailAction.LoadMoreTimelineEvents(direction))
    }

    override fun onAddMoreReaction(event: TimelineEvent) {
        openEmojiReactionPicker(event.eventId)
    }

    override fun onEventCellClicked(informationData: MessageInformationData, messageContent: Any?, view: View, isRootThreadEvent: Boolean) {
        when (messageContent) {
            is MessageVerificationRequestContent -> {
                timelineViewModel.handle(RoomDetailAction.ResumeVerification(informationData.eventId, null))
            }
            is MessageWithAttachmentContent      -> {
                val action = RoomDetailAction.DownloadOrOpen(informationData.eventId, informationData.senderId, messageContent)
                timelineViewModel.handle(action)
            }
            is EncryptedEventContent             -> {
                timelineViewModel.handle(RoomDetailAction.TapOnFailedToDecrypt(informationData.eventId))
            }
            is MessageLocationContent            -> {
                handleShowLocationPreview(messageContent, informationData.senderId)
            }
            else                                 -> {
                val handled = onThreadSummaryClicked(informationData.eventId, isRootThreadEvent)
                if (!handled) {
                    Timber.d("No click action defined for this message content")
                }
            }
        }
    }

    override fun onEventLongClicked(informationData: MessageInformationData, messageContent: Any?, view: View): Boolean {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        val roomId = timelineArgs.roomId
        this.view?.hideKeyboard()

        MessageActionsBottomSheet.newInstance(roomId, informationData, isThreadTimeLine()).show(
                requireActivity().supportFragmentManager, "MESSAGE_CONTEXTUAL_ACTIONS"
        )

        return true
    }

    private fun handleCancelSend(action: EventSharedAction.Cancel) {
        if (action.force) {
            timelineViewModel.handle(RoomDetailAction.CancelSend(action.eventId, true))
        } else {
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.dialog_title_confirmation).setMessage(getString(R.string.event_status_cancel_sending_dialog_message)).setNegativeButton(
                    R.string.no, null
            ).setPositiveButton(R.string.yes) { _, _ ->
                timelineViewModel.handle(RoomDetailAction.CancelSend(action.eventId, false))
            }.show()
        }
    }

    override fun onThreadSummaryClicked(eventId: String, isRootThreadEvent: Boolean): Boolean {
        return if (vectorPreferences.areThreadMessagesEnabled() && isRootThreadEvent && !isThreadTimeLine()) {
            navigateToThreadTimeline(eventId)
            true
        } else {
            false
        }
    }

    override fun onAvatarClicked(informationData: MessageInformationData) {
        openRoomMemberProfile(informationData.senderId)
    }

    private fun openRoomMemberProfile(userId: String) {
        navigator.openRoomMemberProfile(userId = userId, roomId = timelineArgs.roomId, context = requireActivity(), displayMode = timelineArgs.displayMode)
    }

    override fun onMemberNameClicked(informationData: MessageInformationData) {
        insertUserDisplayNameInTextEditor(informationData.senderId)
    }

    override fun onClickOnReactionPill(informationData: MessageInformationData, reaction: String, on: Boolean) {
        if (on) {
            
            timelineViewModel.handle(RoomDetailAction.SendReaction(informationData.eventId, reaction))
        } else {
            
            timelineViewModel.handle(RoomDetailAction.UndoReaction(informationData.eventId, reaction))
        }
    }

    override fun onLongClickOnReactionPill(informationData: MessageInformationData, reaction: String) {
        ViewReactionsBottomSheet.newInstance(timelineArgs.roomId, informationData).show(requireActivity().supportFragmentManager, "DISPLAY_REACTIONS")
    }

    override fun onEditedDecorationClicked(informationData: MessageInformationData) {
        ViewEditHistoryBottomSheet.newInstance(timelineArgs.roomId, informationData).show(requireActivity().supportFragmentManager, "DISPLAY_EDITS")
    }

    override fun onTimelineItemAction(itemAction: RoomDetailAction) {
        timelineViewModel.handle(itemAction)
    }

    override fun getPreviewUrlRetriever(): PreviewUrlRetriever {
        return timelineViewModel.previewUrlRetriever
    }

    override fun onRoomCreateLinkClicked(url: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            permalinkHandler.launch(requireContext(), url, object : NavigationInterceptor {
                override fun navToRoom(roomId: String?, eventId: String?, deepLink: Uri?, rootThreadEventId: String?): Boolean {
                    requireActivity().finish()
                    return false
                }
            })
        }
    }

    override fun onReadReceiptsClicked(readReceipts: List<ReadReceiptData>) {
        DisplayReadReceiptsBottomSheet.newInstance(readReceipts).show(requireActivity().supportFragmentManager, "DISPLAY_READ_RECEIPTS")
    }

    override fun onReadMarkerVisible() {
        timelineViewModel.handle(RoomDetailAction.EnterTrackingUnreadMessagesState)
    }

    override fun onBalanceClicked(targetEvent: TimelineEvent) {
        onBalanceWithStatus(targetEvent)
    }

    private fun onBalanceWithStatus(targetEvent: TimelineEvent) = withState(timelineViewModel) {
        val summary = it.asyncRoomSummary() ?: return@withState

        val messageContent = targetEvent.getLastMessageContent()
        if (messageContent !is MessageCusAwardContent) {
            return@withState
        }
        vectorBaseActivity.applicationContext?.takeAs<IApplication>()?.apply {
            val app = getDelegate(ApplicationDelegate.MOODLE_TYPE_APP)?.appNotice
            
            app?.showBalanceDialog(
                    vectorBaseActivity,
                    messageContent.type,
                    summary.roomId,
                    "",
                    session.myOriginUId,
                    targetEvent.root.originServerTs
            ) { status, balance ->
                lifecycleScope.launch {
                    timelineViewModel.handle(RoomDetailAction.EditCusAwardMessage(targetEvent, MessageType.MSGTYPE_AWARD_TEXT, status, balance))
                }

            }
        }
    }

    override fun onCusClusterClicked() {
        
    }

    override fun onDposToClicked() {
        
    }

    override fun onDposMethodClicked() {
        
    }

    override fun onVoteClicked() {
        val intent = Intent()
        intent.setClassName(vectorBaseActivity, "com.app.view.groupinformation.vote.votelist.GovernancePoolsVoteListActivity".trim())
        intent.putExtra("groupId", timelineArgs.roomId)
        startActivity(intent)
    }

    override fun onDaoClicked() {

        val intent = Intent()
        intent.setClassName(vectorBaseActivity, "com.app.view.groupinformation.introduction.IntroductionPageActivity".trim())
        startActivity(intent)
    }

    override fun onLordInvitateClicked() {
        vectorBaseActivity.toast("")
    }

    override fun onLordManageClicked() {
        vectorBaseActivity.toast("")
    }

    
    override fun onPosClicked(type: String, param: String) {
        Timber.v("onPosClicked(${type}, ${param}")
        if (type == "guidBuyDvm") {
            
            val intent = Intent()
            intent.setClassName(vectorBaseActivity, "com.app.me.destory_group.DestoryGroupActivity")
            intent.putExtra("address", session.myOriginUId) 
            intent.putExtra("groupId", timelineArgs.roomId) 
            startActivity(intent)
        } else if (type == "moveGroup") {
            
            
            
            param.split(",").takeIf { it.size == 2 }?.let {
                MigrateGroupActivity.newIntent(vectorBaseActivity, it[0], it[1]).also {
                    startActivity(it)
                }
            }
        } else if (type == "goChat") {
            
            
            
            lifecycleScope.launch {
                val roomId = directRoomHelper.ensureDMExists(param)
                navigator.openRoom(vectorBaseActivity, roomId)
            }
        } else if(type == "chainBridgeComplete" || type == "chainBridgeError"){
            
            startActivity(ActivityRouter.getIntent(vectorBaseActivity, ActivityRouter.App.A_ChainBridgeOrdersActivity))
        }
    }

    override fun posClickListener(is3Day: Boolean) {
        startActivity(RoomProfileActivity.newIntent(vectorBaseActivity, timelineArgs.roomId, RoomProfileActivity.EXTRA_DIRECT_ACCESS_ROOM_MEMBERS_DAILY))
    }

    
    override fun activeClickListener(is3Day: Boolean) {

    }

    
    override fun lGClickListener(is3Day: Boolean) {
    }

    override fun onPreviewUrlClicked(url: String) {
        onUrlClicked(url, url)
    }

    override fun onPreviewUrlCloseClicked(eventId: String, url: String) {
        timelineViewModel.handle(RoomDetailAction.DoNotShowPreviewUrlFor(eventId, url))
    }

    override fun onPreviewUrlImageClicked(sharedView: View?, mxcUrl: String?, title: String?) {
        navigator.openBigImageViewer(requireActivity(), sharedView, mxcUrl, title)
    }

    override fun onVoiceControlButtonClicked(eventId: String, messageAudioContent: MessageAudioContent) {
        messageComposerViewModel.handle(MessageComposerAction.PlayOrPauseVoicePlayback(eventId, messageAudioContent))
    }

    override fun onVoiceWaveformTouchedUp(eventId: String, duration: Int, percentage: Float) {
        messageComposerViewModel.handle(MessageComposerAction.VoiceWaveformTouchedUp(eventId, duration, percentage))
    }

    override fun onVoiceWaveformMovedTo(eventId: String, duration: Int, percentage: Float) {
        messageComposerViewModel.handle(MessageComposerAction.VoiceWaveformMovedTo(eventId, duration, percentage))
    }

    override fun onAudioSeekBarMovedTo(eventId: String, duration: Int, percentage: Float) {
        messageComposerViewModel.handle(MessageComposerAction.AudioSeekBarMovedTo(eventId, duration, percentage))
    }

    private fun onShareActionClicked(action: EventSharedAction.Share) {
        when (action.messageContent) {
            is MessageTextContent           -> shareText(requireContext(), action.messageContent.body)
            is MessageLocationContent       -> {
                action.messageContent.toLocationData()?.let {
                    openLocation(requireActivity(), it.latitude, it.longitude)
                }
            }
            is MessageWithAttachmentContent -> {
                lifecycleScope.launch {
                    val result = runCatching { session.fileService().downloadFile(messageContent = action.messageContent) }
                    if (!isAdded) return@launch
                    result.fold({ shareMedia(requireContext(), it, getMimeTypeFromUri(requireContext(), it.toUri())) }, { showErrorInSnackbar(it) })
                }
            }
        }
    }

    private val saveActionActivityResultLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            sharedActionViewModel.pendingAction?.let {
                handleActions(it)
                sharedActionViewModel.pendingAction = null
            }
        } else {
            if (deniedPermanently) {
                activity?.onPermissionDeniedDialog(R.string.denied_permission_generic)
            }
            cleanUpAfterPermissionNotGranted()
        }
    }

    private fun onSaveActionClicked(action: EventSharedAction.Save) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !checkPermissions(
                        PERMISSIONS_FOR_WRITING_FILES, requireActivity(), saveActionActivityResultLauncher
                )) {
            sharedActionViewModel.pendingAction = action
            return
        }
        session.coroutineScope.launch {
            val result = runCatching { session.fileService().downloadFile(messageContent = action.messageContent) }
            if (!isAdded) return@launch
            result.mapCatching {
                saveMedia(
                        context = requireContext(),
                        file = it,
                        title = action.messageContent.body,
                        mediaMimeType = action.messageContent.mimeType ?: getMimeTypeFromUri(requireContext(), it.toUri()),
                        notificationUtils = notificationUtils
                )
            }.onFailure {
                if (!isAdded) return@onFailure
                showErrorInSnackbar(it)
            }
        }
    }

    private fun handleActions(action: EventSharedAction) {
        when (action) {
            is EventSharedAction.OpenUserProfile            -> {
                openRoomMemberProfile(action.userId)
            }
            is EventSharedAction.AddReaction                -> {
                openEmojiReactionPicker(action.eventId)
            }
            is EventSharedAction.ViewReactions              -> {
                ViewReactionsBottomSheet.newInstance(timelineArgs.roomId, action.messageInformationData).show(
                        requireActivity().supportFragmentManager, "DISPLAY_REACTIONS"
                )
            }
            is EventSharedAction.Copy                       -> {
                
                copyToClipboard(requireContext(), action.content, false)
                showSnackWithMessage(getString(R.string.copied_to_clipboard))
            }
            is EventSharedAction.Redact                     -> {
                promptConfirmationToRedactEvent(action)
            }
            is EventSharedAction.Share                      -> {
                onShareActionClicked(action)
            }
            is EventSharedAction.Save                       -> {
                onSaveActionClicked(action)
            }
            is EventSharedAction.ViewEditHistory            -> {
                onEditedDecorationClicked(action.messageInformationData)
            }
            is EventSharedAction.ViewSource                 -> {
                JSonViewerDialog.newInstance(
                        action.content, -1, createJSonViewerStyleProvider(colorProvider)
                ).show(childFragmentManager, "JSON_VIEWER")
            }
            is EventSharedAction.ViewDecryptedSource        -> {
                JSonViewerDialog.newInstance(
                        action.content, -1, createJSonViewerStyleProvider(colorProvider)
                ).show(childFragmentManager, "JSON_VIEWER")
            }
            is EventSharedAction.QuickReact                 -> {
                
                timelineViewModel.handle(RoomDetailAction.UpdateQuickReactAction(action.eventId, action.clickedOn, action.add))
            }
            is EventSharedAction.Edit                       -> {
                if (action.eventType in EventType.POLL_START) {
                    navigator.openCreatePoll(requireContext(), timelineArgs.roomId, action.eventId, PollMode.EDIT)
                } else if (withState(messageComposerViewModel) { it.isVoiceMessageIdle }) {
                    messageComposerViewModel.handle(MessageComposerAction.EnterEditMode(action.eventId, views.composerLayout.text.toString()))
                } else {
                    requireActivity().toast(R.string.error_voice_message_cannot_reply_or_edit)
                }
            }
            is EventSharedAction.Quote                      -> {
                messageComposerViewModel.handle(MessageComposerAction.EnterQuoteMode(action.eventId, views.composerLayout.text.toString()))
            }
            is EventSharedAction.Reply                      -> {
                if (withState(messageComposerViewModel) { it.isVoiceMessageIdle }) {
                    messageComposerViewModel.handle(MessageComposerAction.EnterReplyMode(action.eventId, views.composerLayout.text.toString()))
                } else {
                    requireActivity().toast(R.string.error_voice_message_cannot_reply_or_edit)
                }
            }
            is EventSharedAction.ReplyInThread              -> {
                if (withState(messageComposerViewModel) { it.isVoiceMessageIdle }) {
                    onReplyInThreadClicked(action)
                } else {
                    requireActivity().toast(R.string.error_voice_message_cannot_reply_or_edit)
                }
            }
            is EventSharedAction.ViewInRoom                 -> {
                if (withState(messageComposerViewModel) { it.isVoiceMessageIdle }) {
                    handleViewInRoomAction()
                } else {
                    requireActivity().toast(R.string.error_voice_message_cannot_reply_or_edit)
                }
            }
            is EventSharedAction.CopyPermalink              -> {
                val permalink = session.permalinkService().createPermalink(timelineArgs.roomId, action.eventId)
                copyToClipboard(requireContext(), permalink, false)
                showSnackWithMessage(getString(R.string.copied_to_clipboard))
            }
            is EventSharedAction.Resend                     -> {
                timelineViewModel.handle(RoomDetailAction.ResendMessage(action.eventId))
            }
            is EventSharedAction.Remove                     -> {
                timelineViewModel.handle(RoomDetailAction.RemoveFailedEcho(action.eventId))
            }
            is EventSharedAction.Cancel                     -> {
                handleCancelSend(action)
            }
            is EventSharedAction.ReportContentSpam          -> {
                timelineViewModel.handle(
                        RoomDetailAction.ReportContent(
                                action.eventId, action.senderId, "This message is spam", spam = true
                        )
                )
            }
            is EventSharedAction.ReportContentInappropriate -> {
                timelineViewModel.handle(
                        RoomDetailAction.ReportContent(
                                action.eventId, action.senderId, "This message is inappropriate", inappropriate = true
                        )
                )
            }
            is EventSharedAction.ReportContentCustom        -> {
                promptReasonToReportContent(action)
            }
            is EventSharedAction.IgnoreUser                 -> {
                action.senderId?.let { askConfirmationToIgnoreUser(it) }
            }
            is EventSharedAction.OnUrlClicked               -> {
                onUrlClicked(action.url, action.title)
            }
            is EventSharedAction.OnUrlLongClicked           -> {
                onUrlLongClicked(action.url)
            }
            is EventSharedAction.ReRequestKey               -> {
                timelineViewModel.handle(RoomDetailAction.ReRequestKeys(action.eventId))
            }
            is EventSharedAction.UseKeyBackup               -> {
                context?.let {
                    startActivity(KeysBackupRestoreActivity.intent(it))
                }
            }
            is EventSharedAction.EndPoll                    -> {
                askConfirmationToEndPoll(action.eventId)
            }
            is EventSharedAction.ReportContent              -> Unit 
            EventSharedAction.Separator                     -> Unit 
        }
    }

    private fun openEmojiReactionPicker(eventId: String) {
        emojiActivityResultLauncher.launch(EmojiReactionPickerActivity.intent(requireContext(), eventId))
    }

    private fun askConfirmationToEndPoll(eventId: String) {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Vector_MaterialAlertDialog).setTitle(R.string.end_poll_confirmation_title).setMessage(
                R.string.end_poll_confirmation_description
        ).setNegativeButton(R.string.action_cancel, null).setPositiveButton(R.string.end_poll_confirmation_approve_button) { _, _ ->
            timelineViewModel.handle(RoomDetailAction.EndPoll(eventId))
        }.show()
    }

    private fun askConfirmationToIgnoreUser(senderId: String) {
        MaterialAlertDialogBuilder(
                requireContext(), R.style.ThemeOverlay_Vector_MaterialAlertDialog_Destructive
        ).setTitle(R.string.room_participants_action_ignore_title).setMessage(R.string.room_participants_action_ignore_prompt_msg).setNegativeButton(
                R.string.action_cancel, null
        ).setPositiveButton(R.string.room_participants_action_ignore) { _, _ ->
            timelineViewModel.handle(RoomDetailAction.IgnoreUser(senderId))
        }.show()
    }

    
    @SuppressLint("SetTextI18n")
    private fun insertUserDisplayNameInTextEditor(userId: String) {
        val startToCompose = views.composerLayout.text.isNullOrBlank()

        if (startToCompose && userId == session.myUserId) {
            
            views.composerLayout.views.composerEditText.setText(Command.EMOTE.command + " ")
            views.composerLayout.views.composerEditText.setSelection(Command.EMOTE.command.length + 1)
        } else {
            val roomMember = timelineViewModel.getMember(userId)
            
            (roomMember?.displayName ?: userId).let { sanitizeDisplayName(it) }.let { displayName ->
                buildSpannedString {
                    append(displayName)
                    setSpan(
                            PillImageSpan(
                                    glideRequests, avatarRenderer, requireContext(), MatrixItem.UserItem(userId, displayName, roomMember?.avatarUrl)
                            ).also { it.bind(views.composerLayout.views.composerEditText) }, 0, displayName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    append(if (startToCompose) ": " else " ")
                }.let { pill ->
                    if (startToCompose) {
                        if (displayName.startsWith("/")) {
                            
                            views.composerLayout.views.composerEditText.append("\\")
                        }
                        views.composerLayout.views.composerEditText.append(pill)
                    } else {
                        views.composerLayout.views.composerEditText.text?.insert(views.composerLayout.views.composerEditText.selectionStart, pill)
                    }
                }
            }
        }
        focusComposerAndShowKeyboard()
    }

    private fun focusComposerAndShowKeyboard() {
        if (views.composerLayout.isVisible) {
            views.composerLayout.views.composerEditText.showKeyboard(andRequestFocus = true)
        }
    }

    private fun showSnackWithMessage(message: String) {
        view?.showOptimizedSnackbar(message)
    }

    private fun showDialogWithMessage(message: String) {
        MaterialAlertDialogBuilder(requireContext()).setMessage(message).setPositiveButton(getString(R.string.ok), null).show()
    }

    private fun onReplyInThreadClicked(action: EventSharedAction.ReplyInThread) {
        if (vectorPreferences.areThreadMessagesEnabled()) {
            navigateToThreadTimeline(action.eventId, action.startsThread)
        } else {
            displayThreadsBetaOptInDialog()
        }
    }

    

    private fun navigateToThreadTimeline(rootThreadEventId: String, startsThread: Boolean = false) {
        analyticsTracker.capture(Interaction.Name.MobileRoomThreadSummaryItem.toAnalyticsInteraction())
        context?.let {
            val roomThreadDetailArgs = ThreadTimelineArgs(
                    startsThread = startsThread,
                    roomId = timelineArgs.roomId,
                    displayName = timelineViewModel.getRoomSummary()?.displayName,
                    avatarUrl = timelineViewModel.getRoomSummary()?.avatarUrl,
                    roomEncryptionTrustLevel = timelineViewModel.getRoomSummary()?.roomEncryptionTrustLevel,
                    rootThreadEventId = rootThreadEventId
            )
            navigator.openThread(it, roomThreadDetailArgs)
        }
    }

    private fun displayThreadsBetaOptInDialog() {
        activity?.let {
            MaterialAlertDialogBuilder(it).setTitle(R.string.threads_beta_enable_notice_title).setMessage(threadsManager.getBetaEnableThreadsMessage()).setCancelable(
                    true
            ).setNegativeButton(R.string.action_not_now) { _, _ -> }.setPositiveButton(R.string.action_try_it_out) { _, _ ->
                threadsManager.enableThreadsAndRestart(it)
            }.show()?.findViewById<TextView>(android.R.id.message)?.apply {
                linksClickable = true
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    

    private fun navigateToThreadList() {
        analyticsTracker.capture(Interaction.Name.MobileRoomThreadListButton.toAnalyticsInteraction())
        context?.let {
            val roomThreadDetailArgs = ThreadTimelineArgs(
                    roomId = timelineArgs.roomId,
                    displayName = timelineViewModel.getRoomSummary()?.displayName,
                    roomEncryptionTrustLevel = timelineViewModel.getRoomSummary()?.roomEncryptionTrustLevel,
                    avatarUrl = timelineViewModel.getRoomSummary()?.avatarUrl
            )
            navigator.openThreadList(it, roomThreadDetailArgs)
        }
    }

    
    override fun onAcceptInvite() {
        timelineViewModel.handle(RoomDetailAction.AcceptInvite)
    }

    override fun onRejectInvite() {
        timelineViewModel.handle(RoomDetailAction.RejectInvite)
    }

    private fun onJumpToReadMarkerClicked() = withState(timelineViewModel) {
        if (it.unreadState is UnreadState.HasUnread) {
            timelineViewModel.handle(RoomDetailAction.NavigateToEvent(it.unreadState.firstUnreadEventId, false))
        }
        if (it.unreadState is UnreadState.ReadMarkerNotLoaded) {
            timelineViewModel.handle(RoomDetailAction.NavigateToEvent(it.unreadState.readMarkerId, false))
        }
    }

    
    private val typeSelectedActivityResultLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            val pendingType = attachmentsHelper.pendingType
            if (pendingType != null) {
                attachmentsHelper.pendingType = null
                launchAttachmentProcess(pendingType)
            }
        } else {
            if (deniedPermanently) {
                activity?.onPermissionDeniedDialog(R.string.denied_permission_generic)
            }
            cleanUpAfterPermissionNotGranted()
        }
    }

    override fun onTypeSelected(type: AttachmentTypeSelectorView.Type) {
        if (checkPermissions(type.permissions, requireActivity(), typeSelectedActivityResultLauncher)) {
            launchAttachmentProcess(type)
        } else {
            attachmentsHelper.pendingType = type
        }
    }

    private fun launchAttachmentProcess(type: AttachmentTypeSelectorView.Type) {
        if (type != AttachmentTypeSelectorView.Type.VOICE_TRANS) {
            messageComposerViewModel.handle(MessageComposerAction.CloseTextVoiceCloseActions)
        }
        when (type) {
            AttachmentTypeSelectorView.Type.CAMERA      -> attachmentsHelper.openCamera(
                    activity = requireActivity(),
                    vectorPreferences = vectorPreferences,
                    cameraActivityResultLauncher = attachmentCameraActivityResultLauncher,
                    cameraVideoActivityResultLauncher = attachmentCameraVideoActivityResultLauncher
            )
            AttachmentTypeSelectorView.Type.FILE        -> attachmentsHelper.selectFile(attachmentFileActivityResultLauncher)
            AttachmentTypeSelectorView.Type.GALLERY     -> attachmentsHelper.selectGallery(attachmentMediaActivityResultLauncher)
            AttachmentTypeSelectorView.Type.CONTACT     -> attachmentsHelper.selectContact(attachmentContactActivityResultLauncher)
            AttachmentTypeSelectorView.Type.STICKER     -> timelineViewModel.handle(RoomDetailAction.SelectStickerAttachment)
            AttachmentTypeSelectorView.Type.POLL        -> {
                navigator.openCreatePoll(requireContext(), timelineArgs.roomId, null, PollMode.CREATE)
                jumpToBottom()
            }
            AttachmentTypeSelectorView.Type.LOCATION    -> {
                navigator.openLocationSharing(
                        context = requireContext(),
                        roomId = timelineArgs.roomId,
                        mode = LocationSharingMode.STATIC_SHARING,
                        initialLocationData = null,
                        locationOwnerId = session.myUserId
                )
            }
            AttachmentTypeSelectorView.Type.RED_PACKET  -> {
                
                val toAddress: String? = timelineViewModel.getRoomSummary()?.directUserId
                val toAddr = toAddress?.let {
                    val start = it.indexOf("@")
                    val end = it.indexOf(":")
                    var addr = it
                    if (start != -1 && end != -1) {
                        addr = it.substring(start + 1, end)
                    }
                    addr
                }
                Timber.i("toAddr=$toAddr")
                attachmentRedPacketActivityResultLauncher.launch(WalletHandler.getRedPacketIntent(context = requireContext(), toAddr))
            }

            AttachmentTypeSelectorView.Type.VOICE_TRANS -> {
                vectorBaseActivity.hideKeyboard()

                lifecycleScope.launch {
                    val result = kotlin.runCatching {
                        session.translateIsOpen()
                    }
                    result.onFailure {
                        vectorBaseActivity.toast(errorFormatter.toHumanReadable(it))
                    }
                    result.onSuccess {
                        if (!it) {
                            vectorBaseActivity.toast(getString(R.string.tts_is_not_open))
                            return@launch
                        }
                        
                        messageComposerViewModel.handle(
                                MessageComposerAction.OnVoiceTextRecordingUiStateChanged(MessageTranslateView.TranslateRecordingUiState.Idle)
                        )
                    }

                }
            }
        }
    }

    
    override fun onContentAttachmentsReady(attachments: List<ContentAttachmentData>) {
        val grouped = attachments.toGroupedContentAttachmentData()
        if (grouped.notPreviewables.isNotEmpty()) {
            
            timelineViewModel.handle(RoomDetailAction.SendMedia(grouped.notPreviewables, false))
            jumpToBottom()
        }
        if (grouped.previewables.isNotEmpty()) {
            val intent = AttachmentsPreviewActivity.newIntent(requireContext(), AttachmentsPreviewArgs(grouped.previewables))
            contentAttachmentActivityResultLauncher.launch(intent)
        }
    }

    override fun onAttachmentsProcessFailed() {
        Toast.makeText(requireContext(), R.string.error_attachment, Toast.LENGTH_SHORT).show()
    }

    override fun onContactAttachmentReady(contactAttachment: ContactAttachment) {
        super.onContactAttachmentReady(contactAttachment)
        val formattedContact = contactAttachment.toHumanReadable()
        messageComposerViewModel.handle(MessageComposerAction.SendMessage(formattedContact, false))
    }

    private fun onViewWidgetsClicked() {
        RoomWidgetsBottomSheet.newInstance().show(childFragmentManager, "ROOM_WIDGETS_BOTTOM_SHEET")
    }

    override fun onTapToReturnToCall() {
        callManager.getCurrentCall()?.let { call ->
            VectorCallActivity.newIntent(
                    context = requireContext(),
                    callId = call.callId,
                    signalingRoomId = call.signalingRoomId,
                    otherUserId = call.mxCall.opponentUserId,
                    isIncomingCall = !call.mxCall.isOutgoing,
                    isVideoCall = call.mxCall.isVideoCall,
                    mode = null
            ).let {
                startActivity(it)
            }
        }
    }

    
    private fun isThreadTimeLine(): Boolean = timelineArgs.threadTimelineArgs?.rootThreadEventId != null

    
    fun getRootThreadEventId(): String? = timelineArgs.threadTimelineArgs?.rootThreadEventId
}

