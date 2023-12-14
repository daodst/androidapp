

package org.matrix.android.sdk.api.session.permalinks

import android.net.Uri
import android.net.UrlQuerySanitizer
import org.matrix.android.sdk.api.MatrixPatterns
import timber.log.Timber
import java.net.URLDecoder


object PermalinkParser {

    
    fun parse(uriString: String): PermalinkData {
        val uri = Uri.parse(uriString)
        return parse(uri)
    }

    
    fun parse(uri: Uri): PermalinkData {
        
        
        
        val matrixToUri = MatrixToConverter.convert(uri) ?: return PermalinkData.FallbackLink(uri)

        
        
        val fragment = matrixToUri.toString().substringAfter("#") 
        if (fragment.isEmpty()) {
            return PermalinkData.FallbackLink(uri)
        }
        val safeFragment = fragment.substringBefore('?')
        val viaQueryParameters = fragment.getViaParameters()

        
        val params = safeFragment
                .split(MatrixPatterns.SEP_REGEX)
                .filter { it.isNotEmpty() }
                .map { URLDecoder.decode(it, "UTF-8") }
                .take(2)

        val identifier = params.getOrNull(0)
        val extraParameter = params.getOrNull(1)
        return when {
            identifier.isNullOrEmpty()             -> PermalinkData.FallbackLink(uri)
            MatrixPatterns.isUserId(identifier)    -> PermalinkData.UserLink(userId = identifier)
            MatrixPatterns.isGroupId(identifier)   -> PermalinkData.GroupLink(groupId = identifier)
            MatrixPatterns.isRoomId(identifier)    -> {
                handleRoomIdCase(fragment, identifier, matrixToUri, extraParameter, viaQueryParameters)
            }
            MatrixPatterns.isRoomAlias(identifier) -> {
                PermalinkData.RoomLink(
                        roomIdOrAlias = identifier,
                        isRoomAlias = true,
                        eventId = extraParameter.takeIf { !it.isNullOrEmpty() && MatrixPatterns.isEventId(it) },
                        viaParameters = viaQueryParameters
                )
            }
            else                                   -> PermalinkData.FallbackLink(uri)
        }
    }

    private fun handleRoomIdCase(fragment: String, identifier: String, uri: Uri, extraParameter: String?, viaQueryParameters: List<String>): PermalinkData {
        
        val paramList = safeExtractParams(fragment)
        val signUrl = paramList.firstOrNull { it.first == "signurl" }?.second
        val email = paramList.firstOrNull { it.first == "email" }?.second
        return if (signUrl.isNullOrEmpty().not() && email.isNullOrEmpty().not()) {
            try {
                val signValidUri = Uri.parse(signUrl)
                val identityServerHost = signValidUri.authority ?: throw IllegalArgumentException()
                val token = signValidUri.getQueryParameter("token") ?: throw IllegalArgumentException()
                val privateKey = signValidUri.getQueryParameter("private_key") ?: throw IllegalArgumentException()
                PermalinkData.RoomEmailInviteLink(
                        roomId = identifier,
                        email = email!!,
                        signUrl = signUrl!!,
                        roomName = paramList.firstOrNull { it.first == "room_name" }?.second,
                        inviterName = paramList.firstOrNull { it.first == "inviter_name" }?.second,
                        roomAvatarUrl = paramList.firstOrNull { it.first == "room_avatar_url" }?.second,
                        roomType = paramList.firstOrNull { it.first == "room_type" }?.second,
                        identityServer = identityServerHost,
                        token = token,
                        privateKey = privateKey
                )
            } catch (failure: Throwable) {
                Timber.i("## Permalink: Failed to parse permalink $signUrl")
                PermalinkData.FallbackLink(uri)
            }
        } else {
            PermalinkData.RoomLink(
                    roomIdOrAlias = identifier,
                    isRoomAlias = false,
                    eventId = extraParameter.takeIf { !it.isNullOrEmpty() && MatrixPatterns.isEventId(it) },
                    viaParameters = viaQueryParameters
            )
        }
    }

    private fun safeExtractParams(fragment: String) =
            fragment.substringAfter("?").split('&').mapNotNull {
                val splitNameValue = it.split("=")
                if (splitNameValue.size == 2) {
                    Pair(splitNameValue[0], URLDecoder.decode(splitNameValue[1], "UTF-8"))
                } else null
            }

    private fun String.getViaParameters(): List<String> {
        return UrlQuerySanitizer(this)
                .parameterList
                .filter {
                    it.mParameter == "via"
                }.map {
                    URLDecoder.decode(it.mValue, "UTF-8")
                }
    }
}
