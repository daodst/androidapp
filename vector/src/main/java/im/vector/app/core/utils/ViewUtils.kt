

package im.vector.app.core.utils

import android.text.Editable
import android.view.ViewGroup
import androidx.core.view.children
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.core.platform.SimpleTextWatcher


fun ViewGroup.findAllTextInputLayout(): List<TextInputLayout> {
    val res = ArrayList<TextInputLayout>()

    children.forEach {
        if (it is TextInputLayout) {
            res.add(it)
        } else if (it is ViewGroup) {
            
            res.addAll(it.findAllTextInputLayout())
        }
    }

    return res
}


fun autoResetTextInputLayoutErrors(textInputLayouts: List<TextInputLayout>) {
    textInputLayouts.forEach {
        it.editText?.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                
                it.error = null
            }
        })
    }
}
