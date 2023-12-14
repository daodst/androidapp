
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.ValidVerificationInfoRequest
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState


internal interface VerificationTransport {

    
    fun <T> sendToOther(type: String,
                        verificationInfo: VerificationInfo<T>,
                        nextState: VerificationTxState,
                        onErrorReason: CancelCode,
                        onDone: (() -> Unit)?)

    
    fun sendVerificationRequest(supportedMethods: List<String>,
                                localId: String,
                                otherUserId: String,
                                roomId: String?,
                                toDevices: List<String>?,
                                callback: (String?, ValidVerificationInfoRequest?) -> Unit)

    fun cancelTransaction(transactionId: String,
                          otherUserId: String,
                          otherUserDeviceId: String?,
                          code: CancelCode)

    fun done(transactionId: String,
             onDone: (() -> Unit)?)

    
    fun createAccept(tid: String,
                     keyAgreementProtocol: String,
                     hash: String,
                     commitment: String,
                     messageAuthenticationCode: String,
                     shortAuthenticationStrings: List<String>): VerificationInfoAccept

    fun createKey(tid: String,
                  pubKey: String): VerificationInfoKey

    
    fun createStartForSas(fromDevice: String,
                          transactionId: String,
                          keyAgreementProtocols: List<String>,
                          hashes: List<String>,
                          messageAuthenticationCodes: List<String>,
                          shortAuthenticationStrings: List<String>): VerificationInfoStart

    
    fun createStartForQrCode(fromDevice: String,
                             transactionId: String,
                             sharedSecret: String): VerificationInfoStart

    fun createMac(tid: String, mac: Map<String, String>, keys: String): VerificationInfoMac

    fun createReady(tid: String,
                    fromDevice: String,
                    methods: List<String>): VerificationInfoReady

    
    fun sendVerificationReady(keyReq: VerificationInfoReady,
                              otherUserId: String,
                              otherDeviceId: String?,
                              callback: (() -> Unit)?)
}
