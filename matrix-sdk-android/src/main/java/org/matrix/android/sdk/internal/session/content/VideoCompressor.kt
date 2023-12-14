

package org.matrix.android.sdk.internal.session.content

import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.source.FilePathDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.internal.util.TemporaryFileCreator
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class VideoCompressor @Inject constructor(
        private val temporaryFileCreator: TemporaryFileCreator
) {

    suspend fun compress(videoFile: File,
                         progressListener: ProgressListener?): VideoCompressionResult {
        val destinationFile = temporaryFileCreator.create()

        val job = Job()

        Timber.d("Compressing: start")
        progressListener?.onProgress(0, 100)

        var result: Int = -1
        var failure: Throwable? = null
        Transcoder.into(destinationFile.path)
                .addDataSource(object : FilePathDataSource(videoFile.path) {
                    
                    @Suppress("SENSELESS_COMPARISON") 
                    override fun isInitialized(): Boolean {
                        if (source == null) {
                            return false
                        }
                        return super.isInitialized()
                    }
                })
                .setListener(object : TranscoderListener {
                    override fun onTranscodeProgress(progress: Double) {
                        Timber.d("Compressing: $progress%")
                        progressListener?.onProgress((progress * 100).toInt(), 100)
                    }

                    override fun onTranscodeCompleted(successCode: Int) {
                        Timber.d("Compressing: success: $successCode")
                        result = successCode
                        job.complete()
                    }

                    override fun onTranscodeCanceled() {
                        Timber.d("Compressing: cancel")
                        job.cancel()
                    }

                    override fun onTranscodeFailed(exception: Throwable) {
                        Timber.w(exception, "Compressing: failure")
                        failure = exception
                        job.completeExceptionally(exception)
                    }
                })
                .transcode()

        job.join()

        
        if (job.isCancelled) {
            
            deleteFile(destinationFile)
            return when (val finalFailure = failure) {
                null -> {
                    
                    
                    Timber.w("Compressing: A failure occurred")
                    VideoCompressionResult.CompressionCancelled
                }
                else -> {
                    
                    Timber.w("Compressing: Job cancelled")
                    VideoCompressionResult.CompressionFailed(finalFailure)
                }
            }
        }

        progressListener?.onProgress(100, 100)

        return when (result) {
            Transcoder.SUCCESS_TRANSCODED -> {
                VideoCompressionResult.Success(destinationFile)
            }
            Transcoder.SUCCESS_NOT_NEEDED -> {
                
                deleteFile(destinationFile)
                VideoCompressionResult.CompressionNotNeeded
            }
            else                          -> {
                
                
                deleteFile(destinationFile)
                Timber.w("Unknown result: $result")
                VideoCompressionResult.CompressionFailed(IllegalStateException("Unknown result: $result"))
            }
        }
    }

    private suspend fun deleteFile(file: File) {
        withContext(Dispatchers.IO) {
            file.delete()
        }
    }
}
