package org.matrix.android.sdk.internal.session.tts

import org.matrix.android.sdk.internal.session.tts.model.TranslateStateTask
import org.matrix.android.sdk.internal.session.tts.model.TranslateTask
import javax.inject.Inject

internal class DefaultTtsService @Inject constructor(
        private val translateTask: TranslateTask,
        private val translateStateTask: TranslateStateTask,
) : TtsService {
    override suspend fun translate(text: String, lan: String): String {
        val params = TranslateTask.Params(text, lan)
        return translateTask.execute(params)
    }

    override suspend fun translateIsOpen(): Boolean {
        return translateStateTask.execute(Unit)
    }
}
