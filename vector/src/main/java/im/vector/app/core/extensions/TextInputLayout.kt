

package im.vector.app.core.extensions

import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.map
import reactivecircus.flowbinding.android.widget.textChanges

fun TextInputLayout.editText() = this.editText!!


fun TextInputLayout.hasSurroundingSpaces() = editText().text.toString().let { it.trim() != it }

fun TextInputLayout.hasContentFlow(mapper: (CharSequence) -> CharSequence = { it }) = editText().textChanges().map { mapper(it).isNotEmpty() }

fun TextInputLayout.content() = editText().text.toString()
