

package org.matrix.android.sdk.internal.crypto.repository

import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class WarnOnUnknownDeviceRepository @Inject constructor() {

    
    
    private var warnOnUnknownDevices = false

    
    fun warnOnUnknownDevices() = warnOnUnknownDevices

    
    fun setWarnOnUnknownDevices(warn: Boolean) {
        warnOnUnknownDevices = warn
    }
}
