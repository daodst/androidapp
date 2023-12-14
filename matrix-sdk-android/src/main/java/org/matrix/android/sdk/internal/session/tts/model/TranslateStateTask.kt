

package org.matrix.android.sdk.internal.session.tts.model

import org.matrix.android.sdk.api.session.utils.model.UtilsRpcUrl
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.tts.TtsAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal interface TranslateStateTask : Task<Unit, Boolean> {

}

internal class DefaultTranslateStateTask @Inject constructor(
        private val ttsAPI: TtsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
) : TranslateStateTask {
    override suspend fun execute(params: Unit): Boolean {
        val response = executeRequest(globalErrorReceiver) {
            val url: String = UtilsRpcUrl.getUrl() + "gateway/queryinfo"
            ttsAPI.translateState(url)
        }
        return if (response.status == 0) {
            return false
        } else {
            return response.data?.ttts ?: false
        }
    }
}
