

package im.vector.app.features.invite

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.SimpleFragmentActivity
import im.vector.app.core.platform.WaitingViewData
import im.vector.app.core.utils.PERMISSIONS_FOR_MEMBERS_SEARCH
import im.vector.app.core.utils.checkPermissions
import im.vector.app.core.utils.onPermissionDeniedSnackbar
import im.vector.app.core.utils.registerForPermissionsResult
import im.vector.app.core.utils.toast
import im.vector.app.features.contactsbook.ContactsBookFragment
import im.vector.app.features.pay4invite.Pay4InviteActivity
import im.vector.app.features.pay4invite.Pay4InviteDisplayMode
import im.vector.app.features.pay4invite.RESULT_VALUE
import im.vector.app.features.userdirectory.PendingSelection
import im.vector.app.features.userdirectory.UserListFragment
import im.vector.app.features.userdirectory.UserListFragmentArgs
import im.vector.app.features.userdirectory.UserListSharedAction
import im.vector.app.features.userdirectory.UserListSharedActionViewModel
import im.vector.app.provide.ChatStatusProvide
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import im.wallet.router.listener.TranslationListener
import im.wallet.router.wallet.IWalletPay
import im.wallet.router.wallet.pojo.DeviceGroupMember
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.session.utils.bean.UserBasicInfo
import java.net.HttpURLConnection
import javax.inject.Inject

@Parcelize
data class InviteUsersToRoomArgs(val roomId: String) : Parcelable

@AndroidEntryPoint
class InviteUsersToRoomActivity : SimpleFragmentActivity() {

    private val viewModel: InviteUsersToRoomViewModel by viewModel()
    private lateinit var sharedActionViewModel: UserListSharedActionViewModel
    @Inject lateinit var errorFormatter: ErrorFormatter
    @Inject lateinit var session: Session
    lateinit var args: InviteUsersToRoomArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views.toolbar.visibility = View.GONE
        args = intent.getParcelableExtra<InviteUsersToRoomArgs>(Mavericks.KEY_ARG)!!
        sharedActionViewModel = viewModelProvider.get(UserListSharedActionViewModel::class.java)
        sharedActionViewModel
                .stream()
                .onEach { sharedAction ->
                    when (sharedAction) {
                        UserListSharedAction.Close                 -> finish()
                        UserListSharedAction.GoBack                -> onBackPressed()
                        is UserListSharedAction.OnMenuItemSelected -> onMenuItemSelected(sharedAction)
                        UserListSharedAction.OpenPhoneBook         -> openPhoneBook()
                        
                        else                                       -> Unit
                    }
                }
                .launchIn(lifecycleScope)
        if (isFirstCreation()) {
            addFragment(
                    views.container,
                    UserListFragment::class.java,
                    UserListFragmentArgs(
                            title = getString(R.string.invite_users_to_room_title),
                            menuResId = R.menu.vector_invite_users_to_room,
                            excludedUserIds = viewModel.getUserIdsOfRoomMembers(),
                            showInviteActions = false
                    )
            )
        }

