

package org.matrix.android.sdk.internal.network.parsing

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import okhttp3.TlsVersion

internal class TlsVersionMoshiAdapter {

    @ToJson
    fun toJson(tlsVersion: TlsVersion): String {
        return tlsVersion.javaName
    }

    @FromJson
    fun fromJson(tlsVersionString: String): TlsVersion {
        return TlsVersion.forJavaName(tlsVersionString)
    }
}
