
package im.vector.app.features.notifications

import java.io.Serializable


sealed interface NotifiableEvent : Serializable {
    val eventId: String
    val editedEventId: String?

    
    val canBeReplaced: Boolean
    val isRedacted: Boolean
}
