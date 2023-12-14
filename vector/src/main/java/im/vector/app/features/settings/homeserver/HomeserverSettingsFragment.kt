

package im.vector.app.features.settings.homeserver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.FragmentGenericRecyclerBinding
import javax.inject.Inject


class HomeserverSettingsFragment @Inject constructor(
        private val homeserverSettingsController: HomeserverSettingsController
) : VectorBaseFragment<FragmentGenericRecyclerBinding>(),
        HomeserverSettingsController.Callback {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGenericRecyclerBinding {
        return FragmentGenericRecyclerBinding.inflate(inflater, container, false)
    }

    private val viewModel: HomeserverSettingsViewModel by fragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeserverSettingsController.callback = this
        views.genericRecyclerView.configureWith(homeserverSettingsController)
    }

    override fun onDestroyView() {
        homeserverSettingsController.callback = null
        views.genericRecyclerView.cleanup()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.settings_home_server)
    }

    override fun retry() {
        viewModel.handle(HomeserverSettingsAction.Refresh)
    }

    override fun invalidate() = withState(viewModel) { state ->
        homeserverSettingsController.setData(state)
    }
}
