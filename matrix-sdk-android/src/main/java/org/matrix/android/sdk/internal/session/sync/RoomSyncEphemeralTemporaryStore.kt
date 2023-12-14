

package org.matrix.android.sdk.internal.session.sync

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.buffer
import okio.source
import org.matrix.android.sdk.api.session.sync.model.RoomSyncEphemeral
import org.matrix.android.sdk.api.util.md5
import org.matrix.android.sdk.internal.di.SessionFilesDirectory
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal interface RoomSyncEphemeralTemporaryStore {
    fun write(roomId: String, roomSyncEphemeralJson: String)
    fun read(roomId: String): RoomSyncEphemeral?
    fun reset()
    fun delete(roomId: String)
}

internal class RoomSyncEphemeralTemporaryStoreFile @Inject constructor(
        @SessionFilesDirectory fileDirectory: File,
        moshi: Moshi
) : RoomSyncEphemeralTemporaryStore {

    private val workingDir: File by lazy {
        File(fileDirectory, "rr").also {
            it.mkdirs()
        }
    }

    private val roomSyncEphemeralAdapter = moshi.adapter(RoomSyncEphemeral::class.java)

    
    override fun write(roomId: String, roomSyncEphemeralJson: String) {
        Timber.w("INIT_SYNC Store ephemeral events for room $roomId")
        getFile(roomId).writeText(roomSyncEphemeralJson)
    }

    
    override fun read(roomId: String): RoomSyncEphemeral? {
        return getFile(roomId)
                .takeIf { it.exists() }
                ?.inputStream()
                ?.use { pos ->
                    roomSyncEphemeralAdapter.fromJson(JsonReader.of(pos.source().buffer()))
                }
    }

    override fun delete(roomId: String) {
        getFile(roomId).delete()
    }

    override fun reset() {
        workingDir.deleteRecursively()
        workingDir.mkdirs()
    }

    private fun getFile(roomId: String): File {
        return File(workingDir, "${roomId.md5()}.json")
    }
}
