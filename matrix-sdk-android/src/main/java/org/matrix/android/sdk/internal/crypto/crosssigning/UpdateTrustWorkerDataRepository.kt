

package org.matrix.android.sdk.internal.crypto.crosssigning

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.di.SessionFilesDirectory
import java.io.File
import java.util.UUID
import javax.inject.Inject

@JsonClass(generateAdapter = true)
internal data class UpdateTrustWorkerData(
        @Json(name = "userIds")
        val userIds: List<String>
)

internal class UpdateTrustWorkerDataRepository @Inject constructor(
        @SessionFilesDirectory parentDir: File
) {
    private val workingDirectory = File(parentDir, "tw")
    private val jsonAdapter = MoshiProvider.providesMoshi().adapter(UpdateTrustWorkerData::class.java)

    
    fun createParam(userIds: List<String>): String {
        val filename = "${UUID.randomUUID()}.json"
        workingDirectory.mkdirs()
        val file = File(workingDirectory, filename)

        UpdateTrustWorkerData(userIds = userIds)
                .let { jsonAdapter.toJson(it) }
                .let { file.writeText(it) }

        return filename
    }

    fun getParam(filename: String): UpdateTrustWorkerData? {
        return File(workingDirectory, filename)
                .takeIf { it.exists() }
                ?.readText()
                ?.let { jsonAdapter.fromJson(it) }
    }

    fun delete(filename: String) {
        tryOrNull("Unable to delete $filename") {
            File(workingDirectory, filename).delete()
        }
    }
}
