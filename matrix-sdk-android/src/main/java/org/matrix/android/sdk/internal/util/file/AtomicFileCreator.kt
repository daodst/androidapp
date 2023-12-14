

package org.matrix.android.sdk.internal.util.file

import timber.log.Timber
import java.io.File

internal class AtomicFileCreator(private val file: File) {
    val partFile = File(file.parentFile, "${file.name}.part")

    init {
        if (file.exists()) {
            Timber.w("## AtomicFileCreator: target file ${file.path} exists, it should not happen.")
        }
        if (partFile.exists()) {
            Timber.d("## AtomicFileCreator: discard aborted part file ${partFile.path}")
            
        }
    }

    fun cancel() {
        partFile.delete()
    }

    fun commit() {
        partFile.renameTo(file)
    }
}
