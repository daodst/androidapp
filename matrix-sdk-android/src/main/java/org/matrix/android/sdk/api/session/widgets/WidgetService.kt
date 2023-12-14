

package org.matrix.android.sdk.api.session.widgets

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.widgets.model.Widget


interface WidgetService {

    
    fun getWidgetURLFormatter(): WidgetURLFormatter

    
    fun getWidgetPostAPIMediator(): WidgetPostAPIMediator

    
    fun getRoomWidgets(
            roomId: String,
            widgetId: QueryStringValue = QueryStringValue.NoCondition,
            widgetTypes: Set<String>? = null,
            excludedTypes: Set<String>? = null
    ): List<Widget>

    
    fun getWidgetComputedUrl(widget: Widget, isLightTheme: Boolean): String?

    
    fun getRoomWidgetsLive(
            roomId: String,
            widgetId: QueryStringValue = QueryStringValue.NoCondition,
            widgetTypes: Set<String>? = null,
            excludedTypes: Set<String>? = null
    ): LiveData<List<Widget>>

    
    fun getUserWidgets(
            widgetTypes: Set<String>? = null,
            excludedTypes: Set<String>? = null
    ): List<Widget>

    
    fun getUserWidgetsLive(
            widgetTypes: Set<String>? = null,
            excludedTypes: Set<String>? = null
    ): LiveData<List<Widget>>

    
    suspend fun createRoomWidget(roomId: String, widgetId: String, content: Content): Widget

    
    suspend fun destroyRoomWidget(roomId: String, widgetId: String)

    
    fun hasPermissionsToHandleWidgets(roomId: String): Boolean
}
