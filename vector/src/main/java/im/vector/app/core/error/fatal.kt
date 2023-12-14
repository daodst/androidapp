

package im.vector.app.core.error

import timber.log.Timber


fun fatalError(message: String, failFast: Boolean) {
    if (failFast) {
        error(message)
    } else {
        Timber.e(message)
    }
}
