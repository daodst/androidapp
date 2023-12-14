package im.vector.app.features.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.features.crypto.quads.SharedSecureStorageActivity
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME

data class SharedSecureStorageMainViewState(
        val ready: Boolean = false,
        val hasPassphrase: Boolean = true,
        val checkingSSSSAction: Async<Unit> = Uninitialized,
        val step: SharedSecureStorageStep = SharedSecureStorageStep.EnterPassphrase,
        val activeDeviceCount: Int = 0,
        val showResetAllAction: Boolean = false,
        val userId: String = "",
        val keyId: String? = null,
        val requestedSecrets: List<String> = listOf(MASTER_KEY_SSSS_NAME, USER_SIGNING_KEY_SSSS_NAME, SELF_SIGNING_KEY_SSSS_NAME, KEYBACKUP_SECRET_SSSS_NAME),
        val resultKeyStoreAlias: String = SharedSecureStorageActivity.DEFAULT_RESULT_KEYSTORE_ALIAS
) : MavericksState {

}

enum class SharedSecureStorageStep {
    EnterPassphrase,
    EnterKey,
    ResetAll
}
