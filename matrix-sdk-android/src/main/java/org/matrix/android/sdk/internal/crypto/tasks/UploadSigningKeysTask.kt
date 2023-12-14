

package org.matrix.android.sdk.internal.crypto.tasks

import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.crypto.crosssigning.CryptoCrossSigningKey
import org.matrix.android.sdk.api.session.crypto.crosssigning.toRest
import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.crypto.model.rest.UploadSigningKeysBody
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface UploadSigningKeysTask : Task<UploadSigningKeysTask.Params, Unit> {
    data class Params(
            
            val masterKey: CryptoCrossSigningKey,
            
            val userKey: CryptoCrossSigningKey,
            
            val selfSignedKey: CryptoCrossSigningKey,
            
            val userAuthParam: UIABaseAuth?
    )
}

internal data class UploadSigningKeys(val failures: Map<String, Any>?) : Failure.FeatureFailure()

internal class DefaultUploadSigningKeysTask @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : UploadSigningKeysTask {

    override suspend fun execute(params: UploadSigningKeysTask.Params) {
        val uploadQuery = UploadSigningKeysBody(
                masterKey = params.masterKey.toRest(),
                userSigningKey = params.userKey.toRest(),
                selfSigningKey = params.selfSignedKey.toRest(),
                auth = params.userAuthParam?.asMap()
        )
        doRequest(uploadQuery)
    }

    private suspend fun doRequest(uploadQuery: UploadSigningKeysBody) {
        val keysQueryResponse = executeRequest(globalErrorReceiver) {
            cryptoApi.uploadSigningKeys(uploadQuery)
        }
        if (keysQueryResponse.failures?.isNotEmpty() == true) {
            throw UploadSigningKeys(keysQueryResponse.failures)
        }
    }
}
