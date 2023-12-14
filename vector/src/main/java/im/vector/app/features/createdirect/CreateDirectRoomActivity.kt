

package im.vector.app.features.createdirect

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
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
import im.vector.app.core.utils.PERMISSIONS_FOR_TAKING_PHOTO
import im.vector.app.core.utils.checkPermissions
import im.vector.app.core.utils.onPermissionDeniedSnackbar
import im.vector.app.core.utils.registerForPermissionsResult
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.contactsbook.ContactsBookFragment
import im.vector.app.features.pay4invite.Pay4InviteActivity
import im.vector.app.features.pay4invite.Pay4InviteDisplayMode
import im.vector.app.features.pay4invite.RESULT_VALUE
import im.vector.app.features.qrcode.QrCodeScannerEvents
import im.vector.app.features.qrcode.QrCodeScannerFragment
import im.vector.app.features.qrcode.QrCodeScannerViewModel
import im.vector.app.features.qrcode.QrScannerArgs
import im.vector.app.features.userdirectory.PendingSelection
import im.vector.app.features.userdirectory.UserListFragment
import im.vector.app.features.userdirectory.UserListFragmentArgs
import im.vector.app.features.userdirectory.UserListSharedAction
import im.vector.app.features.userdirectory.UserListSharedActionViewModel
import im.vector.app.provide.ChatStatusProvide
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.room.failure.CreateRoomFailure
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.session.utils.bean.UserBasicInfo
import java.net.HttpURLConnection
import javax.inject.Inject

@AndroidEntryPoint
class CreateDirectRoomActivity : SimpleFragmentActivity() {

    private val viewModel: CreateDirectRoomViewModel by viewModel()
    private val qrViewModel: QrCodeScannerViewModel by viewModel()

    private lateinit var sharedActionViewModel: UserListSharedActionViewModel
    @Inject lateinit var errorFormatter: ErrorFormatter
    @Inject lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.StartChat
        views.toolbar.visibility = View.GONE

        sharedActionViewModel = viewModelProvider.get(UserListSharedActionViewModel::class.java)
        sharedActionViewModel
                .stream()
                .onEach { action ->
                    when (action) {
                        UserListSharedAction.Close                 -> finish()
                        UserListSharedAction.GoBack                -> onBackPressed()
                        is UserListSharedAction.OnMenuItemSelected -> onMenuItemSelected(action)
                        UserListSharedAction.OpenPhoneBook         -> openPhoneBook()
                        UserListSharedAction.AddByQrCode           -> openAddByQrCode()
                    }
                }
                .launchIn(lifecycleScope)
        if (isFirstCreation()) {
            addFragment(
                    views.container,
                    UserListFragment::class.java,
                    UserListFragmentArgs(
                            title = getString(R.string.fab_menu_create_chat),
                            menuResId = R.menu.vector_create_direct_room
                    )
            )
        }
        viewModel.onEach(CreateDirectRoomViewState::createAndInviteState) {
            renderCreateAndInviteState(it)
        }

