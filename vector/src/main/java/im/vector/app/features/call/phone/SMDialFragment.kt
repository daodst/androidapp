
package im.vector.app.features.call.phone

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.TextUtils
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.lxj.xpopup.XPopup
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.extensions.takeAs
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.SmFragmentDialBinding
import im.vector.app.databinding.SmFragmentDialItemBinding
import im.vector.app.features.call.phone.logs.SMDialHistoryActivity
import im.vector.app.features.call.phone.popup.SMPhoneNumberEntity
import im.vector.app.features.call.phone.popup.SMSelectPhonePopup
import im.vector.app.features.createdirect.DirectRoomHelper
import im.vector.app.features.pay4invite.Pay4InviteActivity
import im.vector.app.features.pay4invite.Pay4InviteDisplayMode
import im.vector.app.features.pay4invite.RESULT_VALUE
import im.vector.app.provide.ChatStatusProvide
import im.vector.app.provide.ChatStatusViewModel
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.call.MXCALL_KEY_PHONE
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.utils.bean.UserBasicInfo
import reactivecircus.flowbinding.android.widget.textChanges

const val PHONE_KEY_SUFFIX = "mlist"


@AndroidEntryPoint
class SMDialFragment : VectorBaseFragmentHost<SmFragmentDialBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): SmFragmentDialBinding {
        return SmFragmentDialBinding.inflate(inflater, container, false)
    }

    private lateinit var chatStatusViewModel: ChatStatusViewModel

    
    private val cursorVisible = true
    private var popup: SMSelectPhonePopup? = null

    lateinit var directRoomHelper: DirectRoomHelper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        chatStatusViewModel = ViewModelProviders.of(this).get(ChatStatusViewModel::class.java)
        getDirectRoomHelper()
    }

    private fun getDirectRoomHelper(): Boolean {
        if (this::directRoomHelper.isInitialized) {
            return true
        }
        
        return if (ChatStatusProvide.loginStatus(requireContext())) {
            directRoomHelper = requireActivity().singletonEntryPoint().directRoomHelper()
            true
        } else {
            false
        }
    }

    var entity: MutableList<SMPhoneNumberEntity>? = null

    override fun onResume() {
        super.onResume()

        val loginStatus = ChatStatusProvide.loginStatus(requireContext())
        if (loginStatus) {
            val activeSession = requireActivity().singletonEntryPoint().activeSessionHolder().getActiveSession()
            val myUserId = getAddressByUid(activeSession.myUserId)
            val decodeStringSet = MMKV.defaultMMKV()?.decodeStringSet("${myUserId}$PHONE_KEY_SUFFIX")
            val phoneList = decodeStringSet?.toList()

            val myPhone = MMKV.defaultMMKV()?.getString(myUserId + MXCALL_KEY_PHONE, "")
            views.tvHistory.text = myPhone
            entity = ArrayList()
            if (phoneList != null) {
                for (value in phoneList) {
                    entity?.add(SMPhoneNumberEntity(value, TextUtils.equals(myPhone, value)))
                }
            }
            popup?.setList(entity)
        }
    }

    private val phoneLog4Phone = registerStartForActivityResult { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK && null != data) {
            val stringExtra = data.getStringExtra(RESULT_VALUE)
            if (TextUtils.isEmpty(stringExtra)) {
                return@registerStartForActivityResult
            }
            views.etInputPhoneNumber.setText(stringExtra)
        }
    }

    fun initView() {

        views.etInputPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        views.includedDialpad.bind()
        
        views.ivDeletedButton.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_DEL
            )
        }
        views.imgHistory.setOnClickListener {
            phoneLog4Phone.launch(Intent(context, SMDialHistoryActivity::class.java))
        }
        views.ivDeletedButton.setOnLongClickListener {
            views.etInputPhoneNumber.setText("")
            true
        }

        
        views.ivDialButton.setOnClickListener {
            showLoading("")
            views.ivDialButton.isEnabled = false
            val loginStatus = ChatStatusProvide.loginStatus(requireContext())
            if (!loginStatus) {
                dismissLoadingDialog()
                Toast.makeText(requireContext(), getString(R.string.please_login_first), Toast.LENGTH_SHORT).show()
                views.ivDialButton.isEnabled = true
                return@setOnClickListener
            }
            val activeSession = requireActivity().singletonEntryPoint().activeSessionHolder().getActiveSession()
            val myUserId = getAddressByUid(activeSession.myUserId)
            val myPhone = MMKV.defaultMMKV()?.getString(myUserId + MXCALL_KEY_PHONE, "")

            val decodeStringSet = MMKV.defaultMMKV()?.decodeStringSet("${myUserId}$PHONE_KEY_SUFFIX")
            val phoneList = decodeStringSet?.toList() ?: ArrayList()

            if (TextUtils.isEmpty(myPhone)) {
                dismissLoadingDialog()
                Toast.makeText(requireContext(), getString(R.string.please_set_phone), Toast.LENGTH_SHORT).show()
                views.ivDialButton.isEnabled = true
                return@setOnClickListener
            }

            val phoneNumber = views.etInputPhoneNumber.text.toString()
            if (TextUtils.isEmpty(phoneNumber)) {
                dismissLoadingDialog()
                Toast.makeText(requireContext(), getString(R.string.please_input_phone), Toast.LENGTH_SHORT).show()
                views.ivDialButton.isEnabled = true
                return@setOnClickListener
            }

            if (TextUtils.equals(myPhone, phoneNumber)) {
                dismissLoadingDialog()
                Toast.makeText(requireContext(), getString(R.string.un_call_self), Toast.LENGTH_SHORT).show()
                views.ivDialButton.isEnabled = true
                return@setOnClickListener
            }

            for (item in phoneList) {
                if (TextUtils.equals(item, phoneNumber)) {
                    dismissLoadingDialog()
                    Toast.makeText(requireContext(), getString(R.string.un_call_self), Toast.LENGTH_SHORT).show()
                    views.ivDialButton.isEnabled = true
                    return@setOnClickListener
                }
            }
            views.ivDialButton.isEnabled = true
            
            if (!getDirectRoomHelper()) {
                return@setOnClickListener
            }


            lifecycleScope.launch {
                val users = try {
                    val iApplication = vectorBaseActivity.applicationContext?.takeAs<IApplication>() ?: return@launch
                    val signInfo = iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET)?.walletPay?.getSignInfo(
                            vectorBaseActivity,
                            ChatStatusProvide.getAddress(vectorBaseActivity)
                    )

                    if (null == signInfo) {
                        dismissLoadingDialog()
                        return@launch
                    }
                    activeSession.searchUserByPhone(phoneNumber, signInfo.pub_key, signInfo.query_sign, signInfo.timestamp, signInfo.localpart)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    dismissLoadingDialog()
                    Toast.makeText(requireContext(), errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val existRoom = ChatStatusProvide.existRoom(requireContext(), users.userId)
                if (!TextUtils.isEmpty(existRoom)) {
                    dismissLoadingDialog()
                    chatStatusViewModel.startOutgoingCallInner(requireContext(), existRoom!!, users.userId, false, phoneNumber)
                } else {
                    if (users.can_we_talk) {
                        
                        try {
                            val ensureDMExists = directRoomHelper.ensureDMExists(users.userId)
                            dismissLoadingDialog()
                            chatStatusViewModel.startOutgoingCallInner(requireContext(), ensureDMExists, users.userId, false, phoneNumber)
                        } catch (e: Throwable) {
                            Toast.makeText(requireContext(), errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                        }
                    } else if (users.can_pay_talk) {
                        
                        Toast.makeText(requireContext(), getString(R.string.can_pay_talk), Toast.LENGTH_SHORT).show()
                        dismissLoadingDialog()
                        val newIntent = Pay4InviteActivity.newIntent(vectorBaseActivity, Pay4InviteDisplayMode.PEOPLE, mutableListOf(users))
                        pay4InviteActivityResultLauncher.launch(newIntent)
                    } else {
                        dismissLoadingDialog()
                        
                        Toast.makeText(requireContext(), getString(R.string.phone_talk_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        
        views.tvHistory.setOnClickListener {
            
            if (null == popup) popup = SMSelectPhonePopup(requireContext(), null) { _: Int, text: String? ->
                views.tvHistory.text = text
                val loginStatus = ChatStatusProvide.loginStatus(requireContext())
                if (loginStatus) {
                    val activeSession = requireActivity().singletonEntryPoint().activeSessionHolder().getActiveSession()
                    val myUserId = getAddressByUid(activeSession.myUserId)
                    MMKV.defaultMMKV()?.putString(myUserId + MXCALL_KEY_PHONE, text)
                }
            }
            popup?.setList(entity)
            XPopup.Builder(context).hasShadowBg(false).isCenterHorizontal(true)
                    .atView(views.tvHistory).asCustom(popup).show()
        }

        setupSearchView()
    }

    private fun setupSearchView() {

        views.etInputPhoneNumber.textChanges()
                .onStart { views.etInputPhoneNumber.text?.let { emit(it) } }
                .onEach { text ->
                    val searchValue = text.trim()
                    if (searchValue.isBlank()) {
                        views.tvPhoneName.text = ""
                    } else {
                        
                        val session = requireActivity().singletonEntryPoint().activeSessionHolder().getActiveSession()
                        session.getPagedUsersLive(searchValue.toString(), null).observe(vectorBaseActivity) {
                            it.forEach { user ->
                                val firstOrNull = user.tel_numbers?.firstOrNull {
                                    it.equals(searchValue.toString())
                                }

                                firstOrNull?.let {
                                    views.tvPhoneName.text = user.displayName
                                }
                                if (null == firstOrNull) {
                                    views.tvPhoneName.text = ""
                                }
                            }
                        }
                    }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private val pay4InviteActivityResultLauncher = registerStartForActivityResult { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK && null != data) {
            val info = data.getParcelableExtra<UserBasicInfo>(RESULT_VALUE)
            info ?: return@registerStartForActivityResult
            showLoading("")
            lifecycleScope.launch {
                
                val phoneNumber = views.etInputPhoneNumber.text.toString()
                try {
                    val ensureDMExists = directRoomHelper.ensureDMExists(info.userId)
                    if (info.shouldSendFlowers) {
                        val activeSession = vectorBaseActivity.singletonEntryPoint().activeSessionHolder().getActiveSession()
                        activeSession.getRoom(ensureDMExists)?.sendGifts()
                    }
                    chatStatusViewModel.startOutgoingCallInner(requireContext(), ensureDMExists, info.userId, false, phoneNumber)
                } catch (e: Throwable) {
                    Toast.makeText(requireContext(), errorFormatter.toHumanReadable(e), Toast.LENGTH_SHORT).show()
                } finally {
                    dismissLoadingDialog()
                }
            }
        }
    }

    
    private fun keyPressed(keyCode: Int) {
        
        views.includedDialpad.llNumber1.performHapticFeedback(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
        val event = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        views.etInputPhoneNumber.isCursorVisible = false
        views.etInputPhoneNumber.onKeyDown(keyCode, event)
        views.etInputPhoneNumber.isCursorVisible = cursorVisible
    }

    companion object {
        private const val TAG = "SMDialFragment"
    }

    
    
    private val enableStar = false
    private val enablePound = false

    
    private val enablePlus = false

    
    fun SmFragmentDialItemBinding.bind() {
        llNumber1.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_1
            )
        }
        llNumber2.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_2
            )
        }
        llNumber3.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_3
            )
        }
        llNumber4.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_4
            )
        }
        llNumber5.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_5
            )
        }
        llNumber6.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_6
            )
        }
        llNumber7.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_7
            )
        }
        llNumber8.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_8
            )
            

        }
        llNumber9.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_9
            )
        }
        llNumber0.setOnClickListener {
            keyPressed(
                    KeyEvent.KEYCODE_0
            )
        }
        if (enableStar) {
            llNumberX.setOnClickListener {
                keyPressed(
                        KeyEvent.KEYCODE_STAR
                )
            }
        }
        if (enablePound) {
            llNumberJ.setOnClickListener {
                keyPressed(
                        KeyEvent.KEYCODE_POUND
                )
            }
        }
        if (enablePlus) {
            llNumber0.setOnLongClickListener {
                keyPressed(KeyEvent.KEYCODE_PLUS)
                true
            }
        }
    }
}
