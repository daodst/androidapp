

package im.vector.app.features.analytics.ui.consent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import im.vector.app.R
import im.vector.app.config.analyticsConfig
import im.vector.app.core.extensions.setTextWithColoredPart
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.openUrlInChromeCustomTab
import im.vector.app.databinding.FragmentAnalyticsOptinBinding
import javax.inject.Inject

class AnalyticsOptInFragment @Inject constructor() :
        VectorBaseFragment<FragmentAnalyticsOptinBinding>(),
        OnBackPressed {

    
    
    private val viewModel: AnalyticsConsentViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAnalyticsOptinBinding {
        return FragmentAnalyticsOptinBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLink()
        setupListeners()
    }

    private fun setupListeners() {
        views.submit.debouncedClicks {
            viewModel.handle(AnalyticsConsentViewActions.SetUserConsent(userConsent = true))
        }
        views.later.debouncedClicks {
            viewModel.handle(AnalyticsConsentViewActions.SetUserConsent(userConsent = false))
        }
    }

    private fun setupLink() {
        views.subtitle.setTextWithColoredPart(
                fullTextRes = R.string.analytics_opt_in_content,
                coloredTextRes = R.string.analytics_opt_in_content_link,
                onClick = {
                    openUrlInChromeCustomTab(requireContext(), null, analyticsConfig.policyLink)
                }
        )
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        
        viewModel.handle(AnalyticsConsentViewActions.SetUserConsent(userConsent = false))
        
        return true
    }
}
