
package im.vector.app.core.dialogs

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.resources.StringProvider
import im.vector.app.databinding.DialogSslFingerprintBinding
import org.matrix.android.sdk.api.network.ssl.Fingerprint
import timber.log.Timber
import javax.inject.Inject


class UnrecognizedCertificateDialog @Inject constructor(
        private val activeSessionHolder: ActiveSessionHolder,
        private val stringProvider: StringProvider
) {
    private val ignoredFingerprints: MutableMap<String, MutableSet<Fingerprint>> = HashMap()
    private val openDialogIds: MutableSet<String> = HashSet()

    
    fun show(activity: Activity,
             unrecognizedFingerprint: Fingerprint,
             callback: Callback) {
        val userId = activeSessionHolder.getSafeActiveSession()?.myUserId
        val hsConfig = activeSessionHolder.getSafeActiveSession()?.sessionParams?.homeServerConnectionConfig ?: return

        internalShow(
                activity = activity,
                unrecognizedFingerprint = unrecognizedFingerprint,
                existing = true,
                callback = callback,
                userId = userId,
                homeServerUrl = hsConfig.homeServerUriBase.toString(),
                homeServerConnectionConfigHasFingerprints = hsConfig.allowedFingerprints.isNotEmpty()
        )
    }

    
    fun show(activity: Activity,
             unrecognizedFingerprint: Fingerprint,
             homeServerUrl: String,
             callback: Callback) {
        internalShow(
                activity = activity,
                unrecognizedFingerprint = unrecognizedFingerprint,
                existing = false,
                callback = callback,
                userId = null,
                homeServerUrl = homeServerUrl,
                homeServerConnectionConfigHasFingerprints = false
        )
    }

    
    private fun internalShow(activity: Activity,
                             unrecognizedFingerprint: Fingerprint,
                             existing: Boolean,
                             callback: Callback,
                             userId: String?,
                             homeServerUrl: String,
                             homeServerConnectionConfigHasFingerprints: Boolean) {
        val dialogId = userId ?: homeServerUrl + unrecognizedFingerprint.displayableHexRepr

        if (openDialogIds.contains(dialogId)) {
            Timber.i("Not opening dialog $dialogId as one is already open.")
            return
        }

        if (userId != null) {
            val f: Set<Fingerprint>? = ignoredFingerprints[userId]
            if (f != null && f.contains(unrecognizedFingerprint)) {
                callback.onIgnore()
                return
            }
        }

        val builder = MaterialAlertDialogBuilder(activity)
        val inflater = activity.layoutInflater
        val layout = inflater.inflate(R.layout.dialog_ssl_fingerprint, null)
        val views = DialogSslFingerprintBinding.bind(layout)
        views.sslFingerprintTitle.text = stringProvider.getString(R.string.ssl_fingerprint_hash, unrecognizedFingerprint.hashType.toString())
        views.sslFingerprint.text = unrecognizedFingerprint.displayableHexRepr
        if (userId != null) {
            views.sslUserId.text = stringProvider.getString(R.string.generic_label_and_value,
                    stringProvider.getString(R.string.username),
                    userId)
        } else {
            views.sslUserId.text = stringProvider.getString(R.string.generic_label_and_value,
                    stringProvider.getString(R.string.hs_url),
                    homeServerUrl)
        }
        if (existing) {
            if (homeServerConnectionConfigHasFingerprints) {
                views.sslExplanation.text = stringProvider.getString(R.string.ssl_expected_existing_expl)
            } else {
                views.sslExplanation.text = stringProvider.getString(R.string.ssl_unexpected_existing_expl)
            }
        } else {
            views.sslExplanation.text = stringProvider.getString(R.string.ssl_cert_new_account_expl)
        }
        builder.setView(layout)
        builder.setTitle(R.string.ssl_could_not_verify)
        builder.setPositiveButton(R.string.ssl_trust) { _, _ ->
            callback.onAccept()
        }
        if (existing) {
            builder.setNegativeButton(R.string.ssl_remain_offline) { _, _ ->
                if (userId != null) {
                    var f = ignoredFingerprints[userId]
                    if (f == null) {
                        f = HashSet()
                        ignoredFingerprints[userId] = f
                    }
                    f.add(unrecognizedFingerprint)
                }
                callback.onIgnore()
            }
            builder.setNeutralButton(R.string.ssl_logout_account) { _, _ -> callback.onReject() }
        } else {
            builder.setNegativeButton(R.string.action_cancel) { _, _ -> callback.onReject() }
        }

        builder.setOnDismissListener {
            Timber.d("Dismissed!")
            openDialogIds.remove(dialogId)
        }

        builder.show()
        openDialogIds.add(dialogId)
    }

    interface Callback {
        
        fun onAccept()

        
        fun onIgnore()

        
        fun onReject()
    }
}
