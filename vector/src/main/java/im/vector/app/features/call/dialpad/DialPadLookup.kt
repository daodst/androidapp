

package im.vector.app.features.call.dialpad

import im.vector.app.features.call.lookup.pstnLookup
import im.vector.app.features.call.lookup.sipNativeLookup
import im.vector.app.features.call.vectorCallService
import im.vector.app.features.call.webrtc.WebRtcCallManager
import im.vector.app.features.createdirect.DirectRoomHelper
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject

class DialPadLookup @Inject constructor(
        private val session: Session,
        private val webRtcCallManager: WebRtcCallManager,
        private val directRoomHelper: DirectRoomHelper
) {
    sealed class Failure : Throwable() {
        object NoResult : Failure()
        object NumberIsYours : Failure()
    }

    data class Result(val userId: String, val roomId: String)

    suspend fun lookupPhoneNumber(phoneNumber: String): Result {
        session.vectorCallService.protocolChecker.awaitCheckProtocols()
        val thirdPartyUser = session.pstnLookup(phoneNumber, webRtcCallManager.supportedPSTNProtocol).firstOrNull() ?: throw Failure.NoResult
        val sipUserId = thirdPartyUser.userId
        val nativeLookupResults = session.sipNativeLookup(thirdPartyUser.userId)
        
        val roomId = if (nativeLookupResults.isNotEmpty()) {
            val nativeUserId = nativeLookupResults.first().userId
            if (nativeUserId == session.myUserId) {
                throw Failure.NumberIsYours
            }
            session.getExistingDirectRoomWithUser(nativeUserId)
            
                    ?: directRoomHelper.ensureDMExists(sipUserId)
        } else {
            
            directRoomHelper.ensureDMExists(sipUserId)
        }
        return Result(userId = sipUserId, roomId = roomId)
    }
}
