

package im.vector.app.features.location.option

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import im.vector.app.R
import im.vector.app.core.extensions.tintBackground
import im.vector.app.databinding.ViewLocationSharingOptionBinding


class LocationSharingOptionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val iconView: ImageView
        get() = binding.shareLocationOptionIcon

    private val binding = ViewLocationSharingOptionBinding.inflate(
            LayoutInflater.from(context),
            this
    )

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LocationSharingOptionView,
                0,
                0
        ).run {
            try {
                setIcon(this)
                setTitle(this)
            } finally {
                recycle()
            }
        }
    }

    fun setIconBackgroundTint(@ColorInt color: Int) {
        binding.shareLocationOptionIcon.tintBackground(color)
    }

    private fun setIcon(typedArray: TypedArray) {
        val icon = typedArray.getDrawable(R.styleable.LocationSharingOptionView_locShareIcon)
        val background = typedArray.getDrawable(R.styleable.LocationSharingOptionView_locShareIconBackground)
        val backgroundTint = typedArray.getColor(
                R.styleable.LocationSharingOptionView_locShareIconBackgroundTint,
                ContextCompat.getColor(context, android.R.color.transparent)
        )
        val padding = typedArray.getDimensionPixelOffset(
                R.styleable.LocationSharingOptionView_locShareIconPadding,
                context.resources.getDimensionPixelOffset(R.dimen.location_sharing_option_default_padding)
        )
        val description = typedArray.getString(R.styleable.LocationSharingOptionView_locShareIconDescription)

        iconView.setImageDrawable(icon)
        iconView.background = background
        iconView.tintBackground(backgroundTint)
        iconView.setPadding(padding)
        iconView.contentDescription = description
    }

    private fun setTitle(typedArray: TypedArray) {
        val title = typedArray.getString(R.styleable.LocationSharingOptionView_locShareTitle)
        binding.shareLocationOptionTitle.text = title
    }
}
