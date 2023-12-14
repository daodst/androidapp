

package org.matrix.android.sdk.internal.session

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.completeWith
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt
import org.matrix.android.sdk.api.session.file.FileService
import org.matrix.android.sdk.api.util.md5
import org.matrix.android.sdk.internal.crypto.attachments.MXEncryptedAttachments
import org.matrix.android.sdk.internal.di.SessionDownloadsDirectory
import org.matrix.android.sdk.internal.di.UnauthenticatedWithCertificateWithProgress
import org.matrix.android.sdk.internal.network.TimeOutInterceptor
import org.matrix.android.sdk.internal.network.token.HomeserverAccessTokenProvider
import org.matrix.android.sdk.internal.session.download.DownloadProgressInterceptor.Companion.DOWNLOAD_PROGRESS_INTERCEPTOR_HEADER
import org.matrix.android.sdk.internal.util.file.AtomicFileCreator
import org.matrix.android.sdk.internal.util.writeToFile
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

internal class DefaultFileService @Inject constructor(
        private val context: Context,
        @SessionDownloadsDirectory
        private val sessionCacheDirectory: File,
        private val contentUrlResolver: ContentUrlResolver,
        @UnauthenticatedWithCertificateWithProgress
        private val okHttpClient: OkHttpClient,
        private val accessTokenProvider: HomeserverAccessTokenProvider,
        private val coroutineDispatchers: MatrixCoroutineDispatchers
) : FileService {

    
    private val legacyFolder = File(sessionCacheDirectory, "MF")

    
    private val downloadFolder = File(sessionCacheDirectory, "F")

    
    private val decryptedFolder = File(downloadFolder, "D")

    init {
        
        legacyFolder.deleteRecursively()
    }

    
    private val ongoing = mutableMapOf<String, CompletableDeferred<File>>()

    
    override suspend fun downloadFile(fileName: String,
                                      mimeType: String?,
                                      url: String?,
                                      elementToDecrypt: ElementToDecrypt?): File {
        url ?: throw IllegalArgumentException("url is null")

        Timber.v("## FileService downloadFile $url")
        Timber.v("## FileService downloadFile fileName $fileName")

        
        val existingDownload = synchronized(ongoing) {
            val existing = ongoing[url]
            if (existing != null) {
                Timber.v("## FileService downloadFile is already downloading.. ")
                existing
            } else {
                
                ongoing[url] = CompletableDeferred()
                
                null
            }
        }

        var atomicFileDownload: AtomicFileCreator? = null
        var atomicFileDecrypt: AtomicFileCreator? = null

        if (existingDownload != null) {
            
            return existingDownload.await()
        }

        val result = runCatching {
            val cachedFiles = withContext(coroutineDispatchers.io) {
                if (!decryptedFolder.exists()) {
                    decryptedFolder.mkdirs()
                }

                
                
                
                val cachedFiles = getFiles(url, fileName, mimeType, elementToDecrypt != null)

                if (!cachedFiles.file.exists()) {

                    val resolvedUrl = contentUrlResolver.resolveForDownload(url, elementToDecrypt)
                    Timber.v("## FileService downloadFile resolvedUrl :$resolvedUrl ")
                    resolvedUrl ?: throw IllegalArgumentException("url is null")

                    val request = when (resolvedUrl) {
                        is ContentUrlResolver.ResolvedMethod.GET  -> {
                            Request.Builder()
                                    .url(resolvedUrl.url)
                                    .header(DOWNLOAD_PROGRESS_INTERCEPTOR_HEADER, url)
                                    .build()
                        }

                        is ContentUrlResolver.ResolvedMethod.POST -> {
                            Request.Builder()
                                    .url(resolvedUrl.url)
                                    .header(DOWNLOAD_PROGRESS_INTERCEPTOR_HEADER, url)
                                    .post(resolvedUrl.jsonBody.toRequestBody("application/json".toMediaType()))
                                    .build()
                        }
                    }

                    val response = try {
                        okHttpClient.newCall(request).execute()
                    } catch (failure: Throwable) {
                        throw if (failure is IOException) {
                            Failure.NetworkConnection(failure)
                        } else {
                            failure
                        }
                    }

                    if (!response.isSuccessful) {
                        throw Failure.NetworkConnection(IOException())
                    }

                    val source = response.body?.source() ?: throw Failure.NetworkConnection(IOException())

                    Timber.v("Response size ${response.body?.contentLength()} - Stream available: ${!source.exhausted()}")

                    
                    
                    val atomicFileCreator = AtomicFileCreator(cachedFiles.file).also { atomicFileDownload = it }
                    writeToFile(source.inputStream(), atomicFileCreator.partFile)
                    response.close()
                    atomicFileCreator.commit()
                } else {
                    Timber.v("## FileService: cache hit for $url")
                }
                cachedFiles
            }

            
            if (cachedFiles.decryptedFile != null) {
                if (!cachedFiles.decryptedFile.exists()) {
                    Timber.v("## FileService: decrypt file")
                    
                    cachedFiles.decryptedFile.parentFile?.mkdirs()
                    
                    val atomicFileCreator = AtomicFileCreator(cachedFiles.decryptedFile).also { atomicFileDecrypt = it }
                    val decryptSuccess = cachedFiles.file.inputStream().use { inputStream ->
                        atomicFileCreator.partFile.outputStream().buffered().use { outputStream ->
                            MXEncryptedAttachments.decryptAttachment(
                                    inputStream,
                                    elementToDecrypt,
                                    outputStream
                            )
                        }
                    }
                    atomicFileCreator.commit()
                    if (!decryptSuccess) {
                        throw IllegalStateException("Decryption error")
                    }
                } else {
                    Timber.v("## FileService: cache hit for decrypted file")
                }
                cachedFiles.decryptedFile
            } else {
                
                cachedFiles.file
            }
        }

        
        val toNotify = synchronized(ongoing) { ongoing.remove(url) }
        result.onSuccess {
            Timber.v("## FileService additional to notify is > 0 ")
        }
        toNotify?.completeWith(result)

        result.onFailure {
            atomicFileDownload?.cancel()
            atomicFileDecrypt?.cancel()
        }

        return result.getOrThrow()
    }

    override suspend fun downloadCusFile(fileName: String,
                                         mimeType: String?,
                                         text: String,
                                         lan: String,
                                         url: String?): File {
        url ?: throw IllegalArgumentException("url is null")

        Timber.v("## FileService downloadFile $url")

        
        val existingDownload = synchronized(ongoing) {
            val existing = ongoing[url]
            if (existing != null) {
                Timber.v("## FileService downloadFile is already downloading.. ")
                existing
            } else {
                
                ongoing[url] = CompletableDeferred()
                
                null
            }
        }

        var atomicFileDownload: AtomicFileCreator? = null
        var atomicFileDecrypt: AtomicFileCreator? = null

        if (existingDownload != null) {
            
            return existingDownload.await()
        }

        val result = runCatching {
            val cachedFiles = withContext(coroutineDispatchers.io) {
                if (!decryptedFolder.exists()) {
                    decryptedFolder.mkdirs()
                }

                
                
                
                val cachedFiles = getFiles(url, fileName, mimeType, false)

                if (!cachedFiles.file.exists()) {

                    val formBody: RequestBody = FormBody.Builder()
                            .add("text", text)
                            .add("lan", lan)
                            .build()
                    val request = Request.Builder()
                            .url(url)
                            .header("token", accessTokenProvider.getToken() ?: "")
                            .header(TimeOutInterceptor.CONNECT_TIMEOUT, TimeOutInterceptor.DEFAULT_LONG_TIMEOUT.toString())
                            .header(TimeOutInterceptor.READ_TIMEOUT, TimeOutInterceptor.DEFAULT_LONG_TIMEOUT.toString())
                            .header(TimeOutInterceptor.WRITE_TIMEOUT, TimeOutInterceptor.DEFAULT_LONG_TIMEOUT.toString())
                            .post(formBody)
                            .build()


                    val response = try {
                        okHttpClient.newCall(request).execute()
                    } catch (failure: Throwable) {
                        throw if (failure is IOException) {
                            Failure.NetworkConnection(failure)
                        } else {
                            failure
                        }
                    }

                    if (!response.isSuccessful) {
                        throw Failure.NetworkConnection(IOException())
                    }

                    val source = response.body?.source() ?: throw Failure.NetworkConnection(IOException())

                    Timber.v("Response size ${response.body?.contentLength()} - Stream available: ${!source.exhausted()}")

                    
                    
                    val atomicFileCreator = AtomicFileCreator(cachedFiles.file).also { atomicFileDownload = it }
                    writeToFile(source.inputStream(), atomicFileCreator.partFile)
                    response.close()
                    atomicFileCreator.commit()
                } else {
                    Timber.v("## FileService: cache hit for $url")
                }
                cachedFiles
            }
            cachedFiles.file
        }

        
        val toNotify = synchronized(ongoing) { ongoing.remove(url) }
        result.onSuccess {
            Timber.v("## FileService additional to notify is > 0 ")
        }
        toNotify?.completeWith(result)

        result.onFailure {
            atomicFileDownload?.cancel()
            atomicFileDecrypt?.cancel()
        }

        return result.getOrThrow()
    }

    fun storeDataFor(mxcUrl: String,
                     filename: String?,
                     mimeType: String?,
                     originalFile: File,
                     encryptedFile: File?) {
        val files = getFiles(mxcUrl, filename, mimeType, encryptedFile != null)
        if (encryptedFile != null) {
            
            files.decryptedFile?.let { originalFile.copyTo(it) }
            encryptedFile.copyTo(files.file)
        } else {
            
            originalFile.copyTo(files.file)
        }
    }

    private fun safeFileName(fileName: String?, mimeType: String?): String {
        return buildString {
            
            val result = fileName
                    ?.replace("[^a-z A-Z0-9\\\\.\\-]".toRegex(), "_")
                    ?.takeIf { it.isNotEmpty() }
                    ?: DEFAULT_FILENAME
            append(result)
            
            val extensionFromMime = mimeType?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) }
            if (extensionFromMime != null) {
                
                val fileExtension = result.substringAfterLast(delimiter = ".", missingDelimiterValue = "")
                if (fileExtension.isEmpty() || fileExtension != extensionFromMime) {
                    
                    append(".")
                    append(extensionFromMime)
                }
            }
        }
    }

    override fun isFileInCache(mxcUrl: String?,
                               fileName: String,
                               mimeType: String?,
                               elementToDecrypt: ElementToDecrypt?): Boolean {
        return fileState(mxcUrl, fileName, mimeType, elementToDecrypt) is FileService.FileState.InCache
    }

    internal data class CachedFiles(
            
            val file: File,
            
            val decryptedFile: File?
    ) {
        fun getClearFile(): File = decryptedFile ?: file
    }

    private fun getFiles(mxcUrl: String,
                         fileName: String?,
                         mimeType: String?,
                         isEncrypted: Boolean): CachedFiles {
        val hashFolder = mxcUrl.md5()
        val safeFileName = safeFileName(fileName, mimeType)
        return if (isEncrypted) {
            
            CachedFiles(
                    File(downloadFolder, "$hashFolder/$ENCRYPTED_FILENAME"),
                    File(decryptedFolder, "$hashFolder/$safeFileName")
            )
        } else {
            
            CachedFiles(
                    File(downloadFolder, "$hashFolder/$safeFileName"),
                    null
            )
        }
    }

    override fun fileState(mxcUrl: String?,
                           fileName: String,
                           mimeType: String?,
                           elementToDecrypt: ElementToDecrypt?): FileService.FileState {
        mxcUrl ?: return FileService.FileState.Unknown
        val files = getFiles(mxcUrl, fileName, mimeType, elementToDecrypt != null)
        if (files.file.exists()) {
            return FileService.FileState.InCache(
                    decryptedFileInCache = files.getClearFile().exists()
            )
        }
        val isDownloading = synchronized(ongoing) {
            ongoing[mxcUrl] != null
        }
        return if (isDownloading) FileService.FileState.Downloading else FileService.FileState.Unknown
    }

    
    override fun getTemporarySharableURI(mxcUrl: String?,
                                         fileName: String,
                                         mimeType: String?,
                                         elementToDecrypt: ElementToDecrypt?): Uri? {
        mxcUrl ?: return null
        
        val authority = "${context.packageName}.mx-sdk.fileprovider"
        val targetFile = getFiles(mxcUrl, fileName, mimeType, elementToDecrypt != null).getClearFile()
        if (!targetFile.exists()) return null
        return FileProvider.getUriForFile(context, authority, targetFile)
    }

    override fun getCacheSize(): Long {
        return downloadFolder.walkTopDown()
                .onEnter {
                    Timber.v("Get size of ${it.absolutePath}")
                    true
                }
                .sumOf { it.length() }
    }

    override fun clearCache() {
        downloadFolder.deleteRecursively()
    }

    override fun clearDecryptedCache() {
        decryptedFolder.deleteRecursively()
    }

    companion object {
        private const val ENCRYPTED_FILENAME = "encrypted.bin"

        
        private const val DEFAULT_FILENAME = "file"
    }
}
