

package org.matrix.android.sdk.api.session.crypto.verification

interface SasVerificationTransaction : VerificationTransaction {

    fun supportsEmoji(): Boolean

    fun supportsDecimal(): Boolean

    fun getEmojiCodeRepresentation(): List<EmojiRepresentation>

    fun getDecimalCodeRepresentation(): String

    
    fun userHasVerifiedShortCode()

    fun shortCodeDoesNotMatch()
}
