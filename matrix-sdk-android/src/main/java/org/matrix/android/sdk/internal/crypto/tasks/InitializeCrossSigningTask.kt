

package org.matrix.android.sdk.internal.crypto.tasks

import dagger.Lazy
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.session.crypto.crosssigning.CryptoCrossSigningKey
import org.matrix.android.sdk.api.session.crypto.crosssigning.KeyUsage
import org.matrix.android.sdk.api.util.toBase64NoPadding
import org.matrix.android.sdk.internal.auth.registration.handleUIA
import org.matrix.android.sdk.internal.crypto.MXOlmDevice
import org.matrix.android.sdk.internal.crypto.MyDeviceInfoHolder
import org.matrix.android.sdk.internal.crypto.crosssigning.canonicalSignable
import org.matrix.android.sdk.internal.crypto.model.rest.UploadSignatureQueryBuilder
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import org.matrix.olm.OlmPkSigning
import timber.log.Timber
import javax.inject.Inject

internal interface InitializeCrossSigningTask : Task<InitializeCrossSigningTask.Params, InitializeCrossSigningTask.Result> {
    data class Params(
            val interactiveAuthInterceptor: UserInteractiveAuthInterceptor?
    )

    data class Result(
            val masterKeyPK: String,
            val userKeyPK: String,
            val selfSigningKeyPK: String,
            val masterKeyInfo: CryptoCrossSigningKey,
            val userKeyInfo: CryptoCrossSigningKey,
            val selfSignedKeyInfo: CryptoCrossSigningKey
    )
}

internal class DefaultInitializeCrossSigningTask @Inject constructor(
        @UserId private val userId: String,
        private val olmDevice: MXOlmDevice,
        private val myDeviceInfoHolder: Lazy<MyDeviceInfoHolder>,
        private val uploadSigningKeysTask: UploadSigningKeysTask,
        private val uploadSignaturesTask: UploadSignaturesTask
) : InitializeCrossSigningTask {

    override suspend fun execute(params: InitializeCrossSigningTask.Params): InitializeCrossSigningTask.Result {
        var masterPkOlm: OlmPkSigning? = null
        var userSigningPkOlm: OlmPkSigning? = null
        var selfSigningPkOlm: OlmPkSigning? = null

        try {
            
            
            

            masterPkOlm = OlmPkSigning()
            val masterKeyPrivateKey = OlmPkSigning.generateSeed()
            val masterPublicKey = masterPkOlm.initWithSeed(masterKeyPrivateKey)

            Timber.v("## CrossSigning - masterPublicKey:$masterPublicKey")

            
            
            
            userSigningPkOlm = OlmPkSigning()
            val uskPrivateKey = OlmPkSigning.generateSeed()
            val uskPublicKey = userSigningPkOlm.initWithSeed(uskPrivateKey)

            Timber.v("## CrossSigning - uskPublicKey:$uskPublicKey")

            
            val signedUSK = CryptoCrossSigningKey.Builder(userId, KeyUsage.USER_SIGNING)
                    .key(uskPublicKey)
                    .build()
                    .canonicalSignable()
                    .let { masterPkOlm.sign(it) }

            
            
            
            selfSigningPkOlm = OlmPkSigning()
            val sskPrivateKey = OlmPkSigning.generateSeed()
            val sskPublicKey = selfSigningPkOlm.initWithSeed(sskPrivateKey)

            Timber.v("## CrossSigning - sskPublicKey:$sskPublicKey")

            
            val signedSSK = CryptoCrossSigningKey.Builder(userId, KeyUsage.SELF_SIGNING)
                    .key(sskPublicKey)
                    .build()
                    .canonicalSignable()
                    .let { masterPkOlm.sign(it) }

            
            val mskCrossSigningKeyInfo = CryptoCrossSigningKey.Builder(userId, KeyUsage.MASTER)
                    .key(masterPublicKey)
                    .build()
            val uploadSigningKeysParams = UploadSigningKeysTask.Params(
                    masterKey = mskCrossSigningKeyInfo,
                    userKey = CryptoCrossSigningKey.Builder(userId, KeyUsage.USER_SIGNING)
                            .key(uskPublicKey)
                            .signature(userId, masterPublicKey, signedUSK)
                            .build(),
                    selfSignedKey = CryptoCrossSigningKey.Builder(userId, KeyUsage.SELF_SIGNING)
                            .key(sskPublicKey)
                            .signature(userId, masterPublicKey, signedSSK)
                            .build(),
                    userAuthParam = null
            )

            try {
                uploadSigningKeysTask.execute(uploadSigningKeysParams)
            } catch (failure: Throwable) {
                if (params.interactiveAuthInterceptor == null ||
                        !handleUIA(
                                failure = failure,
                                interceptor = params.interactiveAuthInterceptor,
                                retryBlock = { authUpdate ->
                                    uploadSigningKeysTask.execute(uploadSigningKeysParams.copy(userAuthParam = authUpdate))
                                }
                        )
                ) {
                    Timber.d("## UIA: propagate failure")
                    throw failure
                }
            }

            
            val uploadSignatureQueryBuilder = UploadSignatureQueryBuilder()

            val myDevice = myDeviceInfoHolder.get().myDevice
            val canonicalJson = JsonCanonicalizer.getCanonicalJson(Map::class.java, myDevice.signalableJSONDictionary())
            val signedDevice = selfSigningPkOlm.sign(canonicalJson)
            val updateSignatures = (myDevice.signatures?.toMutableMap() ?: HashMap())
                    .also {
                        it[userId] = (it[userId]
                                ?: HashMap()) + mapOf("ed25519:$sskPublicKey" to signedDevice)
                    }
            myDevice.copy(signatures = updateSignatures).let {
                uploadSignatureQueryBuilder.withDeviceInfo(it)
            }

            
            val message = JsonCanonicalizer.getCanonicalJson(Map::class.java, mskCrossSigningKeyInfo.signalableJSONDictionary())
            olmDevice.signMessage(message)?.let { sign ->
                val mskUpdatedSignatures = (mskCrossSigningKeyInfo.signatures?.toMutableMap()
                        ?: HashMap()).also {
                    it[userId] = (it[userId]
                            ?: HashMap()) + mapOf("ed25519:${myDevice.deviceId}" to sign)
                }
                mskCrossSigningKeyInfo.copy(
                        signatures = mskUpdatedSignatures
                ).let {
                    uploadSignatureQueryBuilder.withSigningKeyInfo(it)
                }
            }

            
            uploadSignaturesTask.execute(UploadSignaturesTask.Params(uploadSignatureQueryBuilder.build()))

            return InitializeCrossSigningTask.Result(
                    masterKeyPK = masterKeyPrivateKey.toBase64NoPadding(),
                    userKeyPK = uskPrivateKey.toBase64NoPadding(),
                    selfSigningKeyPK = sskPrivateKey.toBase64NoPadding(),
                    masterKeyInfo = uploadSigningKeysParams.masterKey,
                    userKeyInfo = uploadSigningKeysParams.userKey,
                    selfSignedKeyInfo = uploadSigningKeysParams.selfSignedKey
            )
        } finally {
            masterPkOlm?.releaseSigning()
            userSigningPkOlm?.releaseSigning()
            selfSigningPkOlm?.releaseSigning()
        }
    }
}
