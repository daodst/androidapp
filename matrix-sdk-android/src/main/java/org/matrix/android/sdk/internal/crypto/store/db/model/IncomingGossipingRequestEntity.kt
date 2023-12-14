

package org.matrix.android.sdk.internal.crypto.store.db.model

import io.realm.RealmObject
import io.realm.annotations.Index
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.crypto.model.GossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.IncomingSecretShareRequest
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyRequestBody
import org.matrix.android.sdk.internal.crypto.GossipRequestType
import org.matrix.android.sdk.internal.crypto.IncomingShareRequestCommon

internal open class IncomingGossipingRequestEntity(@Index var requestId: String? = "",
                                                   @Index var typeStr: String? = null,
                                                   var otherUserId: String? = null,
                                                   var requestedInfoStr: String? = null,
                                                   var otherDeviceId: String? = null,
                                                   var localCreationTimestamp: Long? = null
) : RealmObject() {

    fun getRequestedSecretName(): String? = if (type == GossipRequestType.SECRET) {
        requestedInfoStr
    } else null

    fun getRequestedKeyInfo(): RoomKeyRequestBody? = if (type == GossipRequestType.KEY) {
        RoomKeyRequestBody.fromJson(requestedInfoStr)
    } else null

    var type: GossipRequestType
        get() {
            return tryOrNull { typeStr?.let { GossipRequestType.valueOf(it) } } ?: GossipRequestType.KEY
        }
        set(value) {
            typeStr = value.name
        }

    private var requestStateStr: String = GossipingRequestState.NONE.name

    var requestState: GossipingRequestState
        get() {
            return tryOrNull { GossipingRequestState.valueOf(requestStateStr) }
                    ?: GossipingRequestState.NONE
        }
        set(value) {
            requestStateStr = value.name
        }

    companion object

    fun toIncomingGossipingRequest(): IncomingShareRequestCommon {
        return when (type) {
            GossipRequestType.KEY    -> {
                IncomingRoomKeyRequest(
                        requestBody = getRequestedKeyInfo(),
                        deviceId = otherDeviceId,
                        userId = otherUserId,
                        requestId = requestId,
                        state = requestState,
                        localCreationTimestamp = localCreationTimestamp
                )
            }
            GossipRequestType.SECRET -> {
                IncomingSecretShareRequest(
                        secretName = getRequestedSecretName(),
                        deviceId = otherDeviceId,
                        userId = otherUserId,
                        requestId = requestId,
                        localCreationTimestamp = localCreationTimestamp
                )
            }
        }
    }
}
