

package im.vector.app.core.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceViewHolder

class VectorCheckboxPreference : CheckBoxPreference {
    
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    init {
        
        isIconSpaceReserved = true
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        
        (holder.findViewById(android.R.id.title) as? TextView)?.isSingleLine = false
        super.onBindViewHolder(holder)
    }
}
