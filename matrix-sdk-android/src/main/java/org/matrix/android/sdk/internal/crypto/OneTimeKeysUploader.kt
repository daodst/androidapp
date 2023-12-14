

package org.matrix.android.sdk.internal.crypto

import android.content.Context
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.internal.crypto.model.MXKey
import org.matrix.android.sdk.internal.crypto.model.rest.KeysUploadResponse
import org.matrix.android.sdk.internal.crypto.tasks.UploadKeysTask
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import org.matrix.olm.OlmAccount
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

private const val FALLBACK_KEY_FORGET_DELAY = 60 * 60_000L

@SessionScope
internal class OneTimeKeysUploader @Inject constructor(
        private val olmDevice: MXOlmDevice,
        private val objectSigner: ObjectSigner,
        private val uploadKeysTask: UploadKeysTask,
        context: Context
) {
    
    private var oneTimeKeyCheckInProgress = false

    
    private var lastOneTimeKeyCheck: Long = 0
    private var oneTimeKeyCount: Int? = null

    
    private val storage = context.getSharedPreferences("OneTimeKeysUploader_${olmDevice.deviceEd25519Key.hashCode()}", Context.MODE_PRIVATE)

    
    fun updateOneTimeKeyCount(currentCount: Int) {
        oneTimeKeyCount = currentCount
    }

    fun needsNewFallback() {
        if (olmDevice.generateFallbackKeyIfNeeded()) {
            
            
            
            saveLastFallbackKeyPublishTime(0L)
        }
    }

    
    suspend fun maybeUploadOneTimeKeys() {
        if (oneTimeKeyCheckInProgress) {
            Timber.v("maybeUploadOneTimeKeys: already in progress")
            return
        }
        if (System.currentTimeMillis() - lastOneTimeKeyCheck < ONE_TIME_KEY_UPLOAD_PERIOD) {
            
            Timber.v("maybeUploadOneTimeKeys: executed too recently")
            return
        }

        oneTimeKeyCheckInProgress = true

        val oneTimeKeyCountFromSync = oneTimeKeyCount
                ?: fetchOtkCount() 
                ?: return Unit.also {
                    oneTimeKeyCheckInProgress = false
                    Timber.w("maybeUploadOneTimeKeys: Failed to get otk count from server")
                }

        Timber.d("maybeUploadOneTimeKeys: otk count $oneTimeKeyCountFromSync , unpublished fallback key ${olmDevice.hasUnpublishedFallbackKey()}")

        lastOneTimeKeyCheck = System.currentTimeMillis()

        
        val maxOneTimeKeys = olmDevice.getMaxNumberOfOneTimeKeys()

        
        
        
        
        
        
        val keyLimit = floor(maxOneTimeKeys / 2.0).toInt()

        
        
        
        
        
        
        
        
        
        
        
        tryOrNull("Unable to upload OTK") {
            val uploadedKeys = uploadOTK(oneTimeKeyCountFromSync, keyLimit)
            Timber.v("## uploadKeys() : success, $uploadedKeys key(s) sent")
        }
        oneTimeKeyCheckInProgress = false

        
        val latestPublishedTime = getLastFallbackKeyPublishTime()
        if (latestPublishedTime != 0L && System.currentTimeMillis() - latestPublishedTime > FALLBACK_KEY_FORGET_DELAY) {
            
            
            Timber.d("## forgetFallbackKey()")
            olmDevice.forgetFallbackKey()
        }
    }

    private suspend fun fetchOtkCount(): Int? {
        return tryOrNull("Unable to get OTK count") {
            val result = uploadKeysTask.execute(UploadKeysTask.Params(null, null, null))
            result.oneTimeKeyCountsForAlgorithm(MXKey.KEY_SIGNED_CURVE_25519_TYPE)
        }
    }

    
    private suspend fun uploadOTK(keyCount: Int, keyLimit: Int): Int {
        if (keyLimit <= keyCount && !olmDevice.hasUnpublishedFallbackKey()) {
            
            return 0
        }
        var keysThisLoop = 0
        if (keyLimit > keyCount) {
            
            
            
            keysThisLoop = min(keyLimit - keyCount, ONE_TIME_KEY_GENERATION_MAX_NUMBER)
            olmDevice.generateOneTimeKeys(keysThisLoop)
        }

        
        val hadUnpublishedFallbackKey = olmDevice.hasUnpublishedFallbackKey()
        val response = uploadOneTimeKeys(olmDevice.getOneTimeKeys())
        olmDevice.markKeysAsPublished()
        if (hadUnpublishedFallbackKey) {
            
            saveLastFallbackKeyPublishTime(System.currentTimeMillis())
        }

        if (response.hasOneTimeKeyCountsForAlgorithm(MXKey.KEY_SIGNED_CURVE_25519_TYPE)) {
            
            return keysThisLoop +
                    uploadOTK(response.oneTimeKeyCountsForAlgorithm(MXKey.KEY_SIGNED_CURVE_25519_TYPE), keyLimit) +
                    (if (hadUnpublishedFallbackKey) 1 else 0)
        } else {
            Timber.e("## uploadOTK() : response for uploading keys does not contain one_time_key_counts.signed_curve25519")
            throw Exception("response for uploading keys does not contain one_time_key_counts.signed_curve25519")
        }
    }

    private fun saveLastFallbackKeyPublishTime(timeMillis: Long) {
        storage.edit().putLong("last_fb_key_publish", timeMillis).apply()
    }

    private fun getLastFallbackKeyPublishTime(): Long {
        return storage.getLong("last_fb_key_publish", 0)
    }

    
    private suspend fun uploadOneTimeKeys(oneTimeKeys: Map<String, Map<String, String>>?): KeysUploadResponse {
        val oneTimeJson = mutableMapOf<String, Any>()

        val curve25519Map = oneTimeKeys?.get(OlmAccount.JSON_KEY_ONE_TIME_KEY).orEmpty()

        curve25519Map.forEach { (key_id, value) ->
            val k = mutableMapOf<String, Any>()
            k["key"] = value

            
            val canonicalJson = JsonCanonicalizer.getCanonicalJson(Map::class.java, k)

            k["signatures"] = objectSigner.signObject(canonicalJson)

            oneTimeJson["signed_curve25519:$key_id"] = k
        }

        val fallbackJson = mutableMapOf<String, Any>()
        val fallbackCurve25519Map = olmDevice.getFallbackKey()?.get(OlmAccount.JSON_KEY_ONE_TIME_KEY).orEmpty()
        fallbackCurve25519Map.forEach { (key_id, key) ->
            val k = mutableMapOf<String, Any>()
            k["key"] = key
            k["fallback"] = true
            val canonicalJson = JsonCanonicalizer.getCanonicalJson(Map::class.java, k)
            k["signatures"] = objectSigner.signObject(canonicalJson)

            fallbackJson["signed_curve25519:$key_id"] = k
        }

        
        
        val uploadParams = UploadKeysTask.Params(
                deviceKeys = null,
                oneTimeKeys = oneTimeJson,
                fallbackKeys = fallbackJson.takeIf { fallbackJson.isNotEmpty() }
        )
        return uploadKeysTask.executeRetry(uploadParams, 3)
    }

    companion object {
        
        
        
        
        private const val ONE_TIME_KEY_GENERATION_MAX_NUMBER = 5

        
        private const val ONE_TIME_KEY_UPLOAD_PERIOD = (60_000).toLong() 
    }
}
