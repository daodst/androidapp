

package im.vector.app.core.extensions

import im.vector.app.core.resources.DateProvider
import org.matrix.android.sdk.api.session.events.model.Event
import org.threeten.bp.LocalDateTime

fun Event.localDateTime(): LocalDateTime {
    return DateProvider.toLocalDateTime(originServerTs)
}
