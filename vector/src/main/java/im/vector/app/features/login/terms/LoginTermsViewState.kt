

package im.vector.app.features.login.terms

import com.airbnb.mvrx.MavericksState
import org.matrix.android.sdk.api.auth.data.LocalizedFlowDataLoginTerms

data class LoginTermsViewState(
        val localizedFlowDataLoginTermsChecked: List<LocalizedFlowDataLoginTermsChecked>
) : MavericksState {
    fun check(data: LocalizedFlowDataLoginTerms) {
        localizedFlowDataLoginTermsChecked.find { it.localizedFlowDataLoginTerms == data }?.checked = true
    }

    fun uncheck(data: LocalizedFlowDataLoginTerms) {
        localizedFlowDataLoginTermsChecked.find { it.localizedFlowDataLoginTerms == data }?.checked = false
    }

    fun allChecked(): Boolean {
        return localizedFlowDataLoginTermsChecked.all { it.checked }
    }
}
