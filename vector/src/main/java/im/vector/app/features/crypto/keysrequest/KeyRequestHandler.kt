

package im.vector.app.features.crypto.keysrequest

import android.content.Context
import im.vector.app.R
import im.vector.app.core.date.DateFormatKind
import im.vector.app.core.date.VectorDateFormatter
import im.vector.app.features.popup.DefaultVectorAlert
import im.vector.app.features.popup.PopupAlertManager
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.api.session.crypto.keyshare.GossipingRequestListener
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.IncomingRequestCancellation
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.IncomingSecretShareRequest
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.api.session.crypto.verification.SasVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationService
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class KeyRequestHandler @Inject constructor(
        private val context: Context,
        private val popupAlertManager: PopupAlertManager,
        private val dateFormatter: VectorDateFormatter
) : GossipingRequestListener,
        VerificationService.Listener {

    private val alertsToRequests = HashMap<String, ArrayList<IncomingRoomKeyRequest>>()

    var session: Session? = null

    fun start(session: Session) {
        this.session = session
        session.cryptoService().verificationService().addListener(this)
        session.cryptoService().addRoomKeysRequestListener(this)
    }

    fun stop() {
        session?.cryptoService()?.verificationService()?.removeListener(this)
        session?.cryptoService()?.removeRoomKeysRequestListener(this)
        session = null
    }

    override fun onSecretShareRequest(request: IncomingSecretShareRequest): Boolean {
        
        Timber.v("## onSecretShareRequest() : Ignoring $request")
        request.ignore?.run()
        return true
    }

    
    override fun onRoomKeyRequest(request: IncomingRoomKeyRequest) {
        val userId = request.userId
        val deviceId = request.deviceId
        val requestId = request.requestId

        if (userId.isNullOrBlank() || deviceId.isNullOrBlank() || requestId.isNullOrBlank()) {
            Timber.e("## handleKeyRequest() : invalid parameters")
            return
        }

        
        val mappingKey = keyForMap(userId, deviceId)
        if (alertsToRequests.containsKey(mappingKey)) {
            
            alertsToRequests[mappingKey]?.add(request)
            return
        }

        alertsToRequests[mappingKey] = ArrayList<IncomingRoomKeyRequest>().apply { this.add(request) }

        
        session?.cryptoService()?.downloadKeys(listOf(userId), false, object : MatrixCallback<MXUsersDevicesMap<CryptoDeviceInfo>> {
            override fun onSuccess(data: MXUsersDevicesMap<CryptoDeviceInfo>) {
                val deviceInfo = data.getObject(userId, deviceId)

                if (null == deviceInfo) {
                    Timber.e("## displayKeyShareDialog() : No details found for device $userId:$deviceId")
                    
                    return
                }

                if (deviceInfo.isUnknown) {
                    session?.cryptoService()?.setDeviceVerification(DeviceTrustLevel(crossSigningVerified = false, locallyVerified = false), userId, deviceId)

                    deviceInfo.trustLevel = DeviceTrustLevel(crossSigningVerified = false, locallyVerified = false)

                    
                    session?.cryptoService()?.getMyDevicesInfo()?.firstOrNull { it.deviceId == deviceId }?.let {
                        postAlert(context, userId, deviceId, true, deviceInfo, it)
                    } ?: run {
                        postAlert(context, userId, deviceId, true, deviceInfo)
                    }
                } else {
                    postAlert(context, userId, deviceId, false, deviceInfo)
                }
            }

            override fun onFailure(failure: Throwable) {
                
                Timber.e(failure, "## displayKeyShareDialog : downloadKeys")
            }
        })
    }

    private fun postAlert(context: Context,
                          userId: String,
                          deviceId: String,
                          wasNewDevice: Boolean,
                          deviceInfo: CryptoDeviceInfo?,
                          moreInfo: DeviceInfo? = null) {
        val deviceName = if (deviceInfo!!.displayName().isNullOrEmpty()) deviceInfo.deviceId else deviceInfo.displayName()
        val dialogText: String?

        if (moreInfo != null) {
            val lastSeenIp = if (moreInfo.lastSeenIp.isNullOrBlank()) {
                context.getString(R.string.encryption_information_unknown_ip)
            } else {
                moreInfo.lastSeenIp
            }

            val lastSeenTime = dateFormatter.format(moreInfo.lastSeenTs, DateFormatKind.DEFAULT_DATE_AND_TIME)
            val lastSeenInfo = context.getString(R.string.devices_details_last_seen_format, lastSeenIp, lastSeenTime)
            dialogText = if (wasNewDevice) {
                context.getString(R.string.you_added_a_new_device_with_info, deviceName, lastSeenInfo)
            } else {
                context.getString(R.string.your_unverified_device_requesting_with_info, deviceName, lastSeenInfo)
            }
        } else {
            dialogText = if (wasNewDevice) {
                context.getString(R.string.you_added_a_new_device, deviceName)
            } else {
                context.getString(R.string.your_unverified_device_requesting, deviceName)
            }
        }

        val alert = DefaultVectorAlert(
                alertManagerId(userId, deviceId),
                context.getString(R.string.key_share_request),
                dialogText,
                R.drawable.key_small
        )

        alert.colorRes = R.color.key_share_req_accent_color

        val mappingKey = keyForMap(userId, deviceId)
        alert.dismissedAction = Runnable {
            denyAllRequests(mappingKey)
        }

        alert.addButton(context.getString(R.string.share_without_verifying_short_label), {
            shareAllSessions(mappingKey)
        })

        alert.addButton(context.getString(R.string.ignore_request_short_label), {
            denyAllRequests(mappingKey)
        })

        popupAlertManager.postVectorAlert(alert)
    }

    private fun denyAllRequests(mappingKey: String) {
        alertsToRequests[mappingKey]?.forEach {
            it.ignore?.run()
        }
        alertsToRequests.remove(mappingKey)
    }

    private fun shareAllSessions(mappingKey: String) {
        alertsToRequests[mappingKey]?.forEach {
            it.share?.run()
        }
        alertsToRequests.remove(mappingKey)
    }

    
    override fun onRoomKeyRequestCancellation(request: IncomingRequestCancellation) {
        
        val userId = request.userId
        val deviceId = request.deviceId
        val requestId = request.requestId

        if (userId.isNullOrEmpty() || deviceId.isNullOrEmpty() || requestId.isNullOrEmpty()) {
            Timber.e("## handleKeyRequestCancellation() : invalid parameters")
            return
        }

        val alertMgrUniqueKey = alertManagerId(userId, deviceId)
        alertsToRequests[alertMgrUniqueKey]?.removeAll {
            it.deviceId == request.deviceId &&
                    it.userId == request.userId &&
                    it.requestId == request.requestId
        }
        if (alertsToRequests[alertMgrUniqueKey]?.isEmpty() == true) {
            popupAlertManager.cancelAlert(alertMgrUniqueKey)
            alertsToRequests.remove(keyForMap(userId, deviceId))
        }
    }

    override fun transactionUpdated(tx: VerificationTransaction) {
        if (tx is SasVerificationTransaction) {
            val state = tx.state
            if (state == VerificationTxState.Verified) {
                
                shareAllSessions("${tx.otherDeviceId}${tx.otherUserId}")
                popupAlertManager.cancelAlert("ikr_${tx.otherDeviceId}${tx.otherUserId}")
            }
        }
        
        
    }

    override fun markedAsManuallyVerified(userId: String, deviceId: String) {
        
        shareAllSessions(keyForMap(userId, deviceId))
        popupAlertManager.cancelAlert(alertManagerId(userId, deviceId))
    }

    private fun keyForMap(userId: String, deviceId: String) = "$deviceId$userId"

    private fun alertManagerId(userId: String, deviceId: String) = "ikr_$deviceId$userId"
}
