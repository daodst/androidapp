

package im.vector.app.core.time

import javax.inject.Inject

interface Clock {
    fun epochMillis(): Long
}

class DefaultClock @Inject constructor() : Clock {

    
    override fun epochMillis(): Long {
        return System.currentTimeMillis()
    }
}
