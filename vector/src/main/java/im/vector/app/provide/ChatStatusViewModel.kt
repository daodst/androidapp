

package im.vector.app.provide

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.provide.log.ChatPhoneLogSource
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ChatStatusViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    lateinit var chatPhoneLogSource: ChatPhoneLogSource

    fun startOutgoingCall(
            context: Context,
            nativeRoomId: String,
            otherUserId: String,
            isVideoCall: Boolean,
            phone: String
    ) {
        viewModelScope.launch {
            startOutgoingCallInner(context, nativeRoomId, otherUserId, isVideoCall, phone)
        }
    }
    suspend fun hasUserInfo(context: Context): Boolean {
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        return runCatching {
            activeSession.hasUserInfo(activeSession.myUserId)
        }.fold({
            it
        }, {
            false
        })
    }
    suspend fun startOutgoingCallInner(
            context: Context,
            nativeRoomId: String,
            otherUserId: String,
            isVideoCall: Boolean,
            phone: String
    ) {
        val webRtcCallManager = context.singletonEntryPoint().webRtcCallManager()
        webRtcCallManager.startOutgoingCall(nativeRoomId, otherUserId, isVideoCall, callPhone = phone)
    }

    fun getLogList() {
        Timber.i("========ChatStatusViewModel==========" + chatPhoneLogSource + "====")
    }
}
