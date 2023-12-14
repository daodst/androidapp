
package im.vector.app.features.crypto.verification

import android.content.Context
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.features.displayname.getBestName
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.detail.RoomDetailActivity
import im.vector.app.features.home.room.detail.arguments.TimelineArgs
import im.vector.app.features.popup.PopupAlertManager
import im.vector.app.features.popup.VerificationVectorAlert
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.verification.PendingVerificationRequest
import org.matrix.android.sdk.api.session.crypto.verification.VerificationService
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import org.matrix.android.sdk.api.util.toMatrixItem
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class IncomingVerificationRequestHandler @Inject constructor(
        private val context: Context,
        private var avatarRenderer: Provider<AvatarRenderer>,
        private val popupAlertManager: PopupAlertManager) : VerificationService.Listener {

    private var session: Session? = null

    fun start(session: Session) {
        this.session = session
        session.cryptoService().verificationService().addListener(this)
    }

    fun stop() {
        session?.cryptoService()?.verificationService()?.removeListener(this)
        this.session = null
    }

    override fun transactionUpdated(tx: VerificationTransaction) {
        if (!tx.isToDeviceTransport()) return
        
        val uid = "kvr_${tx.transactionId}"
        when (tx.state) {
            is VerificationTxState.OnStarted       -> {
                
                val user = session?.getUser(tx.otherUserId)
                val name = user?.toMatrixItem()?.getBestName() ?: tx.otherUserId
                val alert = VerificationVectorAlert(
                        uid,
                        context.getString(R.string.sas_incoming_request_notif_title),
                        context.getString(R.string.sas_incoming_request_notif_content, name),
                        R.drawable.ic_shield_black,
                        shouldBeDisplayedIn = { activity ->
                            if (activity is VectorBaseActivity<*>) {
                                
                                activity.supportFragmentManager.findFragmentByTag(VerificationBottomSheet.WAITING_SELF_VERIF_TAG)?.let {
                                    false.also {
                                        popupAlertManager.cancelAlert(uid)
                                    }
                                } ?: true
                            } else true
                        }
                )
                        .apply {
                            viewBinder = VerificationVectorAlert.ViewBinder(user?.toMatrixItem(), avatarRenderer.get())
                            contentAction = Runnable {
                                (weakCurrentActivity?.get() as? VectorBaseActivity<*>)?.let {
                                    it.navigator.performDeviceVerification(it, tx.otherUserId, tx.transactionId)
                                }
                            }
                            dismissedAction = Runnable {
                                tx.cancel()
                            }
                            addButton(
                                    context.getString(R.string.action_ignore),
                                    { tx.cancel() }
                            )
                            addButton(
                                    context.getString(R.string.action_open),
                                    {
                                        (weakCurrentActivity?.get() as? VectorBaseActivity<*>)?.let {
                                            it.navigator.performDeviceVerification(it, tx.otherUserId, tx.transactionId)
                                        }
                                    }
                            )
                            
                            expirationTimestamp = System.currentTimeMillis() + (10 * 60 * 1000L)
                        }
                popupAlertManager.postVectorAlert(alert)
            }
            is VerificationTxState.TerminalTxState -> {
                
                popupAlertManager.cancelAlert(uid)
            }
            else                                   -> Unit
        }
    }

    override fun verificationRequestCreated(pr: PendingVerificationRequest) {
        Timber.v("## SAS verificationRequestCreated ${pr.transactionId}")
        
        if (pr.isIncoming) {
            
            
            
            if (pr.otherUserId == session?.myUserId) {
                
                popupAlertManager.cancelAlert("review_login")
            }
            val user = session?.getUser(pr.otherUserId)?.toMatrixItem()
            val name = user?.getBestName() ?: pr.otherUserId
            val description = if (name == pr.otherUserId) {
                name
            } else {
                "$name (${pr.otherUserId})"
            }

            val alert = VerificationVectorAlert(
                    uniqueIdForVerificationRequest(pr),
                    context.getString(R.string.sas_incoming_request_notif_title),
                    description,
                    R.drawable.ic_shield_black,
                    shouldBeDisplayedIn = { activity ->
                        if (activity is RoomDetailActivity) {
                            activity.intent?.extras?.getParcelable<TimelineArgs>(RoomDetailActivity.EXTRA_ROOM_DETAIL_ARGS)?.let {
                                it.roomId != pr.roomId
                            } ?: true
                        } else true
                    }
            )
                    .apply {
                        viewBinder = VerificationVectorAlert.ViewBinder(user, avatarRenderer.get())
                        contentAction = Runnable {
                            (weakCurrentActivity?.get() as? VectorBaseActivity<*>)?.let {
                                val roomId = pr.roomId
                                if (roomId.isNullOrBlank()) {
                                    it.navigator.waitSessionVerification(it)
                                } else {
                                    it.navigator.openRoom(it, roomId, pr.transactionId)
                                }
                            }
                        }
                        dismissedAction = Runnable {
                            session?.cryptoService()?.verificationService()?.declineVerificationRequestInDMs(pr.otherUserId,
                                    pr.transactionId ?: "",
                                    pr.roomId ?: ""
                            )
                        }
                        colorAttribute = R.attr.vctr_notice_secondary
                        
                        expirationTimestamp = System.currentTimeMillis() + (5 * 60 * 1000L)
                    }
            popupAlertManager.postVectorAlert(alert)
        }
    }

    override fun verificationRequestUpdated(pr: PendingVerificationRequest) {
        
        if (pr.isIncoming && (pr.isReady || pr.handledByOtherSession || pr.cancelConclusion != null)) {
            popupAlertManager.cancelAlert(uniqueIdForVerificationRequest(pr))
        }
    }

    private fun uniqueIdForVerificationRequest(pr: PendingVerificationRequest) =
            "verificationRequest_${pr.transactionId}"
}
