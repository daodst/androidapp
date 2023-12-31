
package im.vector.app.features.discovery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import im.vector.app.core.extensions.postLiveEvent
import im.vector.app.core.utils.LiveEvent
import javax.inject.Inject

class DiscoverySharedViewModel @Inject constructor() : ViewModel() {
    var navigateEvent = MutableLiveData<LiveEvent<DiscoverySharedViewModelAction>>()

    fun requestChangeToIdentityServer(serverUrl: String) {
        navigateEvent.postLiveEvent(DiscoverySharedViewModelAction.ChangeIdentityServer(serverUrl))
    }
}
