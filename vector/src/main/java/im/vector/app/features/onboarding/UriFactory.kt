

package im.vector.app.features.onboarding

import android.net.Uri
import javax.inject.Inject

class UriFactory @Inject constructor() {

    fun parse(value: String): Uri {
        return Uri.parse(value)
    }
}
