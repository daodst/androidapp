

package im.vector.app.features.roommemberprofile

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.animations.AppBarStateChangeListener
import im.vector.app.core.animations.MatrixItemAppBarStateChangeListener
import im.vector.app.core.dialogs.ConfirmationDialogBuilder
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.extensions.copyOnLongClick
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.setTextOrHide
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.StateView
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.startSharePlainTextIntent
import im.vector.app.databinding.DialogBaseEditTextBinding
import im.vector.app.databinding.DialogShareQrCodeBinding
import im.vector.app.databinding.FragmentMatrixProfileBinding
import im.vector.app.databinding.ViewStubRoomMemberProfileHeaderBinding
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.crypto.verification.VerificationBottomSheet
import im.vector.app.features.displayname.getBestName
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.RoomListDisplayMode
import im.vector.app.features.home.room.detail.RoomDetailPendingAction
import im.vector.app.features.home.room.detail.RoomDetailPendingActionStore
import im.vector.app.features.home.room.detail.timeline.helper.MatrixItemColorProvider
import im.vector.app.features.pay4invite.Pay4InviteActivity
import im.vector.app.features.pay4invite.Pay4InviteDisplayMode
import im.vector.app.features.pay4invite.RESULT_VALUE
import im.vector.app.features.roommemberprofile.devices.DeviceListBottomSheet
import im.vector.app.features.roommemberprofile.powerlevel.EditPowerLevelDialogs
import im.vector.app.provide.ChatStatusProvide
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.session.utils.bean.UserBasicInfo
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject

@Parcelize
data class RoomMemberProfileArgs(
        val userId: String,
        val roomId: String? = null,
        val displayMode: RoomListDisplayMode = RoomListDisplayMode.FILTERED
) : Parcelable

