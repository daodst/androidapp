

package org.matrix.android.sdk.internal.session.download

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer

internal class ProgressResponseBody(
        private val responseBody: ResponseBody,
        private val chainUrl: String,
        private val progressListener: ProgressListener) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? = responseBody.contentType()
    override fun contentLength(): Long = responseBody.contentLength()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0L
                progressListener.update(chainUrl, totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                return bytesRead
            }
        }
    }
}

internal interface ProgressListener {
    fun update(url: String, bytesRead: Long, contentLength: Long, done: Boolean)
    fun error(url: String, errorCode: Int)
}
