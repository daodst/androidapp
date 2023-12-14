

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class JoinedRoom(
        
        val isDM: Boolean,
        
        val isSpace: Boolean,
        
        val roomSize: RoomSize,
        
        val trigger: Trigger? = null,
) : VectorAnalyticsEvent {

    enum class Trigger {
        
        Invite,

        
        MobileExploreRooms,

        
        MobilePermalink,

        
        Notification,

        
        RoomDirectory,

        
        RoomPreview,

        
        SlashCommand,

        
        SpaceHierarchy,

        
        Timeline,
    }

    enum class RoomSize {
        ElevenToOneHundred,
        MoreThanAThousand,
        One,
        OneHundredAndOneToAThousand,
        ThreeToTen,
        Two,
    }

    override fun getName() = "JoinedRoom"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("isDM", isDM)
            put("isSpace", isSpace)
            put("roomSize", roomSize.name)
            trigger?.let { put("trigger", it.name) }
        }.takeIf { it.isNotEmpty() }
    }
}
