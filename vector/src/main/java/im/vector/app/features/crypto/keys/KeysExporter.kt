

package im.vector.app.features.crypto.keys

import android.content.Context
import android.net.Uri
import im.vector.app.core.dispatchers.CoroutineDispatchers
import im.vector.app.core.extensions.safeOpenOutputStream
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject

class KeysExporter @Inject constructor(
        private val session: Session,
        private val context: Context,
        private val dispatchers: CoroutineDispatchers
) {
    
    suspend fun export(password: String, uri: Uri) {
        withContext(dispatchers.io) {
            val data = session.cryptoService().exportRoomKeys(password)
            context.safeOpenOutputStream(uri)
                    ?.use { it.write(data) }
                    ?: throw IllegalStateException("Unable to open file for writing")
            verifyExportedKeysOutputFileSize(uri, expectedSize = data.size.toLong())
        }
    }

    private fun verifyExportedKeysOutputFileSize(uri: Uri, expectedSize: Long) {
        val output = context.contentResolver.openFileDescriptor(uri, "r", null)
        when {
            output == null                  -> throw IllegalStateException("Exported file not found")
            output.statSize != expectedSize -> {
                throw UnexpectedExportKeysFileSizeException(
                        expectedFileSize = expectedSize,
                        actualFileSize = output.statSize
                )
            }
        }
    }
}

class UnexpectedExportKeysFileSizeException(expectedFileSize: Long, actualFileSize: Long) : IllegalStateException(
        "Exported Keys file has unexpected file size, got: $actualFileSize but expected: $expectedFileSize"
)
