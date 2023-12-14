

package org.matrix.android.sdk.internal.crypto.algorithms.megolm

import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap
import timber.log.Timber

internal class MXOutboundSessionInfo(
        
        val sessionId: String,
        val sharedWithHelper: SharedWithHelper,
        
        private val creationTime: Long = System.currentTimeMillis()) {

    
    var useCount: Int = 0

    fun needsRotation(rotationPeriodMsgs: Int, rotationPeriodMs: Int): Boolean {
        var needsRotation = false
        val sessionLifetime = System.currentTimeMillis() - creationTime

        if (useCount >= rotationPeriodMsgs || sessionLifetime >= rotationPeriodMs) {
            Timber.v("## needsRotation() : Rotating megolm session after $useCount, ${sessionLifetime}ms")
            needsRotation = true
        }

        return needsRotation
    }

    
    fun sharedWithTooManyDevices(devicesInRoom: MXUsersDevicesMap<CryptoDeviceInfo>): Boolean {
        val sharedWithDevices = sharedWithHelper.sharedWithDevices()
        val userIds = sharedWithDevices.userIds

        for (userId in userIds) {
            if (null == devicesInRoom.getUserDeviceIds(userId)) {
                Timber.v("## sharedWithTooManyDevices() : Starting new session because we shared with $userId")
                return true
            }

            val deviceIds = sharedWithDevices.getUserDeviceIds(userId)

            for (deviceId in deviceIds!!) {
                if (null == devicesInRoom.getObject(userId, deviceId)) {
                    Timber.v("## sharedWithTooManyDevices() : Starting new session because we shared with $userId:$deviceId")
                    return true
                }
            }
        }

        return false
    }
}
