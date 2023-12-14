

package im.vector.app.core.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceViewHolder
import im.vector.app.R
import timber.log.Timber


class VectorEditTextPreference : EditTextPreference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        dialogLayoutResource = R.layout.dialog_preference_edit_text
        
        isIconSpaceReserved = true
    }

    
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        
        try {
            (holder.findViewById(android.R.id.title) as? TextView)?.isSingleLine = false
        } catch (e: Exception) {
            Timber.e(e, "onBindView")
        }

        super.onBindViewHolder(holder)
    }
}
