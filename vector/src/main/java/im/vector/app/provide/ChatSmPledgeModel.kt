

package im.vector.app.provide

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import im.vector.app.core.extensions.singletonEntryPoint
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.session.utils.bean.MediaInfo
import javax.inject.Inject

class ChatSmPledgeModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val mLiveData = MutableLiveData<TopMediaInfo>()
    val liveData: LiveData<TopMediaInfo> = mLiveData

    fun getMediaInfo(context: Context) {
        viewModelScope.launch {
            try {
                val result = getSuspendMediaInfo(context)
                mLiveData.postValue(TopMediaInfo(result.Username, result.CurUsedFlow, result.FlowLimit, result.UserImprove))
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getSuspendMediaInfo(context: Context): MediaInfo {
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        return activeSession.getMediaInfo()
    }

    private val mLevelLiveData = MutableLiveData<Int>()
    val liveDataLevel: LiveData<Int> = mLevelLiveData

    fun getLevel(context: Context) {
        viewModelScope.launch {
            try {
                val level = getSuspendLevel(context)
                mLevelLiveData.postValue(level)
            } catch (e: Throwable) {
                mLevelLiveData.postValue(0)
                e.printStackTrace()
            }
        }
    }

    suspend fun getSuspendLevel(context: Context): Int {
        val activeSession = context.singletonEntryPoint().activeSessionHolder().getActiveSession()
        return activeSession.getLevel(getAddressByUid(activeSession.myUserId))
    }
}
