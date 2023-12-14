

package im.vector.lib.multipicker.utils

import android.database.Cursor
import androidx.core.database.getStringOrNull

fun Cursor.getColumnIndexOrNull(column: String): Int? {
    return getColumnIndex(column).takeIf { it != -1 }
}

fun Cursor.readStringColumnOrNull(column: String): String? {
    return getColumnIndexOrNull(column)?.let { getStringOrNull(it) }
}
