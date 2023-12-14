package im.vector.app.features.pay4invite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.toast
import im.vector.app.databinding.FragmentPay4InviteBinding
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.utils.bean.UserBasicInfo
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone
import javax.inject.Inject

enum class Pay4InviteDisplayMode(@StringRes val titleRes: Int) {
    
    PEOPLE(R.string.bottom_action_people_x),

    
    ROOMS(R.string.bottom_action_rooms),

    
    NONE(R.string.bottom_action_none),
}


@Parcelize
data class Pay4InviteArgs(
        val displayMode: Pay4InviteDisplayMode,
        val userByPhones: List<UserByPhone>
) : Parcelable

const val RESULT_VALUE = "result_value";


class Pay4InviteFragment @Inject constructor(
        private val controller: Pay4InviteController,
        private val session: Session,
) : VectorBaseFragment<FragmentPay4InviteBinding>(),
        Pay4InviteController.Callback {

    private val viewModel: Pay4InviteViewModel by fragmentViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPay4InviteBinding {
        return FragmentPay4InviteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.pay4inviteBack.setOnClickListener {
            vectorBaseActivity.finish()
        }

        views.pay4inviteRv.configureWith(controller, hasFixedSize = true, disableItemAnimation = true)
        controller.callback = this

        views.pay4inviteOkBt.setOnClickListener {
            withState(viewModel) { state ->
                state.userByPhones?.filter { it.can_we_talk }?.map { user ->
                    UserBasicInfo(localpart = user.localpart, servername = user.servername, shouldSendFlowers = user.shouldSendFlowers)
                }?.let { result ->
                    
                    if (state.displayMode == Pay4InviteDisplayMode.PEOPLE) {
                        result.firstOrNull()?.let {
                            val intent = Intent()
                            intent.putExtra(RESULT_VALUE, it)
                            vectorBaseActivity.setResult(Activity.RESULT_OK, intent)
                        }
                    } else if (result.isNotEmpty()) {
                        val intent = Intent()
                        intent.putParcelableArrayListExtra(RESULT_VALUE, result as ArrayList<UserBasicInfo>)
                        vectorBaseActivity.setResult(Activity.RESULT_OK, intent)
                    }
                    vectorBaseActivity.finish()
                }
            }
        }
    }

    override fun payClick(userByPhone: UserByPhone) {
        if (userByPhone.can_we_talk) {
            
            return
        } else if (!userByPhone.can_pay_talk) {
            
            return
        }
        
        withState(viewModel) { state ->
            val address = getAddressByUid(session.myUserId)
            vectorBaseActivity.applicationContext?.takeAs<IApplication>()?.apply {
                getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.showPayDialog(
                        vectorBaseActivity,
                        address,
                        userByPhone.localpart,
                        userByPhone.chat_fee,
                        ""
                ) {
                    if (it.orFalse()) {
                        vectorBaseActivity.toast(getString(R.string.pay4invite_success))
                        
                        viewModel.handle(Pay4InviteAction.CheckUserPayStatus(state.userByPhones, userByPhone))
                    }
                }
            }
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        
        controller.setData(state)

        val sizeNeedPay = state.userByPhones?.filter { it.can_pay_talk }?.size ?: 0

        if (sizeNeedPay == 0) {
            

            state.userByPhones?.filter { it.can_we_talk }?.map { user ->
                UserBasicInfo(localpart = user.localpart, servername = user.servername, shouldSendFlowers = user.shouldSendFlowers)
            }?.let { result ->

                if (state.displayMode == Pay4InviteDisplayMode.PEOPLE) {
                    
                    result.firstOrNull()?.let {
                        val intent = Intent()
                        intent.putExtra(RESULT_VALUE, it)
                        vectorBaseActivity.setResult(Activity.RESULT_OK, intent)
                        vectorBaseActivity.finish()
                    }
                } else  {
                    
                    val intent = Intent()
                    intent.putParcelableArrayListExtra(RESULT_VALUE, result as ArrayList<UserBasicInfo>)
                    vectorBaseActivity.setResult(Activity.RESULT_OK, intent)
                    vectorBaseActivity.finish()
                }
                null
            }
        }

        
        val size = state.userByPhones?.filter {
            it.can_we_talk == true
        }?.size ?: 0
        views.pay4inviteOkBt.isEnabled = size != 0

    }

    override fun onDestroyView() {
        controller.callback = null
        views.pay4inviteRv.cleanup()
        super.onDestroyView()
    }
}
