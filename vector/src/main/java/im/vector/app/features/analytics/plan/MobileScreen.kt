

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsScreen



data class MobileScreen(
        
        val durationMs: Int? = null,
        val screenName: ScreenName,
) : VectorAnalyticsScreen {

    enum class ScreenName {
        
        Breadcrumbs,

        
        CreateRoom,

        
        DeactivateAccount,

        
        Dialpad,

        
        Favourites,

        
        ForgotPassword,

        
        Group,

        
        Home,

        
        InviteFriends,

        
        Login,

        
        MyGroups,

        
        People,

        
        Register,

        
        Room,

        
        RoomAddresses,

        
        RoomDetails,

        
        RoomDirectory,

        
        RoomFilter,

        
        RoomMembers,

        
        RoomNotifications,

        
        RoomPermissions,

        
        RoomPreview,

        
        RoomSearch,

        
        RoomSettings,

        
        RoomUploads,

        
        Rooms,
        
        GROUP,
        
        SearchFiles,

        
        SearchMessages,

        
        SearchPeople,

        
        SearchRooms,

        
        Settings,

        
        SettingsAdvanced,

        
        SettingsDefaultNotifications,

        
        SettingsGeneral,

        
        SettingsHelp,

        
        SettingsIgnoredUsers,

        
        SettingsLabs,

        
        SettingsLegals,

        
        SettingsMentionsAndKeywords,

        
        SettingsNotifications,

        
        SettingsPreferences,

        
        SettingsSecurity,

        
        SettingsVoiceVideo,

        
        Sidebar,

        
        SpaceExploreRooms,

        
        SpaceMembers,

        
        SpaceMenu,

        
        StartChat,

        
        SwitchDirectory,

        
        ThreadList,

        
        User,

        
        Welcome,
    }

    override fun getName() = screenName.name

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            durationMs?.let { put("durationMs", it) }
        }.takeIf { it.isNotEmpty() }
    }
}
