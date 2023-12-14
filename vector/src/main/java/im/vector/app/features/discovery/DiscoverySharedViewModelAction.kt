

package im.vector.app.features.discovery

sealed class DiscoverySharedViewModelAction {
    data class ChangeIdentityServer(val newUrl: String) : DiscoverySharedViewModelAction()
}
