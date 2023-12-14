

package org.matrix.android.sdk.api.session.securestorage

interface KeySigner {
    fun sign(canonicalJson: String): Map<String, Map<String, String>>?
}

class EmptyKeySigner : KeySigner {
    override fun sign(canonicalJson: String): Map<String, Map<String, String>>? = null
}
