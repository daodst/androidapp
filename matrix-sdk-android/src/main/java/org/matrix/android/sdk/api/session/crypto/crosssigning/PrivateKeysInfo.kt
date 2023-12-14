

package org.matrix.android.sdk.api.session.crypto.crosssigning

data class PrivateKeysInfo(
        val master: String? = null,
        val selfSigned: String? = null,
        val user: String? = null
) {
    fun allKnown() = master != null && selfSigned != null && user != null
}
