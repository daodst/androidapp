

package im.vector.app.features.home

import im.vector.app.core.platform.VectorViewEvents
import org.matrix.android.sdk.api.util.MatrixItem

sealed interface HomeActivityViewEvents : VectorViewEvents {
    data class AskPasswordToInitCrossSigning(val userItem: MatrixItem.UserItem?) : HomeActivityViewEvents
    data class OnNewSession(val userItem: MatrixItem.UserItem?, val waitForIncomingRequest: Boolean = true) : HomeActivityViewEvents
    object  CrossSigning : HomeActivityViewEvents
    data class OnCrossSignedInvalidated(val userItem: MatrixItem.UserItem) : HomeActivityViewEvents
    object PromptToEnableSessionPush : HomeActivityViewEvents
    object ShowAnalyticsOptIn : HomeActivityViewEvents
    object NotifyUserForThreadsMigration : HomeActivityViewEvents
    data class MigrateThreads(val checkSession: Boolean) : HomeActivityViewEvents
}
