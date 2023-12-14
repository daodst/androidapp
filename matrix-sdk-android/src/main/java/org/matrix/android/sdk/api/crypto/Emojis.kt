

package org.matrix.android.sdk.api.crypto

import org.matrix.android.sdk.api.session.crypto.verification.EmojiRepresentation
import org.matrix.android.sdk.internal.crypto.verification.getEmojiForCode


fun getAllVerificationEmojis(): List<EmojiRepresentation> {
    return (0..63).map { getEmojiForCode(it) }
}
