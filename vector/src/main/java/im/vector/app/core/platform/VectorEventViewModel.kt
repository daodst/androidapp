

package im.vector.app.core.platform

import androidx.lifecycle.ViewModel
import im.vector.app.core.utils.MutableDataSource
import im.vector.app.core.utils.PublishDataSource

interface VectorSharedAction


open class VectorSharedActionViewModel<T : VectorSharedAction>(private val store: MutableDataSource<T> = PublishDataSource()) :
        ViewModel(), MutableDataSource<T> by store
