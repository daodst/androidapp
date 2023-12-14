

package org.matrix.android.sdk.internal.session.log

import org.matrix.android.sdk.internal.database.model.ChatPhoneLog


interface ChatPhoneLogService {

    
    fun insert(log: ChatPhoneLog)

    
    fun queryAll(status: Int = 0): List<ChatPhoneLog>
}
