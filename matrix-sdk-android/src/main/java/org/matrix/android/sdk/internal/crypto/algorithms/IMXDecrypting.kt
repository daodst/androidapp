

package org.matrix.android.sdk.internal.crypto.algorithms

import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.IncomingSecretShareRequest
import org.matrix.android.sdk.api.session.crypto.model.MXEventDecryptionResult
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.internal.crypto.keysbackup.DefaultKeysBackupService


internal interface IMXDecrypting {

    
    @Throws(MXCryptoError::class)
    suspend fun decryptEvent(event: Event, timeline: String): MXEventDecryptionResult

    
    fun onRoomKeyEvent(event: Event, defaultKeysBackupService: DefaultKeysBackupService) {}

    
    fun hasKeysForKeyRequest(request: IncomingRoomKeyRequest): Boolean = false

    
    fun shareKeysWithDevice(request: IncomingRoomKeyRequest) {}

    fun shareSecretWithDevice(request: IncomingSecretShareRequest, secretValue: String) {}

    fun requestKeysForEvent(event: Event, withHeld: Boolean)
}
