

package im.vector.app.core.epoxy

import android.text.TextWatcher
import android.widget.CompoundButton
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

fun VectorEpoxyHolder.setValueOnce(textInputEditText: TextInputEditText, value: String?) {
    if (view.isAttachedToWindow) {
        
        
        
    } else {
        textInputEditText.setText(value)
    }
}

fun VectorEpoxyHolder.setValueOnce(switchView: SwitchMaterial, switchChecked: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
    if (view.isAttachedToWindow) {
        
        
    } else {
        switchView.isChecked = switchChecked
        switchView.setOnCheckedChangeListener(listener)
    }
}

fun TextView.addTextChangedListenerOnce(textWatcher: TextWatcher) {
    
    removeTextChangedListener(textWatcher)
    addTextChangedListener(textWatcher)
}
