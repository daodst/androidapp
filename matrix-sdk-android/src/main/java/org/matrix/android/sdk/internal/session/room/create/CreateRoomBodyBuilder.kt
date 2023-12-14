

package org.matrix.android.sdk.internal.session.room.create

import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.api.session.identity.toMedium
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.util.MimeTypes
import org.matrix.android.sdk.internal.crypto.DeviceListManager
import org.matrix.android.sdk.internal.di.AuthenticatedIdentity
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.token.AccessTokenProvider
import org.matrix.android.sdk.internal.session.content.FileUploader
import org.matrix.android.sdk.internal.session.identity.EnsureIdentityTokenTask
import org.matrix.android.sdk.internal.session.identity.data.IdentityStore
import org.matrix.android.sdk.internal.session.identity.data.getIdentityServerUrlWithoutProtocol
import org.matrix.android.sdk.internal.session.room.membership.threepid.ThreePidInviteBody
import java.security.InvalidParameterException
import java.util.UUID
import javax.inject.Inject

internal class CreateRoomBodyBuilder @Inject constructor(
        private val ensureIdentityTokenTask: EnsureIdentityTokenTask,
        private val deviceListManager: DeviceListManager,
        private val identityStore: IdentityStore,
        private val fileUploader: FileUploader,
        @UserId
        private val userId: String,
        @AuthenticatedIdentity
        private val accessTokenProvider: AccessTokenProvider
) {

    suspend fun build(params: CreateRoomParams): CreateRoomBody {
        val invite3pids = params.invite3pids
                .takeIf { it.isNotEmpty() }
                ?.let { invites ->
                    
                    ensureIdentityTokenTask.execute(Unit)

                    val identityServerUrlWithoutProtocol = identityStore.getIdentityServerUrlWithoutProtocol()
                            ?: throw IdentityServiceError.NoIdentityServerConfigured
                    val identityServerAccessToken = accessTokenProvider.getToken() ?: throw IdentityServiceError.NoIdentityServerConfigured

                    invites.map {
                        ThreePidInviteBody(
                                idServer = identityServerUrlWithoutProtocol,
                                idAccessToken = identityServerAccessToken,
                                medium = it.toMedium(),
                                address = it.value
                        )
                    }
                }

        params.featurePreset?.updateRoomParams(params)

        val initialStates = (
                listOfNotNull(
                        buildEncryptionWithAlgorithmEvent(params),
                        buildHistoryVisibilityEvent(params),
                        buildAvatarEvent(params),
                        buildGuestAccess(params)
                ) +
                        params.featurePreset?.setupInitialStates().orEmpty() +
                        buildCustomInitialStates(params)
                )
                .takeIf { it.isNotEmpty() }

        return CreateRoomBody(
                visibility = params.visibility,
                roomAliasName = params.roomAliasName,
                name = params.name,
                topic = params.topic,
                invitedUserIds = params.invitedUserIds.filter { it != userId }.takeIf { it.isNotEmpty() },
                invite3pids = invite3pids,
                creationContent = params.creationContent.takeIf { it.isNotEmpty() },
                initialStates = initialStates,
                preset = params.preset,
                isDirect = params.isDirect,
                powerLevelContentOverride = params.powerLevelContentOverride,
                roomVersion = params.roomVersion
        )
    }

    private fun buildCustomInitialStates(params: CreateRoomParams): List<Event> {
        return params.initialStates.map {
            Event(
                    type = it.type,
                    stateKey = it.stateKey,
                    content = it.content
            )
        }
    }

    private suspend fun buildAvatarEvent(params: CreateRoomParams): Event? {
        return params.avatarUri?.let { avatarUri ->
            
            tryOrNull("Failed to upload image") {
                fileUploader.uploadFromUri(
                        uri = avatarUri,
                        filename = UUID.randomUUID().toString(),
                        mimeType = MimeTypes.Jpeg)
            }
        }?.let { response ->
            Event(
                    type = EventType.STATE_ROOM_AVATAR,
                    stateKey = "",
                    content = mapOf("url" to response.contentUri)
            )
        }
    }

    private fun buildHistoryVisibilityEvent(params: CreateRoomParams): Event? {
        return params.historyVisibility
                ?.let {
                    Event(
                            type = EventType.STATE_ROOM_HISTORY_VISIBILITY,
                            stateKey = "",
                            content = mapOf("history_visibility" to it)
                    )
                }
    }

    private fun buildGuestAccess(params: CreateRoomParams): Event? {
        return params.guestAccess
                ?.let {
                    Event(
                            type = EventType.STATE_ROOM_GUEST_ACCESS,
                            stateKey = "",
                            content = mapOf("guest_access" to it.value)
                    )
                }
    }

    
    private suspend fun buildEncryptionWithAlgorithmEvent(params: CreateRoomParams): Event? {
        if (params.algorithm == null &&
                canEnableEncryption(params)) {
            
            params.enableEncryption()
        }
        return params.algorithm
                ?.let {
                    if (it != MXCRYPTO_ALGORITHM_MEGOLM) {
                        throw InvalidParameterException("Unsupported algorithm: $it")
                    }
                    Event(
                            type = EventType.STATE_ROOM_ENCRYPTION,
                            stateKey = "",
                            content = mapOf("algorithm" to it)
                    )
                }
    }

    private suspend fun canEnableEncryption(params: CreateRoomParams): Boolean {
        return params.enableEncryptionIfInvitedUsersSupportIt &&
                
                
                
                params.invite3pids.isEmpty() &&
                params.invitedUserIds.isNotEmpty() &&
                params.invitedUserIds.let { userIds ->
                    val keys = deviceListManager.downloadKeys(userIds, forceDownload = false)

                    userIds.all { userId ->
                        keys.map[userId].let { deviceMap ->
                            if (deviceMap.isNullOrEmpty()) {
                                
                                false
                            } else {
                                
                                deviceMap.values.all { !it.keys.isNullOrEmpty() }
                            }
                        }
                    }
                }
    }
}
