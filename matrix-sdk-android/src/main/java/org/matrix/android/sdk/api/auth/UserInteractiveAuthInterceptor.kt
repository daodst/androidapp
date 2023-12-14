

package org.matrix.android.sdk.api.auth

import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import kotlin.coroutines.Continuation


interface UserInteractiveAuthInterceptor {

    
    fun performStage(flowResponse: RegistrationFlowResponse, errCode: String?, promise: Continuation<UIABaseAuth>)
}
