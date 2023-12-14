

package org.matrix.android.sdk.api.session.crypto.verification

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class EmojiRepresentation(val emoji: String,
                               @StringRes val nameResId: Int,
                               @DrawableRes val drawableRes: Int? = null
)