        viewModel.observeViewEvents {
            when (it) {
                CreateDirectRoomViewEvents.InvalidCode -> {
                    Toast.makeText(this, R.string.invalid_qr_code_uri, Toast.LENGTH_SHORT).show()
                    finish()
                }
                CreateDirectRoomViewEvents.DmSelf      -> {
                    Toast.makeText(this, R.string.cannot_dm_self, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is CreateDirectRoomViewEvents.QrId     -> {

                    if (!BuildConfig.NEED_PAY) {
                        return@observeViewEvents
                    }
                    val address = ChatStatusProvide.getAddress(this@CreateDirectRoomActivity)

                    val invitee = it.id;
                    
                    session.getExistingDirectRoomWithUser(invitee)?.let {
                        navigator.openRoom(this, it)
                        return@observeViewEvents
                    }
                    val inviteeAddress = getAddressByUid(it.id);
                    
                    lifecycleScope.launch {
                        val iApplication = this@CreateDirectRoomActivity.application.takeAs<IApplication>() ?: return@launch
                        val signInfo =
                                iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.getSignInfo(this@CreateDirectRoomActivity, address)
                                        ?: return@launch

                        val user = try {
                            session.checkInviteRoomPayStatus(inviteeAddress, signInfo.pub_key, signInfo.query_sign, signInfo.timestamp, signInfo.localpart)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            Toast.makeText(this@CreateDirectRoomActivity, errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        if (user.can_we_talk) {
                            
                            viewModel.handle(
                                    CreateDirectRoomAction.CreateRoomAndInviteSelectedUsers(
                                            setOf(
                                                    PendingSelection.UserPendingSelection(
                                                            User(
                                                                    invitee,
                                                                    user.display_name,
                                                                    user.avatar_url
                                                            )
                                                    )
                                            )
                                    )
                            )
                        } else if (user.can_pay_talk) {
                            
                            val newIntent =
                                    Pay4InviteActivity.newIntent(this@CreateDirectRoomActivity, Pay4InviteDisplayMode.ROOMS, userByPhones = mutableListOf(user))
                            pay4InviteActivityResultLauncher.launch(newIntent)
                        } else {
                            
                            Toast.makeText(this@CreateDirectRoomActivity, getString(R.string.phone_talk_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        qrViewModel.observeViewEvents {
            when (it) {
                is QrCodeScannerEvents.CodeParsed  -> {
                    viewModel.handle(CreateDirectRoomAction.QrScannedAction(it.result))
                }
                is QrCodeScannerEvents.ParseFailed -> {
                    Toast.makeText(this, R.string.qr_code_not_scanned, Toast.LENGTH_SHORT).show()
                    finish()
                }
                else                               -> Unit
            }
        }
    }

    private fun openAddByQrCode() {
        if (checkPermissions(PERMISSIONS_FOR_TAKING_PHOTO, this, permissionCameraLauncher)) {
            val args = QrScannerArgs(showExtraButtons = false, R.string.add_by_qr_code)
            addFragment(views.container, QrCodeScannerFragment::class.java, args)
        }
    }

    private fun openPhoneBook() {
        
        if (checkPermissions(PERMISSIONS_FOR_MEMBERS_SEARCH, this, permissionReadContactLauncher)) {
            addFragmentToBackstack(views.container, ContactsBookFragment::class.java)
        }
    }

    private val permissionReadContactLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            doOnPostResume { addFragmentToBackstack(views.container, ContactsBookFragment::class.java) }
        } else if (deniedPermanently) {
            onPermissionDeniedSnackbar(R.string.permissions_denied_add_contact)
        }
    }

    private val permissionCameraLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            val args = QrScannerArgs(showExtraButtons = false, R.string.add_by_qr_code)
            addFragment(views.container, QrCodeScannerFragment::class.java, args)
        } else if (deniedPermanently) {
            onPermissionDeniedSnackbar(R.string.permissions_denied_qr_code)
        }
    }

    var clicked = false
    private fun onMenuItemSelected(action: UserListSharedAction.OnMenuItemSelected) {
        if (action.itemId == R.id.action_create_direct_room) {
            if (!BuildConfig.NEED_PAY) {
                viewModel.handle(CreateDirectRoomAction.CreateRoomAndInviteSelectedUsers(action.selections))
                return
            }
            if (clicked) {
                return
            }
            showWaitingView()
            clicked = true

            if (action.selections.size == 1) {
                val selection = action.selections.elementAt(0)
                session.getExistingDirectRoomWithUser(selection.getMxId())?.let {
                    clicked = false
                    hideWaitingView()
                    navigator.openRoom(this, it)
                    return
                }
            }
            
            lifecycleScope.launch {
                val uid = ChatStatusProvide.getAddress(this@CreateDirectRoomActivity)
                val iApplication = this@CreateDirectRoomActivity.application.takeAs<IApplication>() ?: return@launch
                val signInfo =
                        iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.getSignInfo(this@CreateDirectRoomActivity, uid)
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
                    clicked = false
                    hideWaitingView()
                    e.printStackTrace()
                    Toast.makeText(this@CreateDirectRoomActivity, errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val can_we_talk = users.filter { it.can_we_talk }
                
                val can_pay_talk = users.filter { it.can_pay_talk }.isNotEmpty()


                if (can_we_talk.isNotEmpty() && action.selections.size == can_we_talk.size) {
                    
                    viewModel.handle(CreateDirectRoomAction.CreateRoomAndInviteSelectedUsers(action.selections))
                } else if (can_pay_talk) {
                    
                    val newIntent = Pay4InviteActivity.newIntent(this@CreateDirectRoomActivity, Pay4InviteDisplayMode.ROOMS, userByPhones = users)
                    pay4InviteActivityResultLauncher.launch(newIntent)
                } else {
                    
                    Toast.makeText(this@CreateDirectRoomActivity, getString(R.string.phone_talk_error), Toast.LENGTH_SHORT).show()
                }
                clicked = false
                hideWaitingView()
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
            viewModel.handle(CreateDirectRoomAction.CreateRoomAndInviteSelectedUsers(selections))
        }
    }

    private fun renderCreateAndInviteState(state: Async<String>) {
        when (state) {
            is Loading -> renderCreationLoading()
            is Success -> renderCreationSuccess(state())
            is Fail    -> renderCreationFailure(state.error)
            else       -> Unit
        }
    }

    private fun renderCreationLoading() {
        updateWaitingView(WaitingViewData(getString(R.string.creating_direct_room)))
    }

    private fun renderCreationFailure(error: Throwable) {
        hideWaitingView()
        when (error) {
            is CreateRoomFailure.CreatedWithTimeout           -> {
                finish()
            }
            is CreateRoomFailure.CreatedWithFederationFailure -> {
                MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.create_room_federation_error, error.matrixError.message))
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> finish() }
                        .show()
            }
            else                                              -> {
                val message = if (error is Failure.ServerError && error.httpCode == HttpURLConnection.HTTP_INTERNAL_ERROR ) {
                    
                    getString(R.string.create_room_dm_failure)
                } else {
                    errorFormatter.toHumanReadable(error)
                }
                MaterialAlertDialogBuilder(this)
                        .setMessage(message)
                        .setPositiveButton(R.string.ok, null)
                        .show()
            }
        }
    }

    private fun renderCreationSuccess(roomId: String) {
        
        navigator.openRoom(this, roomId)
        finish()
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, CreateDirectRoomActivity::class.java)
        }
    }
}
