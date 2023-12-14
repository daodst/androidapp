

package org.matrix.android.sdk.internal.session.sync

import com.squareup.moshi.JsonClass
import okio.buffer
import okio.source
import org.matrix.android.sdk.internal.di.MoshiProvider
import timber.log.Timber
import java.io.File

@JsonClass(generateAdapter = true)
internal data class InitialSyncStatus(
        val step: Int = STEP_INIT,
        val downloadedDate: Long = 0
) {
    companion object {
        const val STEP_INIT = 0
        const val STEP_DOWNLOADING = 1
        const val STEP_DOWNLOADED = 2
        const val STEP_PARSED = 3
        const val STEP_SUCCESS = 4
    }
}

internal interface InitialSyncStatusRepository {
    fun getStep(): Int

    fun setStep(step: Int)
}


internal class FileInitialSyncStatusRepository(directory: File) : InitialSyncStatusRepository {

    companion object {
        
        
        
        private const val INIT_SYNC_FILE_LIFETIME = 2 * 60 * 60 * 1_000L
    }

    private val file = File(directory, "status.json")
    private val jsonAdapter = MoshiProvider.providesMoshi().adapter(InitialSyncStatus::class.java)

    private var cache: InitialSyncStatus? = null

    override fun getStep(): Int {
        ensureCache()
        val state = cache?.step ?: InitialSyncStatus.STEP_INIT
        return if (state >= InitialSyncStatus.STEP_DOWNLOADED &&
                System.currentTimeMillis() > (cache?.downloadedDate ?: 0) + INIT_SYNC_FILE_LIFETIME) {
            Timber.d("INIT_SYNC downloaded file is outdated, download it again")
            
            setStep(InitialSyncStatus.STEP_INIT)
            InitialSyncStatus.STEP_INIT
        } else {
            state
        }
    }

    override fun setStep(step: Int) {
        var newStatus = cache?.copy(step = step) ?: InitialSyncStatus(step = step)
        if (step == InitialSyncStatus.STEP_DOWNLOADED) {
            
            newStatus = newStatus.copy(
                    downloadedDate = System.currentTimeMillis()
            )
        }
        cache = newStatus
        writeFile()
    }

    private fun ensureCache() {
        if (cache == null) readFile()
    }

    
    private fun readFile() {
        cache = file
                .takeIf { it.exists() }
                ?.let { jsonAdapter.fromJson(it.source().buffer()) }
    }

    
    private fun writeFile() {
        file.delete()
        cache
                ?.let { jsonAdapter.toJson(it) }
                ?.let { file.writeText(it) }
    }
}
