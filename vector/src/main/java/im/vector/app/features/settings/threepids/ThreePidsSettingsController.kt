

package im.vector.app.features.settings.threepids

import android.text.InputType
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.core.epoxy.loadingItem
import im.vector.app.core.epoxy.noResultItem
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.extensions.getFormattedValue
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.genericButtonItem
import im.vector.app.core.ui.list.genericFooterItem
import im.vector.app.features.discovery.SettingsEditTextItem
import im.vector.app.features.discovery.settingsContinueCancelItem
import im.vector.app.features.discovery.settingsEditTextItem
import im.vector.app.features.discovery.settingsInfoItem
import im.vector.app.features.discovery.settingsInformationItem
import im.vector.app.features.discovery.settingsSectionTitleItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.identity.ThreePid
import javax.inject.Inject

class ThreePidsSettingsController @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider,
        private val errorFormatter: ErrorFormatter
) : TypedEpoxyController<ThreePidsSettingsViewState>() {

    interface InteractionListener {
        fun addEmail()
        fun addMsisdn()
        fun cancelAdding()
        fun doAddEmail(email: String)
        fun doAddMsisdn(msisdn: String)
        fun submitCode(threePid: ThreePid.Msisdn, code: String)
        fun continueThreePid(threePid: ThreePid)
        fun cancelThreePid(threePid: ThreePid)
        fun deleteThreePid(threePid: ThreePid)
    }

    var interactionListener: InteractionListener? = null

    
    private var currentInputValue = ""

    
    private val currentCodes = mutableMapOf<ThreePid, String>()

    override fun buildModels(data: ThreePidsSettingsViewState?) {
        if (data == null) return
        val host = this

        if (data.uiState is ThreePidsSettingsUiState.Idle) {
            currentInputValue = ""
        }

        when (data.threePids) {
            is Loading -> {
                loadingItem {
                    id("loading")
                    loadingText(host.stringProvider.getString(R.string.loading))
                }
            }
            is Fail    -> {
                genericFooterItem {
                    id("fail")
                    text(data.threePids.error.localizedMessage?.toEpoxyCharSequence())
                }
            }
            is Success -> {
                val dataList = data.threePids.invoke()
                buildThreePids(dataList, data)
            }
            else       -> Unit
        }
    }

    private fun buildThreePids(list: List<ThreePid>, data: ThreePidsSettingsViewState) {
        val host = this
        val splited = list.groupBy { it is ThreePid.Email }
        val emails = splited[true].orEmpty()
        val msisdn = splited[false].orEmpty()

        settingsSectionTitleItem {
            id("email")
            title(host.stringProvider.getString(R.string.settings_emails))
        }

        emails.forEach { buildThreePid("email ", it) }

        
        data.pendingThreePids.invoke()
                ?.filterIsInstance(ThreePid.Email::class.java)
                .orEmpty()
                .let { pendingList ->
                    if (pendingList.isEmpty() && emails.isEmpty()) {
                        noResultItem {
                            id("noEmail")
                            text(host.stringProvider.getString(R.string.settings_emails_empty))
                        }
                    }

                    pendingList.forEach { buildPendingThreePid(data, "p_email ", it) }
                }

        when (data.uiState) {
            ThreePidsSettingsUiState.Idle                 ->
                genericButtonItem {
                    id("addEmail")
                    text(host.stringProvider.getString(R.string.settings_add_email_address))
                    textColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                    buttonClickAction { host.interactionListener?.addEmail() }
                }
            is ThreePidsSettingsUiState.AddingEmail       -> {
                settingsEditTextItem {
                    id("addingEmail")
                    inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    hint(host.stringProvider.getString(R.string.medium_email))
                    if (data.editTextReinitiator.isTrue()) {
                        value("")
                        requestFocus(true)
                    }
                    errorText(data.uiState.error)
                    interactionListener(object : SettingsEditTextItem.Listener {
                        override fun onValidate() {
                            host.interactionListener?.doAddEmail(host.currentInputValue)
                        }

                        override fun onTextChange(text: String) {
                            host.currentInputValue = text
                        }
                    })
                }
                settingsContinueCancelItem {
                    id("contAddingEmail")
                    continueOnClick { host.interactionListener?.doAddEmail(host.currentInputValue) }
                    cancelOnClick { host.interactionListener?.cancelAdding() }
                }
            }
            is ThreePidsSettingsUiState.AddingPhoneNumber -> Unit
        }

        settingsSectionTitleItem {
            id("msisdn")
            title(host.stringProvider.getString(R.string.settings_phone_numbers))
        }

        msisdn.forEach { buildThreePid("msisdn ", it) }

        
        data.pendingThreePids.invoke()
                ?.filterIsInstance(ThreePid.Msisdn::class.java)
                .orEmpty()
                .let { pendingList ->
                    if (pendingList.isEmpty() && msisdn.isEmpty()) {
                        noResultItem {
                            id("noMsisdn")
                            text(host.stringProvider.getString(R.string.settings_phone_number_empty))
                        }
                    }

                    pendingList.forEach { buildPendingThreePid(data, "p_msisdn ", it) }
                }

        when (data.uiState) {
            ThreePidsSettingsUiState.Idle                 ->
                genericButtonItem {
                    id("addMsisdn")
                    text(host.stringProvider.getString(R.string.settings_add_phone_number))
                    textColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                    buttonClickAction { host.interactionListener?.addMsisdn() }
                }
            is ThreePidsSettingsUiState.AddingEmail       -> Unit
            is ThreePidsSettingsUiState.AddingPhoneNumber -> {
                settingsInfoItem {
                    id("addingMsisdnInfo")
                    helperText(host.stringProvider.getString(R.string.login_msisdn_notice))
                }
                settingsEditTextItem {
                    id("addingMsisdn")
                    inputType(InputType.TYPE_CLASS_PHONE)
                    hint(host.stringProvider.getString(R.string.medium_phone_number))
                    if (data.editTextReinitiator.isTrue()) {
                        value("")
                        requestFocus(true)
                    }
                    errorText(data.uiState.error)
                    interactionListener(object : SettingsEditTextItem.Listener {
                        override fun onValidate() {
                            host.interactionListener?.doAddMsisdn(host.currentInputValue)
                        }

                        override fun onTextChange(text: String) {
                            host.currentInputValue = text
                        }
                    })
                }
                settingsContinueCancelItem {
                    id("contAddingMsisdn")
                    continueOnClick { host.interactionListener?.doAddMsisdn(host.currentInputValue) }
                    cancelOnClick { host.interactionListener?.cancelAdding() }
                }
            }
        }
    }

    private fun buildThreePid(idPrefix: String, threePid: ThreePid) {
        val host = this
        threePidItem {
            id(idPrefix + threePid.value)
            
            
            title(threePid.getFormattedValue())
            deleteClickListener { host.interactionListener?.deleteThreePid(threePid) }
        }
    }

    private fun buildPendingThreePid(data: ThreePidsSettingsViewState, idPrefix: String, threePid: ThreePid) {
        val host = this
        threePidItem {
            id(idPrefix + threePid.value)
            
            
            title(threePid.getFormattedValue())
        }

        when (threePid) {
            is ThreePid.Email  -> {
                settingsInformationItem {
                    id("info" + idPrefix + threePid.value)
                    message(host.stringProvider.getString(R.string.account_email_validation_message))
                    textColor(host.colorProvider.getColor(R.color.vector_info_color))
                }
                settingsContinueCancelItem {
                    id("cont" + idPrefix + threePid.value)
                    continueOnClick { host.interactionListener?.continueThreePid(threePid) }
                    cancelOnClick { host.interactionListener?.cancelThreePid(threePid) }
                }
            }
            is ThreePid.Msisdn -> {
                settingsInformationItem {
                    id("info" + idPrefix + threePid.value)
                    message(host.stringProvider.getString(R.string.settings_text_message_sent, threePid.getFormattedValue()))
                    textColor(host.colorProvider.getColor(R.color.vector_info_color))
                }
                settingsEditTextItem {
                    id("msisdnVerification${threePid.value}")
                    inputType(InputType.TYPE_CLASS_NUMBER)
                    hint(host.stringProvider.getString(R.string.settings_text_message_sent_hint))
                    if (data.msisdnValidationReinitiator[threePid]?.isTrue() == true) {
                        value("")
                    }
                    errorText(host.getCodeError(data, threePid))
                    interactionListener(object : SettingsEditTextItem.Listener {
                        override fun onValidate() {
                            host.interactionListener?.submitCode(threePid, host.currentCodes[threePid] ?: "")
                        }

                        override fun onTextChange(text: String) {
                            host.currentCodes[threePid] = text
                        }
                    })
                }
                settingsContinueCancelItem {
                    id("cont" + idPrefix + threePid.value)
                    continueOnClick { host.interactionListener?.submitCode(threePid, host.currentCodes[threePid] ?: "") }
                    cancelOnClick { host.interactionListener?.cancelThreePid(threePid) }
                }
            }
        }
    }

    private fun getCodeError(data: ThreePidsSettingsViewState, threePid: ThreePid.Msisdn): String? {
        val failure = (data.msisdnValidationRequests[threePid.value] as? Fail)?.error ?: return null
        
        
        return if (failure is Failure.ServerError &&
                failure.httpCode == 400 &&
                failure.error.code == MatrixError.M_UNKNOWN) {
            stringProvider.getString(R.string.settings_text_message_sent_wrong_code)
        } else {
            errorFormatter.toHumanReadable(failure)
        }
    }
}
