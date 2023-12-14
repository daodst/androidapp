

package im.vector.app.core.utils

import android.content.Context
import timber.log.Timber
import java.io.File
import java.util.Locale

typealias ActionOnFile = (file: File) -> Boolean



fun deleteAllFiles(root: File) {
    Timber.v("Delete ${root.absolutePath}")
    recursiveActionOnFile(root, ::deleteAction)
}

private fun deleteAction(file: File): Boolean {
    if (file.exists()) {
        Timber.v("deleteFile: $file")
        return file.delete()
    }

    return true
}



fun lsFiles(context: Context) {
    Timber.v("Content of cache dir:")
    recursiveActionOnFile(context.cacheDir, ::logAction)

    Timber.v("Content of files dir:")
    recursiveActionOnFile(context.filesDir, ::logAction)
}

private fun logAction(file: File): Boolean {
    if (file.isDirectory) {
        Timber.v(file.toString())
    } else {
        Timber.v("$file ${file.length()} bytes")
    }
    return true
}




private fun recursiveActionOnFile(file: File, action: ActionOnFile): Boolean {
    if (file.isDirectory) {
        file.list()?.forEach {
            val result = recursiveActionOnFile(File(file, it), action)

            if (!result) {
                
                return false
            }
        }
    }

    return action.invoke(file)
}


fun getFileExtension(fileUri: String): String? {
    var reducedStr = fileUri

    if (reducedStr.isNotEmpty()) {
        
        reducedStr = reducedStr.substringBeforeLast('#')

        
        reducedStr = reducedStr.substringBeforeLast('?')

        
        val filename = reducedStr.substringAfterLast('/')

        
        
        if (filename.isNotEmpty()) {
            val dotPos = filename.lastIndexOf('.')
            if (0 <= dotPos) {
                val ext = filename.substring(dotPos + 1)

                if (ext.isNotBlank()) {
                    return ext.lowercase(Locale.ROOT)
                }
            }
        }
    }

    return null
}



fun getSizeOfFiles(root: File): Long {
    return root.walkTopDown()
            .onEnter {
                Timber.v("Get size of ${it.absolutePath}")
                true
            }
            .sumOf { it.length() }
}
