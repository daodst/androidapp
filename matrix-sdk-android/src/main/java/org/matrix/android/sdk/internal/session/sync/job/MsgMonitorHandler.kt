

package org.matrix.android.sdk.internal.session.sync.job

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.matrix.android.sdk.api.session.call.MxCall
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject


@SessionScope
internal class MsgMonitorHandler @Inject constructor() {

    private val msgMonitorLiveData: MutableLiveData<MutableList<MxCall>> by lazy {
        MutableLiveData<MutableList<MxCall>>(mutableListOf())
    }

    fun addCall(call: MxCall) {
        msgMonitorLiveData.postValue(msgMonitorLiveData.value?.apply { add(call) })
    }

    fun removeCall(callId: String) {
        msgMonitorLiveData.postValue(msgMonitorLiveData.value?.apply { removeAll { it.callId == callId } })
    }

    fun getCallWithId(callId: String): MxCall? {
        return msgMonitorLiveData.value?.find { it.callId == callId }
    }

    fun getActiveCallsLiveData(): LiveData<MutableList<MxCall>> = msgMonitorLiveData
}
