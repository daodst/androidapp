

package org.matrix.android.sdk.internal.crypto.store.db.model

import com.squareup.moshi.JsonDataException
import io.realm.RealmObject
import io.realm.annotations.Index
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.model.MXEventDecryptionResult
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.internal.database.mapper.ContentMapper
import org.matrix.android.sdk.internal.di.MoshiProvider
import timber.log.Timber


internal open class GossipingEventEntity(@Index var type: String? = "",
                                         var content: String? = null,
                                         @Index var sender: String? = null,
                                         var decryptionResultJson: String? = null,
                                         var decryptionErrorCode: String? = null,
                                         var ageLocalTs: Long? = null) : RealmObject() {

    private var sendStateStr: String = SendState.UNKNOWN.name

    var sendState: SendState
        get() {
            return SendState.valueOf(sendStateStr)
        }
        set(value) {
            sendStateStr = value.name
        }

    companion object

    fun setDecryptionResult(result: MXEventDecryptionResult) {
        val decryptionResult = OlmDecryptionResult(
                payload = result.clearEvent,
                senderKey = result.senderCurve25519Key,
                keysClaimed = result.claimedEd25519Key?.let { mapOf("ed25519" to it) },
                forwardingCurve25519KeyChain = result.forwardingCurve25519KeyChain
        )
        val adapter = MoshiProvider.providesMoshi().adapter<OlmDecryptionResult>(OlmDecryptionResult::class.java)
        decryptionResultJson = adapter.toJson(decryptionResult)
        decryptionErrorCode = null
    }

    fun toModel(): Event {
        return Event(
                type = this.type ?: "",
                content = ContentMapper.map(this.content),
                senderId = this.sender
        ).also {
            it.ageLocalTs = this.ageLocalTs
            it.sendState = this.sendState
            this.decryptionResultJson?.let { json ->
                try {
                    it.mxDecryptionResult = MoshiProvider.providesMoshi().adapter(OlmDecryptionResult::class.java).fromJson(json)
                } catch (t: JsonDataException) {
                    Timber.e(t, "Failed to parse decryption result")
                }
            }
            
            it.mCryptoError = this.decryptionErrorCode?.let { errorCode ->
                MXCryptoError.ErrorType.valueOf(errorCode)
            }
        }
    }
}
