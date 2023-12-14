

package org.matrix.android.sdk.internal.crypto.verification.qrcode

fun hexToByteArray(hex: String): ByteArray {
    
    return hex.replace(" ", "")
            .let {
                if (it.length % 2 != 0) "0$it" else it
            }
            .let {
                ByteArray(it.length / 2)
                        .apply {
                            for (i in this.indices) {
                                val index = i * 2
                                val v = it.substring(index, index + 2).toInt(16)
                                this[i] = v.toByte()
                            }
                        }
            }
}
