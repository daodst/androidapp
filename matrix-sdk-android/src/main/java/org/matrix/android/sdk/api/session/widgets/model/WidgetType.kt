

package org.matrix.android.sdk.api.session.widgets.model

private val DEFINED_TYPES by lazy {
    listOf(
            WidgetType.Jitsi,
            WidgetType.TradingView,
            WidgetType.Spotify,
            WidgetType.Video,
            WidgetType.GoogleDoc,
            WidgetType.GoogleCalendar,
            WidgetType.Etherpad,
            WidgetType.StickerPicker,
            WidgetType.Grafana,
            WidgetType.Custom,
            WidgetType.IntegrationManager
    )
}


sealed class WidgetType(open val preferred: String, open val legacy: String = preferred) {
    object Jitsi : WidgetType("m.jitsi", "jitsi")
    object TradingView : WidgetType("m.tradingview")
    object Spotify : WidgetType("m.spotify")
    object Video : WidgetType("m.video")
    object GoogleDoc : WidgetType("m.googledoc")
    object GoogleCalendar : WidgetType("m.googlecalendar")
    object Etherpad : WidgetType("m.etherpad")
    object StickerPicker : WidgetType("m.stickerpicker")
    object Grafana : WidgetType("m.grafana")
    object Custom : WidgetType("m.custom")
    object ElementCall : WidgetType("io.element.call")
    object IntegrationManager : WidgetType("m.integration_manager")
    data class Fallback(override val preferred: String) : WidgetType(preferred)

    fun matches(type: String): Boolean {
        return type == preferred || type == legacy
    }

    fun values(): Set<String> {
        return setOf(preferred, legacy)
    }

    companion object {

        fun fromString(type: String): WidgetType {
            val matchingType = DEFINED_TYPES.firstOrNull {
                it.matches(type)
            }
            return matchingType ?: Fallback(type)
        }
    }
}
