

package im.vector.app.provide

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import im.vector.app.VectorApplication
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.extensions.takeAs
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.wallet.router.base.ApplicationDelegate
import im.wallet.router.base.IApplication
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.LiveEventListener
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.permalinks.PermalinkService
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.send.CUS_TEXT_TYPE_NORMAL
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.session.utils.bean.MyRoomList
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.internal.database.model.ChainEntity
import org.matrix.android.sdk.internal.database.model.ChatPhoneLog
import org.matrix.android.sdk.internal.session.remark.Remark
import timber.log.Timber



data class UserInfo(
        val userId: String,
        
        val displayName: String? = null,
        val avatarUrl: String? = null,
        val tel_numbers: List<String>? = null,
        var address: String? = null
)

internal object UserInfoMapper {

    fun map(user: User): UserInfo {
        return UserInfo(
                user.userId, user.displayName, user.avatarUrl, user.tel_numbers
        )
    }
}

internal fun User.asDomain(): UserInfo {
    return UserInfoMapper.map(this)
}

object ChatStatusProvide : Session.Listener {

    val TAG: String = "ChatStatusProvide"

    
    @JvmStatic
    fun loginStatus(context: Context): Boolean {
        return try {
            val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
            activeSessionHolder.getActiveSession().isOpenable
        } catch (e: Exception) {
            false
        }
    }

    fun onMsgEvent(senderId: String?, roomId: String?, msgType: String, session: Session, context: Context) {
        if (null == senderId || null == roomId) {
            return
        }
        Timber.i("-----------onMsgEvent-------------------------------------")
        if (msgType == MessageType.MSGTYPE_TEXT_CREATE_CLUSTER) {
            
            context?.takeAs<IApplication>()?.apply {
                getDelegate(ApplicationDelegate.MOODLE_TYPE_APP)?.appNotice?.refreshChatEngine()
            }
        } else if (msgType == MessageType.MSGTYPE_VOTE_TEXT) {
            
            val name: String = session.getRoomMember(senderId, roomId)?.let { memberSummary ->
                if (memberSummary.displayName.isNullOrEmpty()) {
                    memberSummary.userId
                } else {
                    memberSummary.displayName
                }
            } ?: ""
            context?.takeAs<IApplication>()?.apply {
                getDelegate(ApplicationDelegate.MOODLE_TYPE_APP)?.appNotice?.insertGroupVoteNotice(roomId, name)
            }
        }
    }

    var listener: LiveEventListener? = null

    @JvmStatic
    fun addMonitor(context: Context) {
        tryOrNull {
            val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
            val session = activeSessionHolder.getActiveSession()
            session.addEventStreamListener(getListener(session, context))
        }
    }

    private fun getListener(session: Session, context: Context): LiveEventListener {
        if (listener == null) {
            listener = object : LiveEventListener {
                override fun onLiveEvent(roomId: String, event: Event) {
                    
                    if (event.isEncrypted()) {
                        return
                    }
                    if (event.getClearType() == EventType.MESSAGE) {
                        val messageContent: MessageContent = event.getClearContent()?.toModel() ?: return
                        onMsgEvent(event.senderId, roomId, messageContent.msgType, session, context)
                    }
                }

                override fun onPaginatedEvent(roomId: String, event: Event) {
                }

                override fun onEventDecrypted(event: Event, clearEvent: JsonDict) {
                    
                    
                    val event1 = clearEvent.toModel<Event>()
                    
                    
                    

                    if (event1?.getClearType() == EventType.MESSAGE) {
                        val messageContent: MessageContent = event1.getClearContent()?.toModel() ?: return
                        onMsgEvent(event.senderId, event.roomId, messageContent.msgType, session, context)
                    }
                }

                override fun onEventDecryptionError(event: Event, throwable: Throwable) {
                }

                override fun onLiveToDeviceEvent(event: Event) {
                }
            }
        }
        return listener!!
    }

    override fun onSessionStopped(session: Session) {
        super.onSessionStopped(session)
        listener?.let {
            session.removeEventStreamListener(it)
        }
    }

