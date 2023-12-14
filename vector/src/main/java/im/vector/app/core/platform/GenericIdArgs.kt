

package im.vector.app.core.platform

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class GenericIdArgs(
        val id: String
) : Parcelable
