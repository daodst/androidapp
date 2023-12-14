

package org.matrix.android.sdk.internal.util

import javax.crypto.SecretKey


internal data class SecretKeyAndVersion(
        
        val secretKey: SecretKey,
        
        val androidVersionWhenTheKeyHasBeenGenerated: Int)
