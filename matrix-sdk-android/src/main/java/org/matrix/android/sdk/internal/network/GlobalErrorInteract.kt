package org.matrix.android.sdk.internal.network

interface GlobalErrorInteract {

    fun addGlobalErrorListener(handler: GlobalErrorInteractHandler)

    fun removeGlobalErrorListener(handler: GlobalErrorInteractHandler)

    fun getGlobalErrorInteractHandler(): List<GlobalErrorInteractHandler>
}
