

package org.matrix.android.sdk.internal.crypto.tasks

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.matrix.android.sdk.internal.crypto.api.CryptoApi
import org.matrix.android.sdk.internal.crypto.model.rest.DeviceKeysWithUnsigned
import org.matrix.android.sdk.internal.crypto.model.rest.KeysQueryBody
import org.matrix.android.sdk.internal.crypto.model.rest.KeysQueryResponse
import org.matrix.android.sdk.internal.crypto.model.rest.RestKeyInfo
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.computeBestChunkSize
import javax.inject.Inject

internal interface DownloadKeysForUsersTask : Task<DownloadKeysForUsersTask.Params, KeysQueryResponse> {
    data class Params(
            
            val userIds: List<String>,
            
            val token: String?
    )
}

internal class DefaultDownloadKeysForUsers @Inject constructor(
        private val cryptoApi: CryptoApi,
        private val globalErrorReceiver: GlobalErrorReceiver
) : DownloadKeysForUsersTask {

    override suspend fun execute(params: DownloadKeysForUsersTask.Params): KeysQueryResponse {
        val bestChunkSize = computeBestChunkSize(params.userIds.size, LIMIT)
        val token = params.token?.takeIf { token -> token.isNotEmpty() }

        return if (bestChunkSize.shouldChunk()) {
            
            val deviceKeys = mutableMapOf<String, Map<String, DeviceKeysWithUnsigned>>()
            val failures = mutableMapOf<String, Map<String, Any>>()
            val masterKeys = mutableMapOf<String, RestKeyInfo?>()
            val selfSigningKeys = mutableMapOf<String, RestKeyInfo?>()
            val userSigningKeys = mutableMapOf<String, RestKeyInfo?>()

            val mutex = Mutex()

            
            coroutineScope {
                params.userIds
                        .chunked(bestChunkSize.chunkSize)
                        .map {
                            KeysQueryBody(
                                    deviceKeys = it.associateWith { emptyList() },
                                    token = token
                            )
                        }
                        .map { body ->
                            async {
                                val result = executeRequest(globalErrorReceiver) {
                                    cryptoApi.downloadKeysForUsers(body)
                                }

                                mutex.withLock {
                                    deviceKeys.putAll(result.deviceKeys.orEmpty())
                                    failures.putAll(result.failures.orEmpty())
                                    masterKeys.putAll(result.masterKeys.orEmpty())
                                    selfSigningKeys.putAll(result.selfSigningKeys.orEmpty())
                                    userSigningKeys.putAll(result.userSigningKeys.orEmpty())
                                }
                            }
                        }
                        .joinAll()
            }

            KeysQueryResponse(
                    deviceKeys = deviceKeys,
                    failures = failures,
                    masterKeys = masterKeys,
                    selfSigningKeys = selfSigningKeys,
                    userSigningKeys = userSigningKeys
            )
        } else {
            
            executeRequest(globalErrorReceiver) {
                cryptoApi.downloadKeysForUsers(
                        KeysQueryBody(
                                deviceKeys = params.userIds.associateWith { emptyList() },
                                token = token
                        )
                )
            }
        }
    }

    companion object {
        const val LIMIT = 250
    }
}
