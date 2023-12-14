package im.vector.app.features.roommemberprofile.cus

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import im.vector.app.core.dialogs.ConfirmationDialogBuilder
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.startSharePlainTextIntent
import im.vector.app.databinding.DialogShareQrCodeBinding
import im.vector.app.databinding.FragmentCusRoomMemberProfileBinding
import im.vector.app.features.createdirect.DirectRoomHelper
import im.vector.app.features.crypto.verification.VerificationBottomSheet
import im.vector.app.features.displayname.getBestName
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.detail.RoomDetailPendingAction
import im.vector.app.features.home.room.detail.RoomDetailPendingActionStore
import im.vector.app.features.pay4invite.Pay4InviteActivity
import im.vector.app.features.pay4invite.Pay4InviteDisplayMode
import im.vector.app.features.pay4invite.RESULT_VALUE
import im.vector.app.features.roommemberprofile.RoomMemberProfileAction
import im.vector.app.features.roommemberprofile.RoomMemberProfileArgs
import im.vector.app.features.roommemberprofile.RoomMemberProfileController
import im.vector.app.features.roommemberprofile.RoomMemberProfileViewEvents
import im.vector.app.features.roommemberprofile.RoomMemberProfileViewModel
import im.vector.app.features.roommemberprofile.RoomMemberProfileViewState
import im.vector.app.features.roommemberprofile.powerlevel.EditPowerLevelDialogs
import im.vector.app.features.roommemberprofile.remark.EditRemarkDialog
import im.vector.app.provide.ChatStatusProvide
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.state.isPublic
import org.matrix.android.sdk.api.session.utils.bean.UserBasicInfo
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject


