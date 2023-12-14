package org.matrix.android.sdk.internal.session.tts

interface TtsService {

    suspend fun translate(text: String, lan: String): String
    suspend fun translateIsOpen(): Boolean
}
