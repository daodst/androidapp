package org.matrix.android.sdk.internal.network

import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
class GlobalErrorInteractImpl @Inject constructor() : GlobalErrorInteract {

    private val listener: MutableList<GlobalErrorInteractHandler>

    init {
        listener = ArrayList()
    }

    override fun addGlobalErrorListener(handler: GlobalErrorInteractHandler) {
        listener.add(handler)
    }

    override fun removeGlobalErrorListener(handler: GlobalErrorInteractHandler) {
        listener.remove(handler)
    }

    override fun getGlobalErrorInteractHandler(): List<GlobalErrorInteractHandler> {
        return listener
    }
}
