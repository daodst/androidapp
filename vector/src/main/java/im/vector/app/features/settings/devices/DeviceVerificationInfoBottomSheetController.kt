
package im.vector.app.features.settings.devices

import com.airbnb.epoxy.TypedEpoxyController
import im.vector.app.R
import im.vector.app.core.epoxy.bottomSheetDividerItem
import im.vector.app.core.epoxy.loadingItem
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.ItemStyle
import im.vector.app.core.ui.list.genericFooterItem
import im.vector.app.core.ui.list.genericItem
import im.vector.app.core.ui.views.toDrawableRes
import im.vector.app.features.crypto.verification.epoxy.bottomSheetVerificationActionItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import timber.log.Timber
import javax.inject.Inject

class DeviceVerificationInfoBottomSheetController @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider) :
        TypedEpoxyController<DeviceVerificationInfoBottomSheetViewState>() {

    var callback: Callback? = null

    override fun buildModels(data: DeviceVerificationInfoBottomSheetViewState?) {
        val cryptoDeviceInfo = data?.cryptoDeviceInfo?.invoke()
        when {
            cryptoDeviceInfo != null           -> {
                
                handleE2ECapableDevice(data, cryptoDeviceInfo)
            }
            data?.deviceInfo?.invoke() != null -> {
                
                handleNonE2EDevice(data)
            }
            else                               -> {
                loadingItem {
                    id("loading")
                }
            }
        }
    }

    private fun handleE2ECapableDevice(data: DeviceVerificationInfoBottomSheetViewState, cryptoDeviceInfo: CryptoDeviceInfo) {
        val shield = TrustUtils.shieldForTrust(
                currentDevice = data.isMine,
                trustMSK = data.accountCrossSigningIsTrusted,
                legacyMode = !data.hasAccountCrossSigning,
                deviceTrustLevel = cryptoDeviceInfo.trustLevel
        ).toDrawableRes()

        if (data.hasAccountCrossSigning) {
            
            handleE2EWithCrossSigning(data, cryptoDeviceInfo, shield)
        } else {
            handleE2EInLegacy(data, cryptoDeviceInfo, shield)
        }

        
        addGenericDeviceManageActions(data, cryptoDeviceInfo.deviceId)
    }

    private fun handleE2EWithCrossSigning(data: DeviceVerificationInfoBottomSheetViewState, cryptoDeviceInfo: CryptoDeviceInfo, shield: Int) {
        val isMine = data.isMine
        val currentSessionIsTrusted = data.accountCrossSigningIsTrusted
        Timber.v("handleE2EWithCrossSigning $isMine, $cryptoDeviceInfo, $shield")
        val host = this

        if (isMine) {
            if (currentSessionIsTrusted) {
                genericItem {
                    id("trust${cryptoDeviceInfo.deviceId}")
                    style(ItemStyle.BIG_TEXT)
                    titleIconResourceId(shield)
                    title(host.stringProvider.getString(R.string.encryption_information_verified).toEpoxyCharSequence())
                    description(host.stringProvider.getString(R.string.settings_active_sessions_verified_device_desc).toEpoxyCharSequence())
                }
            } else if (data.canVerifySession) {
                
                genericItem {
                    id("trust${cryptoDeviceInfo.deviceId}")
                    style(ItemStyle.BIG_TEXT)
                    titleIconResourceId(shield)
                    title(host.stringProvider.getString(R.string.crosssigning_verify_this_session).toEpoxyCharSequence())
                    description(host.stringProvider
                            .getString(if (data.hasOtherSessions) R.string.confirm_your_identity else R.string.confirm_your_identity_quad_s)
                            .toEpoxyCharSequence()
                    )
                }
            }
        } else {
            if (!currentSessionIsTrusted) {
                
                
            } else {
                
                val trust = cryptoDeviceInfo.trustLevel?.isCrossSigningVerified() == true
                if (trust) {
                    genericItem {
                        id("trust${cryptoDeviceInfo.deviceId}")
                        style(ItemStyle.BIG_TEXT)
                        titleIconResourceId(shield)
                        title(host.stringProvider.getString(R.string.encryption_information_verified).toEpoxyCharSequence())
                        description(host.stringProvider.getString(R.string.settings_active_sessions_verified_device_desc).toEpoxyCharSequence())
                    }
                } else {
                    genericItem {
                        id("trust${cryptoDeviceInfo.deviceId}")
                        titleIconResourceId(shield)
                        style(ItemStyle.BIG_TEXT)
                        title(host.stringProvider.getString(R.string.encryption_information_not_verified).toEpoxyCharSequence())
                        description(host.stringProvider.getString(R.string.settings_active_sessions_unverified_device_desc).toEpoxyCharSequence())
                    }
                }
            }
        }

        
        genericItem {
            id("info${cryptoDeviceInfo.deviceId}")
            title(cryptoDeviceInfo.displayName().orEmpty().toEpoxyCharSequence())
            description("(${cryptoDeviceInfo.deviceId})".toEpoxyCharSequence())
        }

        if (isMine && !currentSessionIsTrusted && data.canVerifySession) {
            
            bottomSheetDividerItem {
                id("completeSecurityDiv")
            }
            bottomSheetVerificationActionItem {
                id("completeSecurity")
                title(host.stringProvider.getString(R.string.crosssigning_verify_this_session))
                titleColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                iconRes(R.drawable.ic_arrow_right)
                iconColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                listener {
                    host.callback?.onAction(DevicesAction.CompleteSecurity)
                }
            }
        } else if (!isMine) {
            if (currentSessionIsTrusted) {
                
                val isVerified = cryptoDeviceInfo.trustLevel?.crossSigningVerified.orFalse()
                if (!isVerified) {
                    addVerifyActions(cryptoDeviceInfo)
                }
            }
        }
    }

    private fun handleE2EInLegacy(data: DeviceVerificationInfoBottomSheetViewState, cryptoDeviceInfo: CryptoDeviceInfo, shield: Int) {
        val host = this
        
        val isMine = data.isMine

        
        if (cryptoDeviceInfo.trustLevel?.isLocallyVerified() == true) {
            genericItem {
                id("trust${cryptoDeviceInfo.deviceId}")
                style(ItemStyle.BIG_TEXT)
                titleIconResourceId(shield)
                title(host.stringProvider.getString(R.string.encryption_information_verified).toEpoxyCharSequence())
                description(host.stringProvider.getString(R.string.settings_active_sessions_verified_device_desc).toEpoxyCharSequence())
            }
        } else {
            genericItem {
                id("trust${cryptoDeviceInfo.deviceId}")
                titleIconResourceId(shield)
                style(ItemStyle.BIG_TEXT)
                title(host.stringProvider.getString(R.string.encryption_information_not_verified).toEpoxyCharSequence())
                description(host.stringProvider.getString(R.string.settings_active_sessions_unverified_device_desc).toEpoxyCharSequence())
            }
        }

        
        genericItem {
            id("info${cryptoDeviceInfo.deviceId}")
            title(cryptoDeviceInfo.displayName().orEmpty().toEpoxyCharSequence())
            description("(${cryptoDeviceInfo.deviceId})".toEpoxyCharSequence())
        }

        

        if (!isMine) {
            
            bottomSheetDividerItem {
                id("d1")
            }
            bottomSheetVerificationActionItem {
                id("verify${cryptoDeviceInfo.deviceId}")
                title(host.stringProvider.getString(R.string.verification_verify_device))
                titleColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                iconRes(R.drawable.ic_arrow_right)
                iconColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
                listener {
                    host.callback?.onAction(DevicesAction.VerifyMyDevice(cryptoDeviceInfo.deviceId))
                }
            }
        }
    }

    private fun addVerifyActions(cryptoDeviceInfo: CryptoDeviceInfo) {
        val host = this
        bottomSheetDividerItem {
            id("verifyDiv")
        }
        bottomSheetVerificationActionItem {
            id("verify_text")
            title(host.stringProvider.getString(R.string.cross_signing_verify_by_text))
            titleColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
            iconRes(R.drawable.ic_arrow_right)
            iconColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
            listener {
                host.callback?.onAction(DevicesAction.VerifyMyDeviceManually(cryptoDeviceInfo.deviceId))
            }
        }
        bottomSheetDividerItem {
            id("verifyDiv2")
        }
        bottomSheetVerificationActionItem {
            id("verify_emoji")
            title(host.stringProvider.getString(R.string.cross_signing_verify_by_emoji))
            titleColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
            iconRes(R.drawable.ic_arrow_right)
            iconColor(host.colorProvider.getColorFromAttribute(R.attr.colorPrimary))
            listener {
                host.callback?.onAction(DevicesAction.VerifyMyDevice(cryptoDeviceInfo.deviceId))
            }
        }
    }

    private fun addGenericDeviceManageActions(data: DeviceVerificationInfoBottomSheetViewState, deviceId: String) {
        val host = this
        
        if (!data.isMine) {
            
            bottomSheetDividerItem {
                id("manageD1")
            }
            bottomSheetVerificationActionItem {
                id("delete")
                title(host.stringProvider.getString(R.string.settings_active_sessions_signout_device))
                titleColor(host.colorProvider.getColorFromAttribute(R.attr.colorError))
                iconRes(R.drawable.ic_arrow_right)
                iconColor(host.colorProvider.getColorFromAttribute(R.attr.colorError))
                listener {
                    host.callback?.onAction(DevicesAction.Delete(deviceId))
                }
            }
        }

        
        bottomSheetDividerItem {
            id("manageD2")
        }
        bottomSheetVerificationActionItem {
            id("rename")
            title(host.stringProvider.getString(R.string.action_rename))
            titleColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
            iconRes(R.drawable.ic_arrow_right)
            iconColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
            listener {
                host.callback?.onAction(DevicesAction.PromptRename(deviceId))
            }
        }
    }

    private fun handleNonE2EDevice(data: DeviceVerificationInfoBottomSheetViewState) {
        val host = this
        val info = data.deviceInfo.invoke() ?: return
        genericItem {
            id("info${info.deviceId}")
            title(info.displayName.orEmpty().toEpoxyCharSequence())
            description("(${info.deviceId})".toEpoxyCharSequence())
        }

        genericFooterItem {
            id("infoCrypto${info.deviceId}")
            text(host.stringProvider.getString(R.string.settings_failed_to_get_crypto_device_info).toEpoxyCharSequence())
        }

        info.deviceId?.let { addGenericDeviceManageActions(data, it) }
    }

    interface Callback {
        fun onAction(action: DevicesAction)
    }
}
