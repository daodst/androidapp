

package im.vector.app.features.createdirect.cluster

import im.vector.app.core.platform.VectorViewEvents

sealed class ClusterViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : ClusterViewEvents()
}
