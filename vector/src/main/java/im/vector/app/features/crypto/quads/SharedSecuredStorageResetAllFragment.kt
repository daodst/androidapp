

package im.vector.app.features.crypto.quads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.setTextOrHide
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.FragmentSsssResetAllBinding
import im.vector.app.features.roommemberprofile.devices.DeviceListBottomSheet
import javax.inject.Inject

class SharedSecuredStorageResetAllFragment @Inject constructor() :
        VectorBaseFragment<FragmentSsssResetAllBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSsssResetAllBinding {
        return FragmentSsssResetAllBinding.inflate(inflater, container, false)
    }

    val sharedViewModel: SharedSecureStorageViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.ssssResetButtonReset.debouncedClicks {
            sharedViewModel.handle(SharedSecureStorageAction.DoResetAll)
        }

        views.ssssResetButtonCancel.debouncedClicks {
            sharedViewModel.handle(SharedSecureStorageAction.Back)
        }

        views.ssssResetOtherDevices.debouncedClicks {
            withState(sharedViewModel) {
                DeviceListBottomSheet.newInstance(it.userId, false).show(childFragmentManager, "DEV_LIST")
            }
        }

        sharedViewModel.onEach { state ->
            views.ssssResetOtherDevices.setTextOrHide(
                    state.activeDeviceCount
                            .takeIf { it > 0 }
                            ?.let { resources.getQuantityString(R.plurals.secure_backup_reset_devices_you_can_verify, it, it) }
            )
        }
    }
}
