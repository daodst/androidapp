

package org.matrix.android.sdk.api.session

interface EventStreamService {

    fun addEventStreamListener(streamListener: LiveEventListener)

    fun removeEventStreamListener(streamListener: LiveEventListener)
}
