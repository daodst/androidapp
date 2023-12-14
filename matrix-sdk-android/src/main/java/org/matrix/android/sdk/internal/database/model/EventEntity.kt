

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.Index
import org.matrix.android.sdk.api.session.crypto.model.MXEventDecryptionResult
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.threads.ThreadNotificationState
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.extensions.assertIsManaged

internal open class EventEntity(@Index var eventId: String = "",
                                @Index var roomId: String = "",
                                @Index var type: String = "",
                                var content: String? = null,
                                var prevContent: String? = null,
                                var isUseless: Boolean = false,
                                @Index var stateKey: String? = null,
                                var originServerTs: Long? = null,
                                @Index var sender: String? = null,
        
                                var sendStateDetails: String? = null,
                                var age: Long? = 0,
                                var unsignedData: String? = null,
                                var redacts: String? = null,
                                var decryptionResultJson: String? = null,
                                var ageLocalTs: Long? = null,
        
                                @Index var isRootThread: Boolean = false,
                                @Index var rootThreadEventId: String? = null,
        
                                var numberOfThreads: Int = 0,
                                var threadSummaryLatestMessage: TimelineEventEntity? = null
) : RealmObject() {

    private var sendStateStr: String = SendState.UNKNOWN.name

    var sendState: SendState
        get() {
            return SendState.valueOf(sendStateStr)
        }
        set(value) {
            sendStateStr = value.name
        }

    private var threadNotificationStateStr: String = ThreadNotificationState.NO_NEW_MESSAGE.name
    var threadNotificationState: ThreadNotificationState
        get() {
            return ThreadNotificationState.valueOf(threadNotificationStateStr)
        }
        set(value) {
            threadNotificationStateStr = value.name
        }

    var decryptionErrorCode: String? = null
        set(value) {
            if (value != field) field = value
        }

    var decryptionErrorReason: String? = null
        set(value) {
            if (value != field) field = value
        }

    companion object

    fun setDecryptionResult(result: MXEventDecryptionResult) {
        assertIsManaged()
        val decryptionResult = OlmDecryptionResult(
                payload = result.clearEvent,
                senderKey = result.senderCurve25519Key,
                keysClaimed = result.claimedEd25519Key?.let { mapOf("ed25519" to it) },
                forwardingCurve25519KeyChain = result.forwardingCurve25519KeyChain
        )
        val adapter = MoshiProvider.providesMoshi().adapter(OlmDecryptionResult::class.java)
        decryptionResultJson = adapter.toJson(decryptionResult)
        decryptionErrorCode = null
        decryptionErrorReason = null

        
        realm.where(EventInsertEntity::class.java)
                .equalTo(EventInsertEntityFields.EVENT_ID, eventId)
                .findFirst()
                ?.canBeProcessed = true
    }

    fun isThread(): Boolean = rootThreadEventId != null
}
