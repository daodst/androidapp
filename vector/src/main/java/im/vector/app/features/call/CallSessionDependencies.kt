

package im.vector.app.features.call

import im.vector.app.features.call.lookup.CallProtocolsChecker
import im.vector.app.features.call.lookup.CallUserMapper
import im.vector.app.features.session.SessionScopedProperty
import org.matrix.android.sdk.api.session.Session

interface VectorCallService {
    val protocolChecker: CallProtocolsChecker
    val userMapper: CallUserMapper
}

val Session.vectorCallService: VectorCallService by SessionScopedProperty {
    object : VectorCallService {
        override val protocolChecker = CallProtocolsChecker(it)
        override val userMapper = CallUserMapper(it, protocolChecker)
    }
}
