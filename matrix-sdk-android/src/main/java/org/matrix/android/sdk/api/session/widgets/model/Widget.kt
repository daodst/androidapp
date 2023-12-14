

package org.matrix.android.sdk.api.session.widgets.model

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

data class Widget(
        val widgetContent: WidgetContent,
        val event: Event,
        val widgetId: String,
        val senderInfo: SenderInfo?,
        val isAddedByMe: Boolean,
        val type: WidgetType
) {

    val isActive = widgetContent.isActive()

    val name = widgetContent.getHumanName()
}
