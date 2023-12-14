

package im.vector.app.features.raw.wellknown

import org.matrix.android.sdk.api.MatrixPatterns.getDomain
import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.raw.RawService

suspend fun RawService.getElementWellknown(sessionParams: SessionParams): ElementWellKnown? {
    
    val domain = sessionParams.userId.getDomain()
    return tryOrNull { getWellknown(domain) }
            ?.let { ElementWellKnownMapper.from(it) }
}

fun ElementWellKnown.isE2EByDefault() = elementE2E?.e2eDefault ?: riotE2E?.e2eDefault ?: true