    fun showUpgradeAnim(context: Context, roomId: String) {
        try {
            val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
            val session = activeSessionHolder.getActiveSession()
            session.getRoom(roomId)?.setRoomSummaryUpgradeAnimShow(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    suspend fun getInfo(context: Context, address: String): String {
        val holder = context.singletonEntryPoint().activeSessionHolder()
        val session = holder.getActiveSession()
        val pledgeBean2 = session.getPledgeBean(address)
        return pledgeBean2.data?.all_pledge_amount?.amount ?: ""
    }

    @JvmStatic
    suspend fun getUserInfo(context: Context, address: String): UserInfo {
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        val user = activeSession.resolveUserOnline(address)
        return user.asDomain()
    }

    
    @JvmStatic
    fun sendThanks(context: Context, roomId: String) {
        val holder = context.singletonEntryPoint().activeSessionHolder()
        val session = holder.getActiveSession()
        val roomSummary = session.getRoomSummary(roomId) ?: return
        val template = session.permalinkService().createMentionSpanTemplate(PermalinkService.SpanTemplateType.HTML)
        session.getLikeRoomMember(roomSummary.owner, roomId)?.let { memberSummary ->

            val displayName = if (memberSummary.displayName.isNullOrEmpty()) {
                memberSummary.userId
            } else {
                memberSummary.displayName
            }
            val content = buildString {
                append(String.format(template, memberSummary.userId, displayName))
                append(":GAS")
            }
            session.getRoom(roomId)?.sendThanks(content)
        }
    }

    
    @JvmStatic
    fun getOtherUserInfo(context: Context, address: String, roomId: String): String {
        val holder = context.singletonEntryPoint().activeSessionHolder()
        val session = holder.getActiveSession()
        return session.getLikeRoomMember(address, roomId)?.let { memberSummary ->
            if (memberSummary.displayName.isNullOrEmpty()) {
                memberSummary.userId
            } else {
                memberSummary.displayName!!
            }

        } ?: "--"
    }

    
    @JvmStatic
    fun getOtherUserIdentity(context: Context, address: String, roomId: String): Int {
        val holder = context.singletonEntryPoint().activeSessionHolder()
        val session = holder.getActiveSession()
        val roomSummary = session.getRoomSummary(roomId) ?: return 2
        return if (roomSummary.owner == address) 0
        else 2
    }

    
    @JvmStatic
    fun setDisplayName(context: Context, userId: String, newDisplayName: String) {
        runBlocking {
            try {
                val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
                activeSession.setDisplayName(userId, newDisplayName)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

        }
    }

    
    @JvmStatic
    fun updateAvatar(context: Context, userId: String, newAvatarUri: Uri, fileName: String) {
        runBlocking(Dispatchers.IO) {
            try {
                val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
                activeSession.updateAvatar(userId, newAvatarUri, fileName)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun getAddress(context: Context): String {
        try {
            val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
            return getAddressByUid(activeSession.myUserId)
        } catch (e: Exception) {
            
            return "";
        }
    }

    @JvmStatic
    fun getFullAddress(context: Context): String {
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        return activeSession.myUserId
    }

    @JvmStatic
    suspend fun startOutgoingCallInner(
            context: Context,
            nativeRoomId: String,
            otherUserId: String,
            isVideoCall: Boolean
    ) {
        val webRtcCallManager = context.singletonEntryPoint().webRtcCallManager()
        webRtcCallManager.startOutgoingCall(nativeRoomId, otherUserId, isVideoCall)
    }

    
    @JvmStatic
    fun getUserId(context: Context): String {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        return activeSessionHolder.getSafeActiveSession()?.myUserId ?: "";
    }

    @JvmStatic
    fun isLogin(context: Context, address: String): Boolean {
        val vectorApplication = context.applicationContext as VectorApplication;
        return vectorApplication.isLogin(address)
    }

    @JvmStatic
    fun toLogin(context: Context) {
        context.singletonEntryPoint().navigator().openLogin(context)
    }

    @JvmStatic
    fun showAvatarRenderer(
            context: Context,
            id: String?,
            displayName: String?,
            ico: String?,
            iv: ImageView
    ) {
        val renderer = context.singletonEntryPoint().avatarRenderer()
        renderer.render(MatrixItem.UserItem(id ?: "", displayName, ico), iv)
    }

    @JvmStatic
    fun setUserInfo(context: Context, imageView: ImageView, displayNameTv: TextView) {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val userId: String? = activeSessionHolder.getSafeActiveSession()?.myUserId;
        if (userId != null && userId.isNotEmpty()) {
            val user: User? = activeSessionHolder.getSafeActiveSession()?.getUser(userId);
            user?.let {
                val avatarRenderer = context.singletonEntryPoint().avatarRenderer()
                avatarRenderer.render(it.toMatrixItem(), imageView)
                displayNameTv.text = it.displayName
            }
        }
    }

    @JvmStatic
    fun getLoginUserInfo(context: Context): UserInfo? {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val userId: String? = activeSessionHolder.getSafeActiveSession()?.myUserId;
        var loginUserInfo: UserInfo? = null;
        if (userId != null && userId.isNotEmpty()) {
            val user: User? = activeSessionHolder.getSafeActiveSession()?.getUser(userId);
            loginUserInfo = user?.asDomain()
        }
        return loginUserInfo
    }

    
    @JvmStatic
    fun signOut(activity: Activity) {
        MainActivity.restartApp(activity, MainActivityArgs(clearCredentials = true))
    }

    @JvmStatic
    fun homeServerUrl(context: Context): String {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        return activeSession.sessionParams.homeServerUrl
    }

    
    @JvmStatic
    fun existRoom(context: Context, userId: String): String? {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        return activeSession.getExistingDirectRoomWithUser(userId)
    }

    @JvmStatic
    fun insetChatLog(context: Context, log: ChatPhoneLog) {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        activeSession.insert(log)
    }

    
    @JvmStatic
    fun getChatLog(context: Context, status: Int = 0): List<ChatPhoneLog> {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        return activeSession.queryAll(status)
    }

    
    @JvmStatic
    fun getRemarks(context: Context): LiveData<List<Remark>> {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        return activeSession.getRemarks()
    }

    @JvmStatic
    fun searchUserInfoList(
            viewModel: ViewModel,
            context: Context,
            addrs: List<String>,
            block: (List<UserInfo>) -> Unit
    ) {
        viewModel.viewModelScope.launch() {
            var list = mutableListOf<UserInfo>()
            addrs.forEach { strt ->
                try {
                    var userInfo = getUserInfo(context, strt)
                    userInfo.address = strt
                    list.add(userInfo)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                    list.add(UserInfo("", "", null))
                }
            }
            block.invoke(list)
        }
    }

    @JvmStatic
    fun getServerPubKeyTask(context: Context, viewModel: ViewModel, call: (String) -> Unit) {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        viewModel.viewModelScope.launch {
            val pubKey = activeSession.getServerPubKeyTask()
            call.invoke(pubKey)
        }
    }

    
    @JvmStatic
    fun forceUpdateRemarks(context: Context, remarks: List<Remark>) {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        activeSession.forceUpdateRemarks(remarks)
    }

    
    @JvmStatic
    fun insertOnlineRemarks(context: Context, remarks: List<Remark>) {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        activeSession.insertOnlineRemarks(remarks)
    }

    @JvmStatic
    fun getChainInfoCache(context: Context): ChainEntity? {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        return activeSession.queryChainAll()
    }

    @JvmStatic
    fun saveChainInfo(context: Context, chain: ChainEntity, call: Realm.Transaction.OnSuccess) {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        activeSession.insertChain(chain, call)
    }

    @JvmStatic
    fun deleteAllChainInfo(context: Context) {
        val activeSessionHolder = context.singletonEntryPoint().activeSessionHolder()
        val activeSession = activeSessionHolder.getActiveSession()
        activeSession.deleteAll()
    }

    
    @JvmStatic
    fun httpIsTodayActive(context: Context): Boolean {
        return runBlocking {
            try {
                val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
                activeSession.isTodayActive()
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }
        }
    }

    
    @JvmStatic
    fun httpRecent7DayActive(context: Context): String {
        return runBlocking {
            try {
                val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
                activeSession.recent7DayActive()
            } catch (e: Throwable) {
                e.printStackTrace()
                ""
            }
        }
    }

    
    @JvmStatic
    fun httpGetMyJoinedRooms(context: Context): MyRoomList? {
        return runBlocking {
            try {
                val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
                activeSession.httpGetMyJoinedRooms()
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        }
    }

    
    @JvmStatic
    fun httpAutoJoinRoom(context: Context, groupId: String): Any? {
        return runBlocking {
            try {
                val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
                activeSession.httpAutoJoinRoom(groupId)
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        }
    }

    
    @SuppressLint("LogNotTimber")
    @JvmStatic
    fun insertLocalCusTxtMessage(context: Context, roomId: String, chatTips: String,
                                 userName: String, msgTitle: String, msgContent: String, type: Int = CUS_TEXT_TYPE_NORMAL): Boolean {
        if (roomId.isEmpty()) {
            Log.e(TAG, "insertLocalCusTxtMessage() roomId is empty")
            return false
        }
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        val room = activeSession.getRoom(roomId)
        if (room == null) {
            Log.e(TAG, "insertLocalCusTxtMessage() room is null")
            return false;
        }
        try {
            room.sendCusTxtMessage(chatTips, userName, msgTitle, msgContent, type = type)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.e(TAG, "insertLocalCusTxtMessage() exception:${ex}")
            return false
        }
        return true
    }

    
    @SuppressLint("LogNotTimber")
    @JvmStatic
    fun insertLocalRewardMessage(context: Context, roomId: String, rewardType: Int): Boolean {
        if (roomId.isEmpty()) {
            Log.e(TAG, "insertLocalDeviceRewardMessage() roomId is empty")
            return false
        }
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        val room = activeSession.getRoom(roomId)
        if (room == null) {
            Log.e(TAG, "insertLocalDeviceRewardMessage() room is null")
            return false;
        }
        try {
            room.sendCusAwardMessage(rewardType)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.e(TAG, "insertLocalDeviceRewardMessage() exception:${ex}")
            return false;
        }
        return true
    }

    
    @SuppressLint("LogNotTimber")
    @JvmStatic
    fun insertLocalCreateGroupMessage(context: Context, roomId: String): Boolean {
        if (roomId.isEmpty()) {
            Log.e(TAG, "insertLocalCreateGroupMessage() roomId is empty")
            return false
        }
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        val room = activeSession.getRoom(roomId)
        if (room == null) {
            Log.e(TAG, "insertLocalCreateGroupMessage() room is null")
            return false
        }
        try {
            room.sendCusDaoMessage()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.e(TAG, "insertLocalCreateGroupMessage() exception:${ex}")
            return false
        }
        return true
    }

    
    @SuppressLint("LogNotTimber")
    @JvmStatic
    fun insertLocalDailyReportMessage(context: Context, roomId: String,
                                      deviceRate: String,
                                      deviceRateSmall: String,
                                      deviceRateUp: Boolean,
                                      connRate: String,
                                      connRateSmall: String,
                                      connRateUp: Boolean,
                                      newDeviceNum: String,
                                      dvmNum: String,
                                      posNum: String,
                                      day3Pos: String,
                                      day3Active: String,
                                      day3Lg: String,
                                      day7Pos: String,
                                      day7Active: String,
                                      day7Lg: String,
                                      file: String): Boolean {
        if (roomId.isEmpty()) {
            Log.e(TAG, "insertLocalDailyReportMessage() roomId is empty")
            return false
        }
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        val room = activeSession.getRoom(roomId)
        if (room == null) {
            Log.e(TAG, "insertLocalDailyReportMessage() room is null")
            return false;
        }
        try {
            room.sendCusChartMessage(
                    deviceRate,
                    deviceRateSmall,
                    deviceRateUp,
                    connRate,
                    connRateSmall,
                    connRateUp,
                    newDeviceNum,
                    dvmNum,
                    posNum,
                    day3Pos,
                    day3Active,
                    day3Lg,
                    day7Pos,
                    day7Active,
                    day7Lg,
                    file
            )
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.e(TAG, "insertLocalDailyReportMessage() exception:${ex}")
            return false
        }
        return true
    }

    
    @SuppressLint("LogNotTimber")
    @JvmStatic
    fun insertLocalBuyDvmMessage(context: Context, roomId: String,
                                 listTitle: String,
                                 nickName: String,
                                 title: String,
                                 content: String,
                                 btStr: String,
                                 type: String,
                                 params: String = ""): Boolean {
        if (roomId.isEmpty()) {
            Log.e(TAG, "insertLocalBuyDvmMessage() roomId is empty")
            return false
        }
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        val room = activeSession.getRoom(roomId)
        if (room == null) {
            Log.e(TAG, "insertLocalBuyDvmMessage() room is null")
            return false;
        }
        try {
            room.sendCusPosMessage(listTitle, nickName, title, content, btStr, type, params)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.e(TAG, "insertLocalBuyDvmMessage() exception:${ex}")
            return false
        }
        return true
    }

    @JvmStatic
    fun noticeFirtJoinState(context: Context, roomId: String): Boolean {
        Timber.i("noticeFirtJoinState(${roomId}")
        showUpgradeAnim(context, roomId)
        return true
    }

    
    @SuppressLint("LogNotTimber")
    @JvmStatic
    fun sendCusVoteMessage(context: Context, roomId: String, params: String = "", title: String = "", time: String = ""): Boolean {
        if (roomId.isEmpty()) {
            Log.e(TAG, "sendCusVoteMessage() roomId is empty")
            return false
        }
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        val room = activeSession.getRoom(roomId)
        if (room == null) {
            Log.e(TAG, "sendCusVoteMessage() room is null")
            return false;
        }
        try {
            room.sendCusVoteMessage(params, title, time)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Log.e(TAG, "sendCusVoteMessage() exception:${ex}")
            return false
        }
        return true
    }
}
