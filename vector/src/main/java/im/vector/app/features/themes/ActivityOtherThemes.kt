

package im.vector.app.features.themes

import androidx.annotation.StyleRes
import im.vector.app.R


sealed class ActivityOtherThemes(@StyleRes val dark: Int,
                                 @StyleRes val black: Int) {

    object Default : ActivityOtherThemes(
            R.style.Theme_Vector_Dark,
            R.style.Theme_Vector_Black
    )

    object Launcher : ActivityOtherThemes(
            R.style.Theme_Vector_Launcher,
            R.style.Theme_Vector_Launcher
    )

    object AttachmentsPreview : ActivityOtherThemes(
            R.style.Theme_Vector_Black_AttachmentsPreview,
            R.style.Theme_Vector_Black_AttachmentsPreview
    )

    object VectorAttachmentsPreview : ActivityOtherThemes(
            R.style.Theme_Vector_Black_Transparent,
            R.style.Theme_Vector_Black_Transparent
    )
}
