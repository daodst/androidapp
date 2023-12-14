

package org.matrix.android.sdk.internal.session.call

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.matrix.android.sdk.api.session.call.MxCall
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class ActiveCallHandler @Inject constructor() {

    private val activeCallListLiveData: MutableLiveData<MutableList<MxCall>> by lazy {
        MutableLiveData<MutableList<MxCall>>(mutableListOf())
    }

    fun addCall(call: MxCall) {
        activeCallListLiveData.postValue(activeCallListLiveData.value?.apply { add(call) })
    }

    fun removeCall(callId: String) {
        activeCallListLiveData.postValue(activeCallListLiveData.value?.apply { removeAll { it.callId == callId } })
    }

    fun getCallWithId(callId: String): MxCall? {
        return activeCallListLiveData.value?.find { it.callId == callId }
    }

    fun getActiveCallsLiveData(): LiveData<MutableList<MxCall>> = activeCallListLiveData
}
