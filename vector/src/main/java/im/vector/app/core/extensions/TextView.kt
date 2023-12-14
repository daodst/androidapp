

package im.vector.app.core.extensions

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import im.vector.app.R
import im.vector.app.core.platform.showOptimizedSnackbar
import im.vector.app.core.utils.copyToClipboard
import im.vector.app.features.themes.ThemeUtils


fun TextView.setTextOrHide(newText: CharSequence?, hideWhenBlank: Boolean = true, vararg relatedViews: View = emptyArray()) {
    if (newText == null ||
            (newText.isBlank() && hideWhenBlank)) {
        isVisible = false
        relatedViews.forEach { it.isVisible = false }
    } else {
        this.text = newText
        isVisible = true
        relatedViews.forEach { it.isVisible = true }
    }
}


fun TextView.setTextWithColoredPart(@StringRes fullTextRes: Int,
                                    @StringRes coloredTextRes: Int,
                                    @AttrRes colorAttribute: Int = R.attr.colorPrimary,
                                    underline: Boolean = false,
                                    onClick: (() -> Unit)? = null) {
    val coloredPart = resources.getString(coloredTextRes)
    
    val fullText = resources.getString(fullTextRes, coloredPart)

    setTextWithColoredPart(fullText, coloredPart, colorAttribute, underline, onClick)
}


fun TextView.setTextWithColoredPart(fullText: String,
                                    coloredPart: String,
                                    @AttrRes colorAttribute: Int = R.attr.colorPrimary,
                                    underline: Boolean = true,
                                    onClick: (() -> Unit)? = null) {
    val color = ThemeUtils.getColor(context, colorAttribute)

    val foregroundSpan = ForegroundColorSpan(color)

    val index = fullText.indexOf(coloredPart)

    text = SpannableString(fullText)
            .apply {
                setSpan(foregroundSpan, index, index + coloredPart.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (onClick != null) {
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            onClick()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = color
                        }
                    }
                    setSpan(clickableSpan, index, index + coloredPart.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    movementMethod = LinkMovementMethod.getInstance()
                }
                if (underline) {
                    setSpan(UnderlineSpan(), index, index + coloredPart.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
}

fun TextView.setLeftDrawable(@DrawableRes iconRes: Int, @AttrRes tintColor: Int? = null) {
    val icon = if (tintColor != null) {
        val tint = ThemeUtils.getColor(context, tintColor)
        ContextCompat.getDrawable(context, iconRes)?.also {
            DrawableCompat.setTint(it.mutate(), tint)
        }
    } else {
        ContextCompat.getDrawable(context, iconRes)
    }
    setLeftDrawable(icon)
}

fun TextView.setLeftDrawable(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.clearDrawables() {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}


fun TextView.copyOnLongClick() {
    setOnLongClickListener { view ->
        (view as? TextView)
                ?.text
                ?.let { text ->
                    copyToClipboard(view.context, text, false)
                    view.showOptimizedSnackbar(view.resources.getString(R.string.copied_to_clipboard))
                }
        true
    }
}
