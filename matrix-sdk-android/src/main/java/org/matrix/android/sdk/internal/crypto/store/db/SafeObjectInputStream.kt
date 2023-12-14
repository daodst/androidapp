

package org.matrix.android.sdk.internal.crypto.store.db

import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectStreamClass


internal class SafeObjectInputStream(inputStream: InputStream) : ObjectInputStream(inputStream) {

    init {
        enableResolveObject(true)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    override fun readClassDescriptor(): ObjectStreamClass {
        val read = super.readClassDescriptor()
        if (read.name.startsWith("im.vector.matrix.android.")) {
            return ObjectStreamClass.lookup(Class.forName(read.name.replace("im.vector.matrix.android.", "org.matrix.android.sdk.")))
        }
        return read
    }
}
