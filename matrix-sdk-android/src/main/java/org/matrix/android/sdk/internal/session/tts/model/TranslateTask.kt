

package org.matrix.android.sdk.internal.session.tts.model

import org.matrix.android.sdk.api.session.utils.model.UtilsRpcUrl
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.network.token.HomeserverAccessTokenProvider
import org.matrix.android.sdk.internal.session.tts.TtsAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject


internal interface TranslateTask : Task<TranslateTask.Params, String> {

    data class Params(
            val text: String,
            val lan: String,
    )
}

internal class DefaultTranslateTask @Inject constructor(
        private val ttsAPI: TtsAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        private val accessTokenProvider: HomeserverAccessTokenProvider
) : TranslateTask {

    override suspend fun execute(params: TranslateTask.Params): String {
        val response = executeRequest(globalErrorReceiver) {
            val url: String = UtilsRpcUrl.getTranslateUrl() + "translate"
            val param = mutableMapOf<String, String>()
            
            param["lan"] = params.lan
            param["text"] = params.text
            ttsAPI.translate(url, param, accessTokenProvider.getToken() ?: "")
        }
        
        return if (response.error == 0) {
            response.text
        } else {
            throw RuntimeException("translate error,later retry")
        }
    }
}
