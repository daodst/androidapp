

package im.vector.app.features.raw.wellknown

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ElementWellKnown(
        
        @Json(name = "im.vector.riot.jitsi")
        val jitsiServer: WellKnownPreferredConfig? = null,

        
        @Json(name = "io.element.e2ee")
        val elementE2E: E2EWellKnownConfig? = null,

        @Json(name = "im.vector.riot.e2ee")
        val riotE2E: E2EWellKnownConfig? = null,

        @Json(name = "org.matrix.msc3488.tile_server")
        val unstableMapTileServerConfig: MapTileServerConfig? = null,

        @Json(name = "m.tile_server")
        val mapTileServerConfig: MapTileServerConfig? = null
) {
    fun getBestMapTileServerConfig() = mapTileServerConfig ?: unstableMapTileServerConfig
}

@JsonClass(generateAdapter = true)
data class E2EWellKnownConfig(
        
        @Json(name = "default")
        val e2eDefault: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class WellKnownPreferredConfig(
        @Json(name = "preferredDomain")
        val preferredDomain: String? = null
)

@JsonClass(generateAdapter = true)
data class MapTileServerConfig(
        @Json(name = "map_style_url")
        val mapStyleUrl: String? = null
)
