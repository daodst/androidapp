package org.matrix.android.sdk.internal.network

interface GlobalErrorInteractHandler {

    fun handlerThrowable(throwable: Throwable, roomId: String);
    fun handlerThrowable(code: String, throwable: Throwable, roomId: String);
}
