

package org.matrix.android.sdk.api

import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.internal.util.removeInvalidRoomNameChars
import org.matrix.android.sdk.internal.util.replaceSpaceChars
import timber.log.Timber


object MatrixPatterns {

    
    private const val DOMAIN_REGEX = ":[A-Z0-9.-]+(:[0-9]{2,5})?"

    
    
    private const val MATRIX_USER_IDENTIFIER_REGEX = "@[A-Z0-9\\x21-\\x39\\x3B-\\x7F]+$DOMAIN_REGEX"
    val PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER = MATRIX_USER_IDENTIFIER_REGEX.toRegex(RegexOption.IGNORE_CASE)

    
    private const val MATRIX_ROOM_IDENTIFIER_REGEX = "![A-Z0-9]+$DOMAIN_REGEX"
    private val PATTERN_CONTAIN_MATRIX_ROOM_IDENTIFIER = MATRIX_ROOM_IDENTIFIER_REGEX.toRegex(RegexOption.IGNORE_CASE)

    
    private const val MATRIX_ROOM_ALIAS_REGEX = "#[A-Z0-9._%#@=+-]+$DOMAIN_REGEX"
    private val PATTERN_CONTAIN_MATRIX_ALIAS = MATRIX_ROOM_ALIAS_REGEX.toRegex(RegexOption.IGNORE_CASE)

    
    private const val MATRIX_EVENT_IDENTIFIER_REGEX = "\\$[A-Z0-9]+$DOMAIN_REGEX"
    private val PATTERN_CONTAIN_MATRIX_EVENT_IDENTIFIER = MATRIX_EVENT_IDENTIFIER_REGEX.toRegex(RegexOption.IGNORE_CASE)

    
    private const val MATRIX_EVENT_IDENTIFIER_V3_REGEX = "\\$[A-Z0-9/+]+"
    private val PATTERN_CONTAIN_MATRIX_EVENT_IDENTIFIER_V3 = MATRIX_EVENT_IDENTIFIER_V3_REGEX.toRegex(RegexOption.IGNORE_CASE)

    
    private const val MATRIX_EVENT_IDENTIFIER_V4_REGEX = "\\$[A-Z0-9\\-_]+"
    private val PATTERN_CONTAIN_MATRIX_EVENT_IDENTIFIER_V4 = MATRIX_EVENT_IDENTIFIER_V4_REGEX.toRegex(RegexOption.IGNORE_CASE)

    
    private const val MATRIX_GROUP_IDENTIFIER_REGEX = "\\+[A-Z0-9=_\\-./]+$DOMAIN_REGEX"
    private val PATTERN_CONTAIN_MATRIX_GROUP_IDENTIFIER = MATRIX_GROUP_IDENTIFIER_REGEX.toRegex(RegexOption.IGNORE_CASE)

    
    
    private const val PERMALINK_BASE_REGEX = "https://matrix\\.to/#/"
    private const val APP_BASE_REGEX = "https://[A-Z0-9.-]+\\.[A-Z]{2,}/[A-Z]{3,}/#/room/"
    const val SEP_REGEX = "/"

    private const val LINK_TO_ROOM_ID_REGEXP = PERMALINK_BASE_REGEX + MATRIX_ROOM_IDENTIFIER_REGEX + SEP_REGEX + MATRIX_EVENT_IDENTIFIER_REGEX
    private val PATTERN_CONTAIN_MATRIX_TO_PERMALINK_ROOM_ID = LINK_TO_ROOM_ID_REGEXP.toRegex(RegexOption.IGNORE_CASE)

    private const val LINK_TO_ROOM_ALIAS_REGEXP = PERMALINK_BASE_REGEX + MATRIX_ROOM_ALIAS_REGEX + SEP_REGEX + MATRIX_EVENT_IDENTIFIER_REGEX
    private val PATTERN_CONTAIN_MATRIX_TO_PERMALINK_ROOM_ALIAS = LINK_TO_ROOM_ALIAS_REGEXP.toRegex(RegexOption.IGNORE_CASE)

    private const val LINK_TO_APP_ROOM_ID_REGEXP = APP_BASE_REGEX + MATRIX_ROOM_IDENTIFIER_REGEX + SEP_REGEX + MATRIX_EVENT_IDENTIFIER_REGEX
    private val PATTERN_CONTAIN_APP_LINK_PERMALINK_ROOM_ID = LINK_TO_APP_ROOM_ID_REGEXP.toRegex(RegexOption.IGNORE_CASE)

    private const val LINK_TO_APP_ROOM_ALIAS_REGEXP = APP_BASE_REGEX + MATRIX_ROOM_ALIAS_REGEX + SEP_REGEX + MATRIX_EVENT_IDENTIFIER_REGEX
    private val PATTERN_CONTAIN_APP_LINK_PERMALINK_ROOM_ALIAS = LINK_TO_APP_ROOM_ALIAS_REGEXP.toRegex(RegexOption.IGNORE_CASE)

    
    val ORDER_STRING_REGEX = "[ -~]+".toRegex()

    
    val MATRIX_PATTERNS = listOf(
            PATTERN_CONTAIN_MATRIX_TO_PERMALINK_ROOM_ID,
            PATTERN_CONTAIN_MATRIX_TO_PERMALINK_ROOM_ALIAS,
            PATTERN_CONTAIN_APP_LINK_PERMALINK_ROOM_ID,
            PATTERN_CONTAIN_APP_LINK_PERMALINK_ROOM_ALIAS,
            PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER,
            PATTERN_CONTAIN_MATRIX_ALIAS,
            PATTERN_CONTAIN_MATRIX_ROOM_IDENTIFIER,
            PATTERN_CONTAIN_MATRIX_EVENT_IDENTIFIER,
            PATTERN_CONTAIN_MATRIX_GROUP_IDENTIFIER
    )

    
    fun isUserId(str: String?): Boolean {
        return str != null && str matches PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER
    }

    
    fun isRoomId(str: String?): Boolean {
        return str != null && str matches PATTERN_CONTAIN_MATRIX_ROOM_IDENTIFIER
    }

    
    fun isRoomAlias(str: String?): Boolean {
        return str != null && str matches PATTERN_CONTAIN_MATRIX_ALIAS
    }

    
    fun isEventId(str: String?): Boolean {
        return str != null &&
                (str matches PATTERN_CONTAIN_MATRIX_EVENT_IDENTIFIER ||
                        str matches PATTERN_CONTAIN_MATRIX_EVENT_IDENTIFIER_V3 ||
                        str matches PATTERN_CONTAIN_MATRIX_EVENT_IDENTIFIER_V4)
    }

    
    fun isGroupId(str: String?): Boolean {
        return str != null && str matches PATTERN_CONTAIN_MATRIX_GROUP_IDENTIFIER
    }

    
    fun extractServerNameFromId(matrixId: String?): String? {
        return matrixId?.substringAfter(":", missingDelimiterValue = "")?.takeIf { it.isNotEmpty() }
    }

    
    fun isValidOrderString(order: String?): Boolean {
        return order != null && order.length < 50 && order matches ORDER_STRING_REGEX
    }

    fun candidateAliasFromRoomName(roomName: String, domain: String): String {
        return roomName.lowercase()
                .replaceSpaceChars(replacement = "_")
                .removeInvalidRoomNameChars()
                .take(MatrixConstants.maxAliasLocalPartLength(domain))
    }

    
    fun String.getDomain(): String {
        if (BuildConfig.DEBUG && !isUserId(this)) {
            
            Timber.w("Not a valid user ID: $this")
        }
        return substringAfter(":")
    }
}