class RoomMemberProfileFragment @Inject constructor(
        private val roomMemberProfileController: RoomMemberProfileController,
        private val avatarRenderer: AvatarRenderer,
        private val roomDetailPendingActionStore: RoomDetailPendingActionStore,
        private val matrixItemColorProvider: MatrixItemColorProvider,
        private val session: Session
) : VectorBaseFragment<FragmentMatrixProfileBinding>(),
        RoomMemberProfileController.Callback {

    private lateinit var headerViews: ViewStubRoomMemberProfileHeaderBinding

    private val fragmentArgs: RoomMemberProfileArgs by args()
    private val viewModel: RoomMemberProfileViewModel by fragmentViewModel()

    private var appBarStateChangeListener: AppBarStateChangeListener? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMatrixProfileBinding {
        return FragmentMatrixProfileBinding.inflate(inflater, container, false)
    }

    override fun getMenuRes() = R.menu.vector_room_member_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.User
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(views.matrixProfileToolbar)
                .allowBack()
        val headerView = views.matrixProfileHeaderView.let {
            it.layoutResource = R.layout.view_stub_room_member_profile_header
            it.inflate()
        }
        headerViews = ViewStubRoomMemberProfileHeaderBinding.bind(headerView)
        headerViews.memberProfileStateView.eventCallback = object : StateView.EventCallback {
            override fun onRetryClicked() {
                viewModel.handle(RoomMemberProfileAction.RetryFetchingInfo)
            }
        }
        headerViews.memberProfileStateView.contentView = headerViews.memberProfileInfoContainer
        views.matrixProfileRecyclerView.configureWith(roomMemberProfileController, hasFixedSize = true, disableItemAnimation = true)
        roomMemberProfileController.callback = this
        appBarStateChangeListener = MatrixItemAppBarStateChangeListener(
                headerView,
                listOf(
                        views.matrixProfileToolbarAvatarImageView,
                        views.matrixProfileToolbarTitleView,
                        views.matrixProfileDecorationToolbarAvatarImageView
                )
        )
        views.matrixProfileAppBarLayout.addOnOffsetChangedListener(appBarStateChangeListener)
        viewModel.observeViewEvents {
            when (it) {
                is RoomMemberProfileViewEvents.Loading                     -> showLoading(it.message)
                is RoomMemberProfileViewEvents.Failure                     -> showFailure(it.throwable)
                is RoomMemberProfileViewEvents.StartVerification           -> handleStartVerification(it)
                is RoomMemberProfileViewEvents.ShareRoomMemberProfile      -> handleShareRoomMemberProfile(it.permalink)
                is RoomMemberProfileViewEvents.ShowPowerLevelValidation    -> handleShowPowerLevelAdminWarning(it)
                is RoomMemberProfileViewEvents.ShowPowerLevelDemoteWarning -> handleShowPowerLevelDemoteWarning(it)
                is RoomMemberProfileViewEvents.OpenRoom                    -> handleOpenRoom(it)
                is RoomMemberProfileViewEvents.OnKickActionSuccess         -> Unit
                is RoomMemberProfileViewEvents.OnSetPowerLevelSuccess      -> Unit
                is RoomMemberProfileViewEvents.OnBanActionSuccess          -> Unit
                is RoomMemberProfileViewEvents.OnIgnoreActionSuccess       -> Unit
                is RoomMemberProfileViewEvents.OnInviteActionSuccess       -> Unit
            }
        }
        setupLongClicks()
    }

    private fun setupLongClicks() {
        headerViews.memberProfileNameView.copyOnLongClick()
        headerViews.memberProfileIdView.copyOnLongClick()
    }

    private fun handleOpenRoom(event: RoomMemberProfileViewEvents.OpenRoom) {
        navigator.openRoom(requireContext(), event.roomId, null)
    }

    private fun handleShowPowerLevelDemoteWarning(event: RoomMemberProfileViewEvents.ShowPowerLevelDemoteWarning) {
        EditPowerLevelDialogs.showDemoteWarning(requireActivity()) {
            viewModel.handle(RoomMemberProfileAction.SetPowerLevel(event.currentValue, event.newValue, false))
        }
    }

    private fun handleShowPowerLevelAdminWarning(event: RoomMemberProfileViewEvents.ShowPowerLevelValidation) {
        EditPowerLevelDialogs.showValidation(requireActivity()) {
            viewModel.handle(RoomMemberProfileAction.SetPowerLevel(event.currentValue, event.newValue, false))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.roomMemberProfileShareAction -> {
                viewModel.handle(RoomMemberProfileAction.ShareRoomMemberProfile)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleStartVerification(startVerification: RoomMemberProfileViewEvents.StartVerification) {
        if (startVerification.canCrossSign) {
            VerificationBottomSheet
                    .withArgs(roomId = null, otherUserId = startVerification.userId)
                    .show(parentFragmentManager, "VERIF")
        } else {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.dialog_title_warning)
                    .setMessage(R.string.verify_cannot_cross_sign)
                    .setPositiveButton(R.string.verification_profile_verify) { _, _ ->
                        VerificationBottomSheet
                                .withArgs(roomId = null, otherUserId = startVerification.userId)
                                .show(parentFragmentManager, "VERIF")
                    }
                    .setNegativeButton(R.string.action_cancel, null)
                    .show()
        }
    }

    override fun onDestroyView() {
        views.matrixProfileAppBarLayout.removeOnOffsetChangedListener(appBarStateChangeListener)
        roomMemberProfileController.callback = null
        appBarStateChangeListener = null
        views.matrixProfileRecyclerView.cleanup()
        super.onDestroyView()
    }

    override fun invalidate() = withState(viewModel) { state ->
        when (val asyncUserMatrixItem = state.userMatrixItem) {
            Uninitialized,
            is Loading -> {
                views.matrixProfileToolbarTitleView.text = state.userId
                avatarRenderer.render(MatrixItem.UserItem(state.userId, null, null), views.matrixProfileToolbarAvatarImageView)
                headerViews.memberProfileStateView.state = StateView.State.Loading
            }
            is Fail    -> {
                avatarRenderer.render(MatrixItem.UserItem(state.userId, null, null), views.matrixProfileToolbarAvatarImageView)
                views.matrixProfileToolbarTitleView.text = state.userId
                val failureMessage = errorFormatter.toHumanReadable(asyncUserMatrixItem.error)
                headerViews.memberProfileStateView.state = StateView.State.Error(failureMessage)
            }
            is Success -> {
                val userMatrixItem = asyncUserMatrixItem()
                headerViews.memberProfileStateView.state = StateView.State.Content
                headerViews.memberProfileIdView.text = userMatrixItem.id
                val bestName = userMatrixItem.getBestName()
                headerViews.memberProfileNameView.text = bestName
                headerViews.memberProfileNameView.setTextColor(matrixItemColorProvider.getColor(userMatrixItem))
                views.matrixProfileToolbarTitleView.text = bestName
                avatarRenderer.render(userMatrixItem, headerViews.memberProfileAvatarView)
                avatarRenderer.render(userMatrixItem, views.matrixProfileToolbarAvatarImageView)

                if (state.isRoomEncrypted) {
                    headerViews.memberProfileDecorationImageView.isVisible = true
                    val trustLevel = if (state.userMXCrossSigningInfo != null) {
                        
                        if (state.userMXCrossSigningInfo.isTrusted()) {
                            
                            if (state.allDevicesAreCrossSignedTrusted) {
                                RoomEncryptionTrustLevel.Trusted
                            } else {
                                RoomEncryptionTrustLevel.Warning
                            }
                        } else {
                            RoomEncryptionTrustLevel.Default
                        }
                    } else {
                        
                        if (state.allDevicesAreTrusted) {
                            RoomEncryptionTrustLevel.Trusted
                        } else {
                            RoomEncryptionTrustLevel.Warning
                        }
                    }
                    headerViews.memberProfileDecorationImageView.render(trustLevel)
                    views.matrixProfileDecorationToolbarAvatarImageView.render(trustLevel)
                } else {
                    headerViews.memberProfileDecorationImageView.isVisible = false
                }

                headerViews.memberProfileAvatarView.setOnClickListener { view ->
                    onAvatarClicked(view, userMatrixItem)
                }
                views.matrixProfileToolbarAvatarImageView.setOnClickListener { view ->
                    onAvatarClicked(view, userMatrixItem)
                }
            }
        }
        headerViews.memberProfilePowerLevelView.setTextOrHide(state.userPowerLevelString())
        roomMemberProfileController.setData(state)
    }

    

    override fun onIgnoreClicked() = withState(viewModel) { state ->
        val isIgnored = state.isIgnored() ?: false
        val titleRes: Int
        val positiveButtonRes: Int
        val confirmationRes: Int
        if (isIgnored) {
            confirmationRes = R.string.room_participants_action_unignore_prompt_msg
            titleRes = R.string.room_participants_action_unignore_title
            positiveButtonRes = R.string.room_participants_action_unignore
        } else {
            confirmationRes = R.string.room_participants_action_ignore_prompt_msg
            titleRes = R.string.room_participants_action_ignore_title
            positiveButtonRes = R.string.room_participants_action_ignore
        }
        ConfirmationDialogBuilder
                .show(
                        activity = requireActivity(),
                        askForReason = false,
                        confirmationRes = confirmationRes,
                        positiveRes = positiveButtonRes,
                        reasonHintRes = 0,
                        titleRes = titleRes
                ) {
                    viewModel.handle(RoomMemberProfileAction.IgnoreUser)
                }
    }

    override fun onTapVerify() {
        viewModel.handle(RoomMemberProfileAction.VerifyUser)
    }

    override fun onShowDeviceList() = withState(viewModel) {
        DeviceListBottomSheet.newInstance(it.userId).show(parentFragmentManager, "DEV_LIST")
    }

    override fun onShowDeviceListNoCrossSigning() = withState(viewModel) {
        DeviceListBottomSheet.newInstance(it.userId).show(parentFragmentManager, "DEV_LIST")
    }

    override fun onOpenDmClicked() {

        if (!BuildConfig.NEED_PAY) {
            viewModel.handle(RoomMemberProfileAction.OpenOrCreateDm(fragmentArgs.userId))
            return
        }
        
        val existRoom = session.getExistingDirectRoomWithUser(fragmentArgs.userId)
        if (!TextUtils.isEmpty(existRoom)) {
            viewModel.handle(RoomMemberProfileAction.OpenOrCreateDm(fragmentArgs.userId))
        } else {
            
            lifecycleScope.launch {
                val address = ChatStatusProvide.getAddress(vectorBaseActivity)
                val iApplication = vectorBaseActivity.application.takeAs<IApplication>() ?: return@launch
                val signInfo =
                        iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.getSignInfo(vectorBaseActivity, address)
                                ?: return@launch

                val inviteeAddress = getAddressByUid(fragmentArgs.userId);

                val user = try {
                    session.checkInviteRoomPayStatus(inviteeAddress, signInfo.pub_key, signInfo.query_sign, signInfo.timestamp, signInfo.localpart)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(vectorBaseActivity, errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (user.can_we_talk) {
                    
                    viewModel.handle(RoomMemberProfileAction.OpenOrCreateDm(fragmentArgs.userId))
                } else if (user.can_pay_talk) {
                    
                    val newIntent =
                            Pay4InviteActivity.newIntent(vectorBaseActivity, Pay4InviteDisplayMode.PEOPLE, userByPhones = mutableListOf(user))
                    pay4InviteActivityResultLauncher.launch(newIntent)
                } else {
                    
                    Toast.makeText(vectorBaseActivity, getString(R.string.phone_talk_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val pay4InviteActivityResultLauncher = registerStartForActivityResult { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK && null != data) {
            
            val info = data.getParcelableExtra<UserBasicInfo>(RESULT_VALUE)
            info ?: return@registerStartForActivityResult
            viewModel.handle(RoomMemberProfileAction.OpenOrCreateDm(fragmentArgs.userId, shouldSendFlowers = info.shouldSendFlowers))
        }
    }

    override fun onJumpToReadReceiptClicked() {
        roomDetailPendingActionStore.data = RoomDetailPendingAction.JumpToReadReceipt(fragmentArgs.userId)
        vectorBaseActivity.finish()
    }

    override fun onMentionClicked() {
        roomDetailPendingActionStore.data = RoomDetailPendingAction.MentionUser(fragmentArgs.userId)
        vectorBaseActivity.finish()
    }

    private fun handleShareRoomMemberProfile(permalink: String) {
        val view = layoutInflater.inflate(R.layout.dialog_share_qr_code, null)
        val views = DialogShareQrCodeBinding.bind(view)
        views.itemShareQrCodeImage.setData(permalink)
        MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setNeutralButton(R.string.ok, null)
                .setPositiveButton(R.string.share_by_text) { _, _ ->
                    startSharePlainTextIntent(
                            fragment = this,
                            activityResultLauncher = null,
                            chooserTitle = null,
                            text = permalink
                    )
                }.show()
    }

    private fun onAvatarClicked(view: View, userMatrixItem: MatrixItem) {
        navigator.openBigImageViewer(requireActivity(), view, userMatrixItem)
    }

    override fun onOverrideColorClicked(): Unit = withState(viewModel) { state ->
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_base_edit_text, null)
        val views = DialogBaseEditTextBinding.bind(layout)
        views.editText.setText(state.userColorOverride)
        views.editText.hint = "#000000"

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.room_member_override_nick_color)
                .setView(layout)
                .setPositiveButton(R.string.ok) { _, _ ->
                    val newColor = views.editText.text.toString()
                    if (newColor != state.userColorOverride) {
                        viewModel.handle(RoomMemberProfileAction.SetUserColorOverride(newColor))
                    }
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
    }

    override fun onEditPowerLevel(currentRole: Role) {
        EditPowerLevelDialogs.showChoice(requireActivity(), R.string.power_level_edit_title, currentRole) { newPowerLevel ->
            viewModel.handle(RoomMemberProfileAction.SetPowerLevel(currentRole.value, newPowerLevel, true))
        }
    }

    override fun onKickClicked(isSpace: Boolean) {
        ConfirmationDialogBuilder
                .show(
                        activity = requireActivity(),
                        askForReason = true,
                        confirmationRes = if (isSpace) R.string.space_participants_remove_prompt_msg
                        else R.string.room_participants_remove_prompt_msg,
                        positiveRes = R.string.room_participants_action_remove,
                        reasonHintRes = R.string.room_participants_remove_reason,
                        titleRes = R.string.room_participants_remove_title
                ) { reason ->
                    viewModel.handle(RoomMemberProfileAction.KickUser(reason))
                }
    }

    override fun onBanClicked(isSpace: Boolean, isUserBanned: Boolean) {
        val titleRes: Int
        val positiveButtonRes: Int
        val confirmationRes: Int
        if (isUserBanned) {
            confirmationRes = if (isSpace) R.string.space_participants_unban_prompt_msg
            else R.string.room_participants_unban_prompt_msg
            titleRes = R.string.room_participants_unban_title
            positiveButtonRes = R.string.room_participants_action_unban
        } else {
            confirmationRes = if (isSpace) R.string.space_participants_ban_prompt_msg
            else R.string.room_participants_ban_prompt_msg
            titleRes = R.string.room_participants_ban_title
            positiveButtonRes = R.string.room_participants_action_ban
        }
        ConfirmationDialogBuilder
                .show(
                        activity = requireActivity(),
                        askForReason = !isUserBanned,
                        confirmationRes = confirmationRes,
                        positiveRes = positiveButtonRes,
                        reasonHintRes = R.string.room_participants_ban_reason,
                        titleRes = titleRes
                ) { reason ->
                    viewModel.handle(RoomMemberProfileAction.BanOrUnbanUser(reason))
                }
    }

    override fun onCancelInviteClicked() {
        ConfirmationDialogBuilder
                .show(
                        activity = requireActivity(),
                        askForReason = false,
                        confirmationRes = R.string.room_participants_action_cancel_invite_prompt_msg,
                        positiveRes = R.string.room_participants_action_cancel_invite,
                        reasonHintRes = 0,
                        titleRes = R.string.room_participants_action_cancel_invite_title
                ) {
                    viewModel.handle(RoomMemberProfileAction.KickUser(null))
                }
    }

    override fun onInviteClicked() {
        viewModel.handle(RoomMemberProfileAction.InviteUser)
    }
}
