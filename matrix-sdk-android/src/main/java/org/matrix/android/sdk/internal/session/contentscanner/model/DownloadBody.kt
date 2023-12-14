

package org.matrix.android.sdk.internal.session.contentscanner.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.util.JsonCanonicalizer

@JsonClass(generateAdapter = true)
internal data class DownloadBody(
        @Json(name = "file") val file: EncryptedFileInfo? = null,
        @Json(name = "encrypted_body") val encryptedBody: EncryptedBody? = null
)

@JsonClass(generateAdapter = true)
internal data class EncryptedBody(
        @Json(name = "ciphertext") val cipherText: String,
        @Json(name = "mac") val mac: String,
        @Json(name = "ephemeral") val ephemeral: String
)

internal fun DownloadBody.toJson(): String = MoshiProvider.providesMoshi().adapter(DownloadBody::class.java).toJson(this)

internal fun DownloadBody.toCanonicalJson() = JsonCanonicalizer.getCanonicalJson(DownloadBody::class.java, this)
