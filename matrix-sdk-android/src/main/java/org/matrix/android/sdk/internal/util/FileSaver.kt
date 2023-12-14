

package org.matrix.android.sdk.internal.util

import androidx.annotation.WorkerThread
import java.io.File
import java.io.InputStream


@WorkerThread
internal fun writeToFile(inputStream: InputStream, outputFile: File) {
    
    outputFile.parentFile?.mkdirs()

    outputFile.outputStream().use {
        inputStream.copyTo(it)
    }
}
