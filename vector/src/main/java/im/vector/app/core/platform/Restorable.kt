

package im.vector.app.core.platform

import android.os.Bundle

interface Restorable {

    fun onSaveInstanceState(outState: Bundle)

    fun onRestoreInstanceState(savedInstanceState: Bundle?)
}
