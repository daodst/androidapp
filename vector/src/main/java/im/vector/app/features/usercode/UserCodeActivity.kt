

package im.vector.app.features.usercode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.utils.onPermissionDeniedSnackbar
import im.vector.app.databinding.ActivitySimpleBinding
import im.vector.app.features.matrixto.MatrixToBottomSheet
import im.vector.app.features.pay4invite.Pay4InviteActivity
import im.vector.app.features.pay4invite.Pay4InviteDisplayMode
import im.vector.app.features.pay4invite.RESULT_VALUE
import im.vector.app.features.qrcode.QrCodeScannerEvents
import im.vector.app.features.qrcode.QrCodeScannerFragment
import im.vector.app.features.qrcode.QrCodeScannerViewModel
import im.vector.app.features.qrcode.QrScannerArgs
import im.vector.app.provide.ChatStatusProvide
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.utils.bean.UserBasicInfo
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint class UserCodeActivity : VectorBaseActivity<ActivitySimpleBinding>(), MatrixToBottomSheet.InteractionListener {

    val sharedViewModel: UserCodeSharedViewModel by viewModel()
    private val qrViewModel: QrCodeScannerViewModel by viewModel()

    @Inject lateinit var session: Session
    @Inject lateinit var errorFormatter: ErrorFormatter
    @Parcelize data class Args(
            val userId: String,
            val mode: Int = 0,
    ) : Parcelable

    override fun getBinding() = ActivitySimpleBinding.inflate(layoutInflater)
    override fun getCoordinatorLayout() = views.coordinatorLayout

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (f is MatrixToBottomSheet) {
                f.interactionListener = this@UserCodeActivity
            }
            super.onFragmentResumed(fm, f)
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            if (f is MatrixToBottomSheet) {
                f.interactionListener = null
            }
            super.onFragmentPaused(fm, f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)

        val args = intent.getParcelableExtra<Args>(Mavericks.KEY_ARG)

        if (isFirstCreation()) {
            if (args?.mode == 1) {
                val qrArgs = QrScannerArgs(showExtraButtons = true, R.string.user_code_scan)
                showFragment(QrCodeScannerFragment::class, qrArgs)
            } else {
                
                showFragment(ShowUserCodeFragment::class, Bundle.EMPTY)
            }
        }

        sharedViewModel.onEach(UserCodeState::mode) { mode ->
            when (mode) {
                UserCodeState.Mode.SHOW      -> showFragment(ShowUserCodeFragment::class)
                UserCodeState.Mode.SCAN      -> {
                    val args = QrScannerArgs(showExtraButtons = true, R.string.user_code_scan)
                    showFragment(QrCodeScannerFragment::class, args)
                }
                is UserCodeState.Mode.RESULT -> {
                    showFragment(ShowUserCodeFragment::class)
                    MatrixToBottomSheet.withLink(mode.rawLink).show(supportFragmentManager, "MatrixToBottomSheet")
                }
            }
        }

        sharedViewModel.observeViewEvents {
            when (it) {
                UserCodeShareViewEvents.Dismiss                       -> ActivityCompat.finishAfterTransition(this)
                UserCodeShareViewEvents.ShowWaitingScreen             -> views.simpleActivityWaitingView.isVisible = true
                UserCodeShareViewEvents.HideWaitingScreen             -> views.simpleActivityWaitingView.isVisible = false
                is UserCodeShareViewEvents.ToastMessage               -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                is UserCodeShareViewEvents.NavigateToRoom             -> {
                    navigator.openRoom(this, it.roomId)
                    finish()
                }
                is UserCodeShareViewEvents.CameraPermissionNotGranted -> {
                    if (it.deniedPermanently) {
                        onPermissionDeniedSnackbar(R.string.permissions_denied_qr_code)
                    }
                }
                is UserCodeShareViewEvents.PostMatrixItem             -> {
                    
                    onOpenDmClicked(it.item)
                }
                else                                                  -> {
                }
            }
        }

        qrViewModel.observeViewEvents {
            when (it) {
                is QrCodeScannerEvents.CodeParsed  -> {
                    sharedViewModel.handle(UserCodeActions.DecodedQRCode(it.result))
                }
                QrCodeScannerEvents.SwitchMode     -> {
                    sharedViewModel.handle(UserCodeActions.SwitchMode(UserCodeState.Mode.SHOW))
                }
                is QrCodeScannerEvents.ParseFailed -> {
                    Toast.makeText(this, R.string.qr_code_not_scanned, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    fun onOpenDmClicked(item: MatrixItem) {
        
        val existRoom = session.getExistingDirectRoomWithUser(item.id)
        if (!TextUtils.isEmpty(existRoom)) {
            sharedViewModel.handle(UserCodeActions.StartChattingWithUser(item))
        } else {

            
            lifecycleScope.launch {

                val address = ChatStatusProvide.getAddress(this@UserCodeActivity)

                val iApplication = this@UserCodeActivity.application.takeAs<IApplication>() ?: return@launch
                val signInfo =
                        iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.getSignInfo(this@UserCodeActivity, address)
                                ?: return@launch
                val inviteeAddress = getAddressByUid(item.id);

                val user = try {
                    session.checkInviteRoomPayStatus(inviteeAddress, signInfo.pub_key, signInfo.query_sign, signInfo.timestamp, signInfo.localpart)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(this@UserCodeActivity, errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (user.can_we_talk) {
                    
                    sharedViewModel.handle(UserCodeActions.StartChattingWithUser(item))
                } else if (user.can_pay_talk) {
                    
                    val newIntent =
                            Pay4InviteActivity.newIntent(this@UserCodeActivity, Pay4InviteDisplayMode.PEOPLE, userByPhones = mutableListOf(user))
                    pay4InviteActivityResultLauncher.launch(newIntent)
                } else {
                    
                    Toast.makeText(this@UserCodeActivity, getString(R.string.phone_talk_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val pay4InviteActivityResultLauncher = registerStartForActivityResult { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK && null != data) {
            
            val info = data.getParcelableExtra<UserBasicInfo>(RESULT_VALUE)
            info ?: return@registerStartForActivityResult

            val item = MatrixItem.UserItem(id = info.userId, shouldSendFlowers = info.shouldSendFlowers)
            sharedViewModel.handle(UserCodeActions.StartChattingWithUser(item))
        }
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
    }

    private fun showFragment(fragmentClass: KClass<out Fragment>, params: Parcelable? = null) {
        if (supportFragmentManager.findFragmentByTag(fragmentClass.simpleName) == null) {
            replaceFragment(views.simpleFragmentContainer, fragmentClass.java, params, fragmentClass.simpleName, useCustomAnimation = true)
        }
    }

    override fun mxToBottomSheetNavigateToRoom(roomId: String) {
        navigator.openRoom(this, roomId)
        finish()
    }

    override fun mxToBottomSheetSwitchToSpace(spaceId: String) {}

    override fun onBackPressed() = withState(sharedViewModel) {
        when (it.mode) {
            UserCodeState.Mode.SHOW                               -> super.onBackPressed()
            is UserCodeState.Mode.RESULT, UserCodeState.Mode.SCAN -> {

                val args = intent.getParcelableExtra<Args>(Mavericks.KEY_ARG)
                if(args?.mode==1){
                    super.onBackPressed()
                }else{
                    sharedViewModel.handle(UserCodeActions.SwitchMode(UserCodeState.Mode.SHOW))
                }
            }
        }
    }

    companion object {
        
        fun newIntent(context: Context, userId: String, mode: Int = 0): Intent {
            return Intent(context, UserCodeActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, Args(userId, mode))
            }
        }
        fun newIntent(context: Context, userId: String): Intent {
            return Intent(context, UserCodeActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, Args(userId, 0))
            }
        }
    }
}
