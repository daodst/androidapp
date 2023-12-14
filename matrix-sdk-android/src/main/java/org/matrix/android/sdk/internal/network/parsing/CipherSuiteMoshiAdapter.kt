

package org.matrix.android.sdk.internal.network.parsing

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import okhttp3.CipherSuite

internal class CipherSuiteMoshiAdapter {

    @ToJson
    fun toJson(cipherSuite: CipherSuite): String {
        return cipherSuite.javaName
    }

    @FromJson
    fun fromJson(cipherSuiteString: String): CipherSuite {
        return CipherSuite.forJavaName(cipherSuiteString)
    }
}
