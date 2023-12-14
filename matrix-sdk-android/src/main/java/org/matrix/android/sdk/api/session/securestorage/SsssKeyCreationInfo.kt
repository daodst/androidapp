

package org.matrix.android.sdk.api.session.securestorage

data class SsssKeyCreationInfo(
        val keyId: String = "",
        val content: SecretStorageKeyContent?,
        val recoveryKey: String = "",
        val keySpec: SsssKeySpec
)
