

package im.vector.app.features.settings.legals

import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.discovery.ServerPolicy
import im.vector.app.features.settings.VectorSettingsUrls
import javax.inject.Inject

class ElementLegals @Inject constructor(
        private val stringProvider: StringProvider
) {
    
    fun getData(): List<ServerPolicy> {
        return listOf(
                ServerPolicy(stringProvider.getString(R.string.settings_copyright), VectorSettingsUrls.COPYRIGHT),
                ServerPolicy(stringProvider.getString(R.string.settings_app_term_conditions), VectorSettingsUrls.TAC),
                ServerPolicy(stringProvider.getString(R.string.settings_privacy_policy), VectorSettingsUrls.PRIVACY_POLICY)
        )
    }
}