class CusRoomMemberProfileFragment @Inject constructor(
        private val roomMemberProfileController: RoomMemberProfileController,
        private val avatarRenderer: AvatarRenderer,
        private val directRoomHelper: DirectRoomHelper,
        private val stringProvider: StringProvider,
        private val roomDetailPendingActionStore: RoomDetailPendingActionStore,
        private val session: Session)
    : VectorBaseFragment<FragmentCusRoomMemberProfileBinding>() {

    private val fragmentArgs: RoomMemberProfileArgs by args()
    private val viewModel: RoomMemberProfileViewModel by fragmentViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCusRoomMemberProfileBinding {
        return FragmentCusRoomMemberProfileBinding.inflate(inflater, container, false)
    }

    
    private val addToBalckResult = registerStartForActivityResult { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            showLoading(stringProvider.getString(R.string.room_profile_leaving_room))
            withState(viewModel) { state ->
                lifecycleScope.launch {
                    val roomId = try {
                        directRoomHelper.ensureDMExists(state.userId)
                    } catch (failure: Throwable) {
                        showFailure(failure)
                        return@launch
                    }
                    onLeaveRoomClicked(roomId)
                }
            }
        }
    }

    
    fun onLeaveRoomClicked(roomId: String) {
        val room = session.getRoom(roomId)!!
        val isPublicRoom = room.isPublic();
        val count = room.roomSummary()?.joinedMembersCount ?: 0
        val message = buildString {
            append(getString(R.string.room_participants_leave_prompt_msg))
            if (!isPublicRoom) {
                append("\n\n")
                append(getString(R.string.room_participants_leave_private_warning))
            }
        }
        MaterialAlertDialogBuilder(requireContext(), if (isPublicRoom) 0 else R.style.ThemeOverlay_Vector_MaterialAlertDialog_Destructive)
                .setTitle(R.string.room_participants_leave_prompt_title)
                .setMessage(message)
                .setPositiveButton(R.string.action_leave) { _, _ ->
                    lifecycleScope.launch {
                        try {
                            session.leaveRoom(roomId);
                        } catch (e: Throwable) {
                            showFailure(e)
                        }
                    }
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        views.imWalletProfileWhiteListParent.setOnClickListener {
            
            vectorBaseActivity.applicationContext?.takeAs<IApplication>()?.apply {
                val mobile = views.imWalletProfilePhone.text.toString()
                val intent = getDelegate(ApplicationDelegate.MOODLE_TYPE_APP)?.appNotice?.goWhiteListAcitivty(
                        vectorBaseActivity,
                        fragmentArgs.userId,
                        mobile,
                )
                startActivity(intent)
            }
        }
        views.imWalletProfileBlackListParent.setOnClickListener {
            
            vectorBaseActivity.applicationContext?.takeAs<IApplication>()?.apply {
                val mobile = views.imWalletProfilePhone.text.toString()
                val intent = getDelegate(ApplicationDelegate.MOODLE_TYPE_APP)?.appNotice?.goBlackListAcitivty(
                        vectorBaseActivity,
                        fragmentArgs.userId,
                        mobile,
                )
                addToBalckResult.launch(intent)
            }

        }

        lifecycleScope.launch {
            val user = runCatching {
                session.resolveUserOnline(fragmentArgs.userId)
            }.fold({
                it
            }, {
                if (it is Failure.ServerError) {
                    Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
                    return@launch
                }
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                return@launch
            })
            val phone = user.tel_numbers?.get(0)?.toBigDecimal()?.toPlainString() ?: ""
            views.imWalletProfilePhone.text = phone
            val address = getAddressByUid(fragmentArgs.userId)
            views.imWalletProfileAddress.text = address
            views.imWalletProfileAddress.setOnClickListener {
                val clipService = vectorBaseActivity.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                clipService?.setPrimaryClip(ClipData.newPlainText("", address))
                Toast.makeText(vectorBaseActivity, R.string.action_copy, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            val uid = getAddressByUid(fragmentArgs.userId)
            if (TextUtils.isEmpty(uid)) {
                return@launch
            }
            val level = runCatching {
                session.getLevel(uid)
            }.fold({
                it
            }, {
                return@launch
            })
            views.imWalletProfileRank1.text = "LV.${level}"
            views.imWalletProfileRank.text = "LV.${level}"
            views.imWalletProfileRank.visibility = View.VISIBLE
            views.imWalletProfileRankLogo.setImageResource(getLevelDrawableRes(level))
            if (level == 0) {
                views.imWalletProfileRankContent.text = getString(R.string.im_wallet_profile_rank_content_empty)
            } else {
                views.imWalletProfileRankContent.text = getString(R.string.im_wallet_profile_rank_content)
            }
        }
        views.imWalletProfileBack.setOnClickListener {
            vectorBaseActivity.finish()
        }
        views.imWalletProfileShare.setOnClickListener {
            viewModel.handle(RoomMemberProfileAction.ShareRoomMemberProfile)
        }

        views.imWalletProfileAtParent.setOnClickListener {
            onMentionClicked()
        }
        views.imWalletProfileReplayParent.setOnClickListener {
            onJumpToReadReceiptClicked()
        }
        views.imWalletProfileIgnoreParent.setOnClickListener {
            onIgnoreClicked()
        }
        views.imWalletProfileMsgParent.setOnClickListener {
            onOpenDmClicked()
        }
        views.imWalletProfileRemarkParent.setOnClickListener {
            showEditRemarkDialog()
        }
        
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
                is RoomMemberProfileViewEvents.SaveRemarkSuccess           -> handleEditRemarkSuccess()
            }
        }
    }

    fun getLevelDrawableRes(level: Int): Int {
        val resName = "sm_pledge$level"
        val packageName = vectorBaseActivity.packageName
        return vectorBaseActivity.resources.getIdentifier(resName, "mipmap", packageName)
    }

    fun onMentionClicked() {
        roomDetailPendingActionStore.data = RoomDetailPendingAction.MentionUser(fragmentArgs.userId)
        vectorBaseActivity.finish()
    }

    fun onIgnoreClicked() = withState(viewModel) { state ->
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
        ConfirmationDialogBuilder.show(
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

    fun onJumpToReadReceiptClicked() {
        roomDetailPendingActionStore.data = RoomDetailPendingAction.JumpToReadReceipt(fragmentArgs.userId)
        vectorBaseActivity.finish()
    }

    fun onOpenDmClicked() {
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

                val inviteeAddress = getAddressByUid(fragmentArgs.userId);

                val iApplication = vectorBaseActivity.application.takeAs<IApplication>() ?: return@launch
                val signInfo =
                        iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.getSignInfo(vectorBaseActivity, address)
                                ?: return@launch

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

    override fun invalidate() = withState(viewModel) { state ->
        when (val asyncUserMatrixItem = state.userMatrixItem) {
            Uninitialized, is Loading -> {
                avatarRenderer.render(MatrixItem.UserItem(state.userId, null, null), views.imWalletProfileIco)
                views.imWalletProfileAddress.text = getAddressByUid(state.userId)
            }
            is Fail                   -> {
                avatarRenderer.render(MatrixItem.UserItem(state.userId, null, null), views.imWalletProfileIco)
                views.imWalletProfileAddress.text = getAddressByUid(state.userId)
            }
            is Success                -> {
                val userMatrixItem = asyncUserMatrixItem()
                val bestName = userMatrixItem.getBestName()
                views.imWalletProfileAddress.text = getAddressByUid(userMatrixItem.id)

                avatarRenderer.render(userMatrixItem, views.imWalletProfileIco)
                views.imWalletProfileIco.setOnClickListener { view ->
                    onAvatarClicked(view, userMatrixItem)
                }

                if (state.isMine || null == state.asyncMembership()) {
                    return@withState
                }

                
                views.imWalletProfileMsgParentLine.visibility = View.VISIBLE
                views.imWalletProfileMsgParent.visibility = View.VISIBLE

                views.imWalletProfileRemarkParent.visibility = View.VISIBLE
                views.imWalletProfileRemarkParentLine.visibility = View.VISIBLE
                views.imWalletProfileRemark.text = state.remark;
                views.imWalletProfileName.text = if (state.remark.isEmpty()) bestName else state.remark
                
                if (!state.isSpace && state.hasReadReceipt) {
                    views.imWalletProfileReplayParentLine.visibility = View.VISIBLE
                    views.imWalletProfileReplayParent.visibility = View.VISIBLE
                } else {
                    views.imWalletProfileReplayParentLine.visibility = View.GONE
                    views.imWalletProfileReplayParent.visibility = View.GONE
                }

                
                if (!state.isSpace) {
                    views.imWalletProfileAtParentLine.visibility = View.VISIBLE
                    views.imWalletProfileAtParent.visibility = View.VISIBLE
                } else {
                    views.imWalletProfileAtParentLine.visibility = View.GONE
                    views.imWalletProfileAtParent.visibility = View.GONE
                }

                val isIgnored = state.isIgnored()
                if (isIgnored != null) {
                    views.imWalletProfileIgnoreLine.visibility = View.VISIBLE
                    views.imWalletProfileIgnoreParent.visibility = View.VISIBLE
                } else {
                    views.imWalletProfileIgnoreLine.visibility = View.GONE
                    views.imWalletProfileIgnoreParent.visibility = View.GONE
                }
                

                
                ban(state)
            }
        }
        roomMemberProfileController.setData(state)
    }

    private fun ban(state: RoomMemberProfileViewState) {
        views.roomParticipantsActionRemoveParent.visibility = View.GONE
        views.roomParticipantsActionRemoveLine.visibility = View.GONE
        views.roomParticipantsActionBanParent.visibility = View.GONE
        views.roomParticipantsActionBanLine.visibility = View.GONE

        val powerLevelsContent = state.powerLevelsContent ?: return
        val powerLevelsHelper = PowerLevelsHelper(powerLevelsContent)
        val userPowerLevel = powerLevelsHelper.getUserRole(state.userId)
        val myPowerLevel = powerLevelsHelper.getUserRole(session.myUserId)
        if ((!state.isMine && myPowerLevel <= userPowerLevel)) {
            return
        }

        val canKick = state.actionPermissions.canKick

        views.roomParticipantsActionRemoveParent.visibility = if (canKick) View.VISIBLE else View.GONE
        views.roomParticipantsActionRemoveLine.visibility = if (canKick) View.VISIBLE else View.GONE


        views.roomParticipantsActionRemoveParent.setOnClickListener {
            
            onKickClicked(state.isSpace)
        }
        
        val canBan = state.actionPermissions.canBan
        val membership = state.asyncMembership()

        views.roomParticipantsActionBanParent.visibility = if (canBan) View.VISIBLE else View.GONE
        views.roomParticipantsActionBanLine.visibility = if (canBan) View.VISIBLE else View.GONE
        if (canBan) {
            val banActionTitle = if (membership == Membership.BAN) {
                stringProvider.getString(R.string.room_participants_action_unban)
            } else {
                stringProvider.getString(R.string.room_participants_action_ban)
            }
            views.roomParticipantsActionBan.text = banActionTitle
        }
        views.roomParticipantsActionBanParent.setOnClickListener {
            
            onBanClicked(state.isSpace, membership == Membership.BAN)

        }
    }

    fun onBanClicked(isSpace: Boolean, isUserBanned: Boolean) {
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

    fun onKickClicked(isSpace: Boolean) {
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

    private fun onAvatarClicked(view: View, userMatrixItem: MatrixItem) {
        navigator.openBigImageViewer(requireActivity(), view, userMatrixItem)
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

    private fun handleShowPowerLevelAdminWarning(event: RoomMemberProfileViewEvents.ShowPowerLevelValidation) {
        EditPowerLevelDialogs.showValidation(requireActivity()) {
            viewModel.handle(RoomMemberProfileAction.SetPowerLevel(event.currentValue, event.newValue, false))
        }
    }

    private fun handleShowPowerLevelDemoteWarning(event: RoomMemberProfileViewEvents.ShowPowerLevelDemoteWarning) {
        EditPowerLevelDialogs.showDemoteWarning(requireActivity()) {
            viewModel.handle(RoomMemberProfileAction.SetPowerLevel(event.currentValue, event.newValue, false))
        }
    }

    private fun handleOpenRoom(event: RoomMemberProfileViewEvents.OpenRoom) {
        if (event.isNew) {
            navigator.openRoom(requireContext(), event.roomId, null)
        } else {
            dismissLoadingDialog()
            activity?.finish()
        }
    }

    fun showEditRemarkDialog() {
        EditRemarkDialog().showChoice(requireActivity(), views.imWalletProfileRemark.text.toString()) {
            viewModel.handleRemarkModifyed(it)
        }
    }

    fun handleEditRemarkSuccess() {
    }
}








