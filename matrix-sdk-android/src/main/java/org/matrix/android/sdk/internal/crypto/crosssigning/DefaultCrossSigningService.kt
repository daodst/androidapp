

package org.matrix.android.sdk.internal.crypto.crosssigning

import androidx.lifecycle.LiveData
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.crypto.crosssigning.CrossSigningService
import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustResult
import org.matrix.android.sdk.api.session.crypto.crosssigning.MXCrossSigningInfo
import org.matrix.android.sdk.api.session.crypto.crosssigning.PrivateKeysInfo
import org.matrix.android.sdk.api.session.crypto.crosssigning.UserTrustResult
import org.matrix.android.sdk.api.session.crypto.crosssigning.isCrossSignedVerified
import org.matrix.android.sdk.api.session.crypto.crosssigning.isLocallyVerified
import org.matrix.android.sdk.api.session.crypto.crosssigning.isVerified
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.api.util.fromBase64
import org.matrix.android.sdk.internal.crypto.DeviceListManager
import org.matrix.android.sdk.internal.crypto.model.rest.UploadSignatureQueryBuilder
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.tasks.InitializeCrossSigningTask
import org.matrix.android.sdk.internal.crypto.tasks.UploadSignaturesTask
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.di.WorkManagerProvider
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.task.TaskExecutor
import org.matrix.android.sdk.internal.task.TaskThread
import org.matrix.android.sdk.internal.task.configureWith
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import org.matrix.android.sdk.internal.util.logLimit
import org.matrix.android.sdk.internal.worker.WorkerParamsFactory
import org.matrix.olm.OlmPkSigning
import org.matrix.olm.OlmUtility
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SessionScope
internal class DefaultCrossSigningService @Inject constructor(
        @UserId private val userId: String,
        @SessionId private val sessionId: String,
        private val cryptoStore: IMXCryptoStore,
        private val deviceListManager: DeviceListManager,
        private val initializeCrossSigningTask: InitializeCrossSigningTask,
        private val uploadSignaturesTask: UploadSignaturesTask,
        private val taskExecutor: TaskExecutor,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val cryptoCoroutineScope: CoroutineScope,
        private val workManagerProvider: WorkManagerProvider,
        private val updateTrustWorkerDataRepository: UpdateTrustWorkerDataRepository
) : CrossSigningService,
        DeviceListManager.UserDevicesUpdateListener {

    private var olmUtility: OlmUtility? = null

    private var masterPkSigning: OlmPkSigning? = null
    private var userPkSigning: OlmPkSigning? = null
    private var selfSigningPkSigning: OlmPkSigning? = null

    init {
        try {
            olmUtility = OlmUtility()

            
            cryptoStore.getMyCrossSigningInfo()?.let { mxCrossSigningInfo ->
                Timber.i("## CrossSigning - Found Existing self signed keys")
                Timber.i("## CrossSigning - Checking if private keys are known")

                cryptoStore.getCrossSigningPrivateKeys()?.let { privateKeysInfo ->
                    privateKeysInfo.master
                            ?.fromBase64()
                            ?.let { privateKeySeed ->
                                val pkSigning = OlmPkSigning()
                                if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.masterKey()?.unpaddedBase64PublicKey) {
                                    masterPkSigning = pkSigning
                                    Timber.i("## CrossSigning - Loading master key success")
                                } else {
                                    Timber.w("## CrossSigning - Public master key does not match the private key")
                                    pkSigning.releaseSigning()
                                    
                                }
                            }
                    privateKeysInfo.user
                            ?.fromBase64()
                            ?.let { privateKeySeed ->
                                val pkSigning = OlmPkSigning()
                                if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.userKey()?.unpaddedBase64PublicKey) {
                                    userPkSigning = pkSigning
                                    Timber.i("## CrossSigning - Loading User Signing key success")
                                } else {
                                    Timber.w("## CrossSigning - Public User key does not match the private key")
                                    pkSigning.releaseSigning()
                                    
                                }
                            }
                    privateKeysInfo.selfSigned
                            ?.fromBase64()
                            ?.let { privateKeySeed ->
                                val pkSigning = OlmPkSigning()
                                if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.selfSigningKey()?.unpaddedBase64PublicKey) {
                                    selfSigningPkSigning = pkSigning
                                    Timber.i("## CrossSigning - Loading Self Signing key success")
                                } else {
                                    Timber.w("## CrossSigning - Public Self Signing key does not match the private key")
                                    pkSigning.releaseSigning()
                                    
                                }
                            }
                }

                
                setUserKeysAsTrusted(userId, checkUserTrust(userId).isVerified())
            }
        } catch (e: Throwable) {
            
            Timber.e(e, "Failed to initialize Cross Signing")
        }

        deviceListManager.addListener(this)
    }

    fun release() {
        olmUtility?.releaseUtility()
        listOf(masterPkSigning, userPkSigning, selfSigningPkSigning).forEach { it?.releaseSigning() }
        deviceListManager.removeListener(this)
    }

    protected fun finalize() {
        release()
    }

    
    override fun initializeCrossSigning(uiaInterceptor: UserInteractiveAuthInterceptor?, callback: MatrixCallback<Unit>) {
        Timber.d("## CrossSigning  initializeCrossSigning")

        val params = InitializeCrossSigningTask.Params(
                interactiveAuthInterceptor = uiaInterceptor
        )
        initializeCrossSigningTask.configureWith(params) {
            this.callbackThread = TaskThread.CRYPTO
            this.callback = object : MatrixCallback<InitializeCrossSigningTask.Result> {
                override fun onFailure(failure: Throwable) {
                    Timber.e(failure, "Error in initializeCrossSigning()")
                    callback.onFailure(failure)
                }

                override fun onSuccess(data: InitializeCrossSigningTask.Result) {
                    val crossSigningInfo = MXCrossSigningInfo(userId, listOf(data.masterKeyInfo, data.userKeyInfo, data.selfSignedKeyInfo))
                    cryptoStore.setMyCrossSigningInfo(crossSigningInfo)
                    setUserKeysAsTrusted(userId, true)
                    cryptoStore.storePrivateKeysInfo(data.masterKeyPK, data.userKeyPK, data.selfSigningKeyPK)
                    masterPkSigning = OlmPkSigning().apply { initWithSeed(data.masterKeyPK.fromBase64()) }
                    userPkSigning = OlmPkSigning().apply { initWithSeed(data.userKeyPK.fromBase64()) }
                    selfSigningPkSigning = OlmPkSigning().apply { initWithSeed(data.selfSigningKeyPK.fromBase64()) }

                    callback.onSuccess(Unit)
                }
            }
        }.executeBy(taskExecutor)
    }

    override fun onSecretMSKGossip(mskPrivateKey: String) {
        Timber.i("## CrossSigning - onSecretSSKGossip")
        val mxCrossSigningInfo = getMyCrossSigningKeys() ?: return Unit.also {
            Timber.e("## CrossSigning - onSecretMSKGossip() received secret but public key is not known")
        }

        mskPrivateKey.fromBase64()
                .let { privateKeySeed ->
                    val pkSigning = OlmPkSigning()
                    try {
                        if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.masterKey()?.unpaddedBase64PublicKey) {
                            masterPkSigning?.releaseSigning()
                            masterPkSigning = pkSigning
                            Timber.i("## CrossSigning - Loading MSK success")
                            cryptoStore.storeMSKPrivateKey(mskPrivateKey)
                            return
                        } else {
                            Timber.e("## CrossSigning - onSecretMSKGossip() private key do not match public key")
                            pkSigning.releaseSigning()
                        }
                    } catch (failure: Throwable) {
                        Timber.e("## CrossSigning - onSecretMSKGossip() ${failure.localizedMessage}")
                        pkSigning.releaseSigning()
                    }
                }
    }

    override fun onSecretSSKGossip(sskPrivateKey: String) {
        Timber.i("## CrossSigning - onSecretSSKGossip")
        val mxCrossSigningInfo = getMyCrossSigningKeys() ?: return Unit.also {
            Timber.e("## CrossSigning - onSecretSSKGossip() received secret but public key is not known")
        }

        sskPrivateKey.fromBase64()
                .let { privateKeySeed ->
                    val pkSigning = OlmPkSigning()
                    try {
                        if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.selfSigningKey()?.unpaddedBase64PublicKey) {
                            selfSigningPkSigning?.releaseSigning()
                            selfSigningPkSigning = pkSigning
                            Timber.i("## CrossSigning - Loading SSK success")
                            cryptoStore.storeSSKPrivateKey(sskPrivateKey)
                            return
                        } else {
                            Timber.e("## CrossSigning - onSecretSSKGossip() private key do not match public key")
                            pkSigning.releaseSigning()
                        }
                    } catch (failure: Throwable) {
                        Timber.e("## CrossSigning - onSecretSSKGossip() ${failure.localizedMessage}")
                        pkSigning.releaseSigning()
                    }
                }
    }

    override fun onSecretUSKGossip(uskPrivateKey: String) {
        Timber.i("## CrossSigning - onSecretUSKGossip")
        val mxCrossSigningInfo = getMyCrossSigningKeys() ?: return Unit.also {
            Timber.e("## CrossSigning - onSecretUSKGossip() received secret but public key is not knwow ")
        }

        uskPrivateKey.fromBase64()
                .let { privateKeySeed ->
                    val pkSigning = OlmPkSigning()
                    try {
                        if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.userKey()?.unpaddedBase64PublicKey) {
                            userPkSigning?.releaseSigning()
                            userPkSigning = pkSigning
                            Timber.i("## CrossSigning - Loading USK success")
                            cryptoStore.storeUSKPrivateKey(uskPrivateKey)
                            return
                        } else {
                            Timber.e("## CrossSigning - onSecretUSKGossip() private key do not match public key")
                            pkSigning.releaseSigning()
                        }
                    } catch (failure: Throwable) {
                        pkSigning.releaseSigning()
                    }
                }
    }

    override fun checkTrustFromPrivateKeys(masterKeyPrivateKey: String?,
                                           uskKeyPrivateKey: String?,
                                           sskPrivateKey: String?
    ): UserTrustResult {
        val mxCrossSigningInfo = getMyCrossSigningKeys() ?: return UserTrustResult.CrossSigningNotConfigured(userId)

        var masterKeyIsTrusted = false
        var userKeyIsTrusted = false
        var selfSignedKeyIsTrusted = false

        masterKeyPrivateKey?.fromBase64()
                ?.let { privateKeySeed ->
                    val pkSigning = OlmPkSigning()
                    try {
                        if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.masterKey()?.unpaddedBase64PublicKey) {
                            masterPkSigning?.releaseSigning()
                            masterPkSigning = pkSigning
                            masterKeyIsTrusted = true
                            Timber.i("## CrossSigning - Loading master key success")
                        } else {
                            pkSigning.releaseSigning()
                        }
                    } catch (failure: Throwable) {
                        pkSigning.releaseSigning()
                    }
                }

        uskKeyPrivateKey?.fromBase64()
                ?.let { privateKeySeed ->
                    val pkSigning = OlmPkSigning()
                    try {
                        if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.userKey()?.unpaddedBase64PublicKey) {
                            userPkSigning?.releaseSigning()
                            userPkSigning = pkSigning
                            userKeyIsTrusted = true
                            Timber.i("## CrossSigning - Loading master key success")
                        } else {
                            pkSigning.releaseSigning()
                        }
                    } catch (failure: Throwable) {
                        pkSigning.releaseSigning()
                    }
                }

        sskPrivateKey?.fromBase64()
                ?.let { privateKeySeed ->
                    val pkSigning = OlmPkSigning()
                    try {
                        if (pkSigning.initWithSeed(privateKeySeed) == mxCrossSigningInfo.selfSigningKey()?.unpaddedBase64PublicKey) {
                            selfSigningPkSigning?.releaseSigning()
                            selfSigningPkSigning = pkSigning
                            selfSignedKeyIsTrusted = true
                            Timber.i("## CrossSigning - Loading master key success")
                        } else {
                            pkSigning.releaseSigning()
                        }
                    } catch (failure: Throwable) {
                        pkSigning.releaseSigning()
                    }
                }

        if (!masterKeyIsTrusted || !userKeyIsTrusted || !selfSignedKeyIsTrusted) {
            return UserTrustResult.KeysNotTrusted(mxCrossSigningInfo)
        } else {
            cryptoStore.markMyMasterKeyAsLocallyTrusted(true)
            val checkSelfTrust = checkSelfTrust()
            if (checkSelfTrust.isVerified()) {
                cryptoStore.storePrivateKeysInfo(masterKeyPrivateKey, uskKeyPrivateKey, sskPrivateKey)
                setUserKeysAsTrusted(userId, true)
            }
            return checkSelfTrust
        }
    }

    
    override fun isUserTrusted(otherUserId: String): Boolean {
        return cryptoStore.getCrossSigningInfo(userId)?.isTrusted() == true
    }

    override fun isCrossSigningVerified(): Boolean {
        return checkSelfTrust().isVerified()
    }

    
    override fun checkUserTrust(otherUserId: String): UserTrustResult {
        Timber.v("## CrossSigning  checkUserTrust for $otherUserId")
        if (otherUserId == userId) {
            return checkSelfTrust()
        }
        
        
        

        
        val myCrossSigningInfo = cryptoStore.getCrossSigningInfo(userId)

        checkOtherMSKTrusted(myCrossSigningInfo, cryptoStore.getCrossSigningInfo(otherUserId))

        return UserTrustResult.Success
    }

    fun checkOtherMSKTrusted(myCrossSigningInfo: MXCrossSigningInfo?, otherInfo: MXCrossSigningInfo?): UserTrustResult {
        val myUserKey = myCrossSigningInfo?.userKey()
                ?: return UserTrustResult.CrossSigningNotConfigured(userId)

        if (!myCrossSigningInfo.isTrusted()) {
            return UserTrustResult.KeysNotTrusted(myCrossSigningInfo)
        }

        
        val otherMasterKey = otherInfo?.masterKey()
                ?: return UserTrustResult.UnknownCrossSignatureInfo(otherInfo?.userId ?: "")

        val masterKeySignaturesMadeByMyUserKey = otherMasterKey.signatures
                ?.get(userId) 
                ?.get("ed25519:${myUserKey.unpaddedBase64PublicKey}")

        if (masterKeySignaturesMadeByMyUserKey.isNullOrBlank()) {
            Timber.d("## CrossSigning  checkUserTrust false for ${otherInfo.userId}, not signed by my UserSigningKey")
            return UserTrustResult.KeyNotSigned(otherMasterKey)
        }

        
        try {
            olmUtility!!.verifyEd25519Signature(masterKeySignaturesMadeByMyUserKey, myUserKey.unpaddedBase64PublicKey, otherMasterKey.canonicalSignable())
        } catch (failure: Throwable) {
            return UserTrustResult.InvalidSignature(myUserKey, masterKeySignaturesMadeByMyUserKey)
        }

        return UserTrustResult.Success
    }

    private fun checkSelfTrust(): UserTrustResult {
        
        
        
        val myCrossSigningInfo = cryptoStore.getCrossSigningInfo(userId)

        return checkSelfTrust(myCrossSigningInfo, cryptoStore.getUserDeviceList(userId))
    }

    fun checkSelfTrust(myCrossSigningInfo: MXCrossSigningInfo?, myDevices: List<CryptoDeviceInfo>?): UserTrustResult {
        
        
        

        val myMasterKey = myCrossSigningInfo?.masterKey()
                ?: return UserTrustResult.CrossSigningNotConfigured(userId)

        
        
        val masterPrivateKey = cryptoStore.getCrossSigningPrivateKeys()
                ?.master
                ?.fromBase64()

        var isMaterKeyTrusted = false
        if (myMasterKey.trustLevel?.locallyVerified == true) {
            isMaterKeyTrusted = true
        } else if (masterPrivateKey != null) {
            
            var olmPkSigning: OlmPkSigning? = null
            try {
                olmPkSigning = OlmPkSigning()
                val expectedPK = olmPkSigning.initWithSeed(masterPrivateKey)
                isMaterKeyTrusted = myMasterKey.unpaddedBase64PublicKey == expectedPK
            } catch (failure: Throwable) {
                Timber.e(failure)
            }
            olmPkSigning?.releaseSigning()
        } else {
            
            myMasterKey.signatures?.get(userId)?.forEach { (key, value) ->
                val potentialDeviceId = key.removePrefix("ed25519:")
                val potentialDevice = myDevices?.firstOrNull { it.deviceId == potentialDeviceId } 
                if (potentialDevice != null && potentialDevice.isVerified) {
                    
                    try {
                        olmUtility?.verifyEd25519Signature(value, potentialDevice.fingerprint(), myMasterKey.canonicalSignable())
                        isMaterKeyTrusted = true
                        return@forEach
                    } catch (failure: Throwable) {
                        
                        Timber.w(failure, "Signature not valid?")
                    }
                }
            }
        }

        if (!isMaterKeyTrusted) {
            return UserTrustResult.KeysNotTrusted(myCrossSigningInfo)
        }

        val myUserKey = myCrossSigningInfo.userKey()
                ?: return UserTrustResult.CrossSigningNotConfigured(userId)

        val userKeySignaturesMadeByMyMasterKey = myUserKey.signatures
                ?.get(userId) 
                ?.get("ed25519:${myMasterKey.unpaddedBase64PublicKey}")

        if (userKeySignaturesMadeByMyMasterKey.isNullOrBlank()) {
            Timber.d("## CrossSigning  checkUserTrust false for $userId, USK not signed by MSK")
            return UserTrustResult.KeyNotSigned(myUserKey)
        }

        
        try {
            olmUtility!!.verifyEd25519Signature(userKeySignaturesMadeByMyMasterKey, myMasterKey.unpaddedBase64PublicKey, myUserKey.canonicalSignable())
        } catch (failure: Throwable) {
            return UserTrustResult.InvalidSignature(myUserKey, userKeySignaturesMadeByMyMasterKey)
        }

        val mySSKey = myCrossSigningInfo.selfSigningKey()
                ?: return UserTrustResult.CrossSigningNotConfigured(userId)

        val ssKeySignaturesMadeByMyMasterKey = mySSKey.signatures
                ?.get(userId) 
                ?.get("ed25519:${myMasterKey.unpaddedBase64PublicKey}")

        if (ssKeySignaturesMadeByMyMasterKey.isNullOrBlank()) {
            Timber.d("## CrossSigning  checkUserTrust false for $userId, SSK not signed by MSK")
            return UserTrustResult.KeyNotSigned(mySSKey)
        }

        
        try {
            olmUtility!!.verifyEd25519Signature(ssKeySignaturesMadeByMyMasterKey, myMasterKey.unpaddedBase64PublicKey, mySSKey.canonicalSignable())
        } catch (failure: Throwable) {
            return UserTrustResult.InvalidSignature(mySSKey, ssKeySignaturesMadeByMyMasterKey)
        }

        return UserTrustResult.Success
    }

    override fun getUserCrossSigningKeys(otherUserId: String): MXCrossSigningInfo? {
        return cryptoStore.getCrossSigningInfo(otherUserId)
    }

    override fun getLiveCrossSigningKeys(userId: String): LiveData<Optional<MXCrossSigningInfo>> {
        return cryptoStore.getLiveCrossSigningInfo(userId)
    }

    override fun getMyCrossSigningKeys(): MXCrossSigningInfo? {
        return cryptoStore.getMyCrossSigningInfo()
    }

    override fun getCrossSigningPrivateKeys(): PrivateKeysInfo? {
        return cryptoStore.getCrossSigningPrivateKeys()
    }

    override fun getLiveCrossSigningPrivateKeys(): LiveData<Optional<PrivateKeysInfo>> {
        return cryptoStore.getLiveCrossSigningPrivateKeys()
    }

    override fun canCrossSign(): Boolean {
        return checkSelfTrust().isVerified() && cryptoStore.getCrossSigningPrivateKeys()?.selfSigned != null &&
                cryptoStore.getCrossSigningPrivateKeys()?.user != null
    }

    override fun allPrivateKeysKnown(): Boolean {
        return checkSelfTrust().isVerified() &&
                cryptoStore.getCrossSigningPrivateKeys()?.allKnown().orFalse()
    }

    override fun trustUser(otherUserId: String, callback: MatrixCallback<Unit>) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            Timber.d("## CrossSigning - Mark user $userId as trusted ")
            
            val otherMasterKeys = getUserCrossSigningKeys(otherUserId)?.masterKey()
            if (otherMasterKeys == null) {
                callback.onFailure(Throwable("## CrossSigning - Other master signing key is not known"))
                return@launch
            }
            val myKeys = getUserCrossSigningKeys(userId)
            if (myKeys == null) {
                callback.onFailure(Throwable("## CrossSigning - CrossSigning is not setup for this account"))
                return@launch
            }
            val userPubKey = myKeys.userKey()?.unpaddedBase64PublicKey
            if (userPubKey == null || userPkSigning == null) {
                callback.onFailure(Throwable("## CrossSigning - Cannot sign from this account, privateKeyUnknown $userPubKey"))
                return@launch
            }

            
            val newSignature = JsonCanonicalizer.getCanonicalJson(Map::class.java,
                    otherMasterKeys.signalableJSONDictionary()).let { userPkSigning?.sign(it) }

            if (newSignature == null) {
                
                callback.onFailure(Throwable("## CrossSigning - Failed to sign"))
                return@launch
            }

            cryptoStore.setUserKeysAsTrusted(otherUserId, true)
            

            Timber.d("## CrossSigning - Upload signature of $userId MSK signed by USK")
            val uploadQuery = UploadSignatureQueryBuilder()
                    .withSigningKeyInfo(otherMasterKeys.copyForSignature(userId, userPubKey, newSignature))
                    .build()
            uploadSignaturesTask.configureWith(UploadSignaturesTask.Params(uploadQuery)) {
                this.executionThread = TaskThread.CRYPTO
                this.callback = callback
            }.executeBy(taskExecutor)
        }
    }

    override fun markMyMasterKeyAsTrusted() {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            cryptoStore.markMyMasterKeyAsLocallyTrusted(true)
            checkSelfTrust()
            
            onUsersDeviceUpdate(listOf(userId))
        }
    }

    override fun trustDevice(deviceId: String, callback: MatrixCallback<Unit>) {
        cryptoCoroutineScope.launch(coroutineDispatchers.crypto) {
            
            val device = cryptoStore.getUserDevice(userId, deviceId)
            if (device == null) {
                callback.onFailure(IllegalArgumentException("This device [$deviceId] is not known, or not yours"))
                return@launch
            }

            val myKeys = getUserCrossSigningKeys(userId)
            if (myKeys == null) {
                callback.onFailure(Throwable("CrossSigning is not setup for this account"))
                return@launch
            }

            val ssPubKey = myKeys.selfSigningKey()?.unpaddedBase64PublicKey
            if (ssPubKey == null || selfSigningPkSigning == null) {
                callback.onFailure(Throwable("Cannot sign from this account, public and/or privateKey Unknown $ssPubKey"))
                return@launch
            }

            
            val newSignature = selfSigningPkSigning?.sign(device.canonicalSignable())

            if (newSignature == null) {
                
                callback.onFailure(Throwable("Failed to sign"))
                return@launch
            }
            val toUpload = device.copy(
                    signatures = mapOf(
                            userId
                                    to
                                    mapOf(
                                            "ed25519:$ssPubKey" to newSignature
                                    )
                    )
            )

            val uploadQuery = UploadSignatureQueryBuilder()
                    .withDeviceInfo(toUpload)
                    .build()
            uploadSignaturesTask.configureWith(UploadSignaturesTask.Params(uploadQuery)) {
                this.executionThread = TaskThread.CRYPTO
                this.callback = callback
            }.executeBy(taskExecutor)
        }
    }

    override fun checkDeviceTrust(otherUserId: String, otherDeviceId: String, locallyTrusted: Boolean?): DeviceTrustResult {
        val otherDevice = cryptoStore.getUserDevice(otherUserId, otherDeviceId)
                ?: return DeviceTrustResult.UnknownDevice(otherDeviceId)

        val myKeys = getUserCrossSigningKeys(userId)
                ?: return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.CrossSigningNotConfigured(userId))

        if (!myKeys.isTrusted()) return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.KeysNotTrusted(myKeys))

        val otherKeys = getUserCrossSigningKeys(otherUserId)
                ?: return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.CrossSigningNotConfigured(otherUserId))

        
        if (!otherKeys.isTrusted()) return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.KeysNotTrusted(otherKeys))

        
        

        val otherSSKSignature = otherDevice.signatures?.get(otherUserId)?.get("ed25519:${otherKeys.selfSigningKey()?.unpaddedBase64PublicKey}")
                ?: return legacyFallbackTrust(
                        locallyTrusted,
                        DeviceTrustResult.MissingDeviceSignature(otherDeviceId, otherKeys.selfSigningKey()
                                ?.unpaddedBase64PublicKey
                                ?: ""
                        )
                )

        
        try {
            olmUtility!!.verifyEd25519Signature(otherSSKSignature, otherKeys.selfSigningKey()?.unpaddedBase64PublicKey, otherDevice.canonicalSignable())
        } catch (e: Throwable) {
            return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.InvalidDeviceSignature(otherDeviceId, otherSSKSignature, e))
        }

        return DeviceTrustResult.Success(DeviceTrustLevel(crossSigningVerified = true, locallyVerified = locallyTrusted))
    }

    fun checkDeviceTrust(myKeys: MXCrossSigningInfo?, otherKeys: MXCrossSigningInfo?, otherDevice: CryptoDeviceInfo): DeviceTrustResult {
        val locallyTrusted = otherDevice.trustLevel?.isLocallyVerified()
        myKeys ?: return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.CrossSigningNotConfigured(userId))

        if (!myKeys.isTrusted()) return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.KeysNotTrusted(myKeys))

        otherKeys ?: return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.CrossSigningNotConfigured(otherDevice.userId))

        
        if (!otherKeys.isTrusted()) return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.KeysNotTrusted(otherKeys))

        
        

        val otherSSKSignature = otherDevice.signatures?.get(otherKeys.userId)?.get("ed25519:${otherKeys.selfSigningKey()?.unpaddedBase64PublicKey}")
                ?: return legacyFallbackTrust(
                        locallyTrusted,
                        DeviceTrustResult.MissingDeviceSignature(otherDevice.deviceId, otherKeys.selfSigningKey()
                                ?.unpaddedBase64PublicKey
                                ?: ""
                        )
                )

        
        try {
            olmUtility!!.verifyEd25519Signature(otherSSKSignature, otherKeys.selfSigningKey()?.unpaddedBase64PublicKey, otherDevice.canonicalSignable())
        } catch (e: Throwable) {
            return legacyFallbackTrust(locallyTrusted, DeviceTrustResult.InvalidDeviceSignature(otherDevice.deviceId, otherSSKSignature, e))
        }

        return DeviceTrustResult.Success(DeviceTrustLevel(crossSigningVerified = true, locallyVerified = locallyTrusted))
    }

    private fun legacyFallbackTrust(locallyTrusted: Boolean?, crossSignTrustFail: DeviceTrustResult): DeviceTrustResult {
        return if (locallyTrusted == true) {
            DeviceTrustResult.Success(DeviceTrustLevel(crossSigningVerified = false, locallyVerified = true))
        } else {
            crossSignTrustFail
        }
    }

    override fun onUsersDeviceUpdate(userIds: List<String>) {
        Timber.d("## CrossSigning - onUsersDeviceUpdate for users: ${userIds.logLimit()}")
        val workerParams = UpdateTrustWorker.Params(
                sessionId = sessionId,
                filename = updateTrustWorkerDataRepository.createParam(userIds)
        )
        val workerData = WorkerParamsFactory.toData(workerParams)

        val workRequest = workManagerProvider.matrixOneTimeWorkRequestBuilder<UpdateTrustWorker>()
                .setInputData(workerData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, WorkManagerProvider.BACKOFF_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .build()

        workManagerProvider.workManager
                .beginUniqueWork("TRUST_UPDATE_QUEUE", ExistingWorkPolicy.APPEND_OR_REPLACE, workRequest)
                .enqueue()
    }

    private fun setUserKeysAsTrusted(otherUserId: String, trusted: Boolean) {
        val currentTrust = cryptoStore.getCrossSigningInfo(otherUserId)?.isTrusted()
        cryptoStore.setUserKeysAsTrusted(otherUserId, trusted)
        
        val users = ArrayList<String>()
        if (otherUserId == userId && currentTrust != trusted) {
            cryptoStore.updateUsersTrust {
                users.add(it)
                checkUserTrust(it).isVerified()
            }

            users.forEach {
                cryptoStore.getUserDeviceList(it)?.forEach { device ->
                    val updatedTrust = checkDeviceTrust(it, device.deviceId, device.trustLevel?.isLocallyVerified() ?: false)
                    Timber.v("## CrossSigning - update trust for device ${device.deviceId} of user $otherUserId , verified=$updatedTrust")
                    cryptoStore.setDeviceTrust(it, device.deviceId, updatedTrust.isCrossSignedVerified(), updatedTrust.isLocallyVerified())
                }
            }
        }
    }

}
