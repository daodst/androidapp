

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class ViewRoom(
        
        val activeSpace: ActiveSpace? = null,
        
        val isDM: Boolean? = null,
        
        val isSpace: Boolean? = null,
        
        val trigger: Trigger? = null,
        
        val viaKeyboard: Boolean? = null,
) : VectorAnalyticsEvent {

    enum class Trigger {
        
        Created,

        
        MessageSearch,

        
        MessageUser,

        
        MobileExploreRooms,

        
        MobileFileSearch,

        
        MobileInCall,

        
        MobileLinkShare,

        
        MobilePermalink,

        
        MobileRoomMemberDetail,

        
        MobileRoomPreview,

        
        MobileRoomSearch,

        
        MobileSearchContactDetail,

        
        MobileSpaceMemberDetail,

        
        MobileSpaceMembers,

        
        MobileSpaceMenu,

        
        MobileSpaceSettings,

        
        Notification,

        
        Predecessor,

        
        RoomDirectory,

        
        RoomList,

        
        SlashCommand,

        
        SpaceHierarchy,

        
        Timeline,

        
        Tombstone,

        
        VerificationRequest,

        
        WebAcceptCall,

        
        WebDialPad,

        
        WebFloatingCallWindow,

        
        WebForwardShortcut,

        
        WebHorizontalBreadcrumbs,

        
        WebKeyboardShortcut,

        
        WebNotificationPanel,

        
        WebPredecessorSettings,

        
        WebRoomListNotificationBadge,

        
        WebSpaceContextSwitch,

        
        WebSpacePanelNotificationBadge,

        
        WebUnifiedSearch,

        
        WebVerticalBreadcrumbs,

        
        Widget,
    }

    enum class ActiveSpace {

        
        Home,

        
        Meta,

        
        Private,

        
        Public,
    }

    override fun getName() = "ViewRoom"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            activeSpace?.let { put("activeSpace", it.name) }
            isDM?.let { put("isDM", it) }
            isSpace?.let { put("isSpace", it) }
            trigger?.let { put("trigger", it.name) }
            viaKeyboard?.let { put("viaKeyboard", it) }
        }.takeIf { it.isNotEmpty() }
    }
}