        viewModel.observeViewEvents { renderInviteEvents(it) }
    }

    private fun onMenuItemSelected(action: UserListSharedAction.OnMenuItemSelected) {
        if (action.itemId == R.id.action_invite_users_to_room_invite) {

            if (!BuildConfig.NEED_PAY) {
                deviceGroup(action)
                return
            }
            
            lifecycleScope.launch {

                val uid = ChatStatusProvide.getAddress(this@InviteUsersToRoomActivity)
                val iApplication = this@InviteUsersToRoomActivity.application.takeAs<IApplication>() ?: return@launch
                val signInfo =
                        iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.getSignInfo(this@InviteUsersToRoomActivity, uid)
                                ?: return@launch
                val users = try {
                    action.selections.map {
                        session.checkInviteRoomPayStatus(
                                getAddressByUid(it.getMxId()),
                                signInfo.pub_key,
                                signInfo.query_sign,
                                signInfo.timestamp,
                                signInfo.localpart
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(this@InviteUsersToRoomActivity, errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                    return@launch
                }

                
                val can_we_talk = users.filter { it.can_we_talk }
                
                val can_pay_talk = users.filter { it.can_pay_talk }.isNotEmpty()

                if (can_we_talk.isNotEmpty() && action.selections.size == can_we_talk.size) {
                    
                    viewModel.handle(InviteUsersToRoomAction.InviteSelectedUsers(action.selections))
                } else if (can_pay_talk) {
                    
                    val newIntent = Pay4InviteActivity.newIntent(this@InviteUsersToRoomActivity, Pay4InviteDisplayMode.ROOMS, userByPhones = users)
                    pay4InviteActivityResultLauncher.launch(newIntent)
                } else {
                    
                    Toast.makeText(this@InviteUsersToRoomActivity, getString(R.string.phone_talk_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deviceGroup(action: UserListSharedAction.OnMenuItemSelected) = withState(viewModel) { state ->
        val summary = state.asyncRoomSummary() ?: return@withState
        if (!summary.isGroup) {
            viewModel.handle(InviteUsersToRoomAction.InviteSelectedUsers(action.selections))
            return@withState
        }
        lifecycleScope.launch {
         runCatching {
                val list = action.selections.map {
                    val userOnline = session.resolveUserOnline(it.getMxId())
                    DeviceGroupMember(userOnline.userId, userOnline.chat_addr)
                }.toList()
                var walletPay: IWalletPay? = null
                applicationContext?.takeAs<IApplication>()?.apply {
                    walletPay = getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay
                }
                
                walletPay?.addDeviceMembers(this@InviteUsersToRoomActivity, session.myOriginUId, args.roomId, list, object : TranslationListener {
                    override fun onFail(errorInfo: String?) {
                    }

                    override fun onTransSuccess() {
                        viewModel.handle(InviteUsersToRoomAction.InviteSelectedUsers(action.selections))
                    }
                })
            }.onFailure {
                this@InviteUsersToRoomActivity.toast(errorFormatter.toHumanReadable(it))
            }
        }
    }

    private val pay4InviteActivityResultLauncher = registerStartForActivityResult { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK && null != data) {
            val info = data.getParcelableArrayListExtra<UserBasicInfo>(RESULT_VALUE)
            info ?: return@registerStartForActivityResult
            
            val selections = mutableSetOf<PendingSelection>()
            info.forEach {
                selections.add(PendingSelection.UserPendingSelection(User(it.userId, shouldSendFlowers = it.shouldSendFlowers)))
            }
            viewModel.handle(InviteUsersToRoomAction.InviteSelectedUsers(selections))
        }
    }

    private fun openPhoneBook() {
        
        if (checkPermissions(PERMISSIONS_FOR_MEMBERS_SEARCH, this, permissionContactLauncher)) {
            addFragmentToBackstack(views.container, ContactsBookFragment::class.java)
        }
    }

    private val permissionContactLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            doOnPostResume { addFragmentToBackstack(views.container, ContactsBookFragment::class.java) }
        } else if (deniedPermanently) {
            onPermissionDeniedSnackbar(R.string.permissions_denied_add_contact)
        }
    }

    private fun renderInviteEvents(viewEvent: InviteUsersToRoomViewEvents) {
        when (viewEvent) {
            is InviteUsersToRoomViewEvents.Loading -> renderInviteLoading()
            is InviteUsersToRoomViewEvents.Success -> renderInvitationSuccess(viewEvent.successMessage)
            is InviteUsersToRoomViewEvents.Failure -> renderInviteFailure(viewEvent.throwable)
        }
    }

    private fun renderInviteLoading() {
        updateWaitingView(WaitingViewData(getString(R.string.inviting_users_to_room)))
    }

    private fun renderInviteFailure(error: Throwable) {
        hideWaitingView()
        val message = if (error is Failure.ServerError && error.httpCode == HttpURLConnection.HTTP_INTERNAL_ERROR ) {
            
            getString(R.string.invite_users_to_room_failure)
        } else {
            errorFormatter.toHumanReadable(error)
        }
        MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show()
    }

    private fun renderInvitationSuccess(successMessage: String) {
        toast(successMessage)
        finish()
    }

    companion object {

        fun getIntent(context: Context, roomId: String): Intent {
            return Intent(context, InviteUsersToRoomActivity::class.java).also {
                it.putExtra(Mavericks.KEY_ARG, InviteUsersToRoomArgs(roomId))
            }
        }
    }
}
