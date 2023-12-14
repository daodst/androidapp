

package org.matrix.android.sdk.internal.crypto.store.db.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import io.realm.RealmObject
import io.realm.annotations.Index
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.crypto.model.OutgoingGossipingRequestState
import org.matrix.android.sdk.api.session.crypto.model.OutgoingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.RoomKeyRequestBody
import org.matrix.android.sdk.internal.crypto.GossipRequestType
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequest
import org.matrix.android.sdk.internal.crypto.OutgoingSecretRequest
import org.matrix.android.sdk.internal.di.MoshiProvider

internal open class OutgoingGossipingRequestEntity(
        @Index var requestId: String? = null,
        var recipientsData: String? = null,
        var requestedInfoStr: String? = null,
        @Index var typeStr: String? = null
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

    private var requestStateStr: String = OutgoingGossipingRequestState.UNSENT.name

    var requestState: OutgoingGossipingRequestState
        get() {
            return tryOrNull { OutgoingGossipingRequestState.valueOf(requestStateStr) }
                    ?: OutgoingGossipingRequestState.UNSENT
        }
        set(value) {
            requestStateStr = value.name
        }

    companion object {

        private val recipientsDataMapper: JsonAdapter<Map<String, List<String>>> =
                MoshiProvider
                        .providesMoshi()
                        .adapter<Map<String, List<String>>>(
                                Types.newParameterizedType(Map::class.java, String::class.java, List::class.java)
                        )
    }

    fun toOutgoingGossipingRequest(): OutgoingGossipingRequest {
        return when (type) {
            GossipRequestType.KEY    -> {
                OutgoingRoomKeyRequest(
                        requestBody = getRequestedKeyInfo(),
                        recipients = getRecipients().orEmpty(),
                        requestId = requestId ?: "",
                        state = requestState
                )
            }
            GossipRequestType.SECRET -> {
                OutgoingSecretRequest(
                        secretName = getRequestedSecretName(),
                        recipients = getRecipients().orEmpty(),
                        requestId = requestId ?: "",
                        state = requestState
                )
            }
        }
    }

    private fun getRecipients(): Map<String, List<String>>? {
        return this.recipientsData?.let { recipientsDataMapper.fromJson(it) }
    }

    fun setRecipients(recipients: Map<String, List<String>>) {
        this.recipientsData = recipientsDataMapper.toJson(recipients)
    }
}
