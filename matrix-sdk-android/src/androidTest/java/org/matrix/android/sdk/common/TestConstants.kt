

package org.matrix.android.sdk.common

import android.os.Debug

object TestConstants {

    const val TESTS_HOME_SERVER_URL = "http://10.0.2.2:8080"

    
    private const val AWAIT_TIME_OUT_MILLIS = 60_000

    
    private const val AWAIT_TIME_OUT_WITH_DEBUGGER_MILLIS = 10 * 60_000

    const val USER_ALICE = "Alice"
    const val USER_BOB = "Bob"
    const val USER_SAM = "Sam"

    const val PASSWORD = "password"

    val timeOutMillis: Long
        get() = if (Debug.isDebuggerConnected()) {
            
            AWAIT_TIME_OUT_WITH_DEBUGGER_MILLIS.toLong()
        } else {
            AWAIT_TIME_OUT_MILLIS.toLong()
        }
}
