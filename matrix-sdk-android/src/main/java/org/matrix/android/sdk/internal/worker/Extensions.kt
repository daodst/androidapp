

package org.matrix.android.sdk.internal.worker

import androidx.work.OneTimeWorkRequest
import org.matrix.android.sdk.internal.session.room.send.NoMerger


internal fun OneTimeWorkRequest.Builder.startChain(startChain: Boolean): OneTimeWorkRequest.Builder {
    if (startChain) {
        setInputMerger(NoMerger::class.java)
    }
    return this
}
