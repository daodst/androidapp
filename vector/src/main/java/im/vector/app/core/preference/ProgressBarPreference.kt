

package im.vector.app.core.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import im.vector.app.R

class ProgressBarPreference : Preference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        layoutResource = R.layout.vector_settings_spinner_preference
    }
}
