

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class Interaction(
        
        val index: Int? = null,
        
        val interactionType: InteractionType? = null,
        
        val name: Name,
) : VectorAnalyticsEvent {

    enum class Name {
        
        MobileRoomAddHome,

        
        MobileRoomLeave,

        
        MobileRoomThreadListButton,

        
        MobileRoomThreadSummaryItem,

        
        MobileThreadListFilterItem,

        
        MobileThreadListThreadItem,

        
        SpacePanelSelectedSpace,

        
        SpacePanelSwitchSpace,

        
        WebAddExistingToSpaceDialogCreateRoomButton,

        
        WebHomeCreateRoomButton,

        
        WebQuickSettingsPinToSidebarCheckbox,

        
        WebQuickSettingsThemeDropdown,

        
        WebRightPanelMemberListInviteButton,

        
        WebRightPanelRoomInfoPeopleButton,

        
        WebRightPanelRoomInfoSettingsButton,

        
        WebRightPanelRoomUserInfoBackButton,

        
        WebRightPanelRoomUserInfoInviteButton,

        
        WebRightPanelThreadPanelFilterDropdown,

        
        WebRoomDirectoryCreateRoomButton,

        
        WebRoomHeaderButtonsThreadsButton,

        
        WebRoomHeaderContextMenuFavouriteToggle,

        
        WebRoomHeaderContextMenuInviteItem,

        
        WebRoomHeaderContextMenuLeaveItem,

        
        WebRoomHeaderContextMenuNotificationsItem,

        
        WebRoomHeaderContextMenuPeopleItem,

        
        WebRoomHeaderContextMenuSettingsItem,

        
        WebRoomListHeaderPlusMenuCreateRoomItem,

        
        WebRoomListHeaderPlusMenuExploreRoomsItem,

        
        WebRoomListRoomTileContextMenuFavouriteToggle,

        
        WebRoomListRoomTileContextMenuInviteItem,

        
        WebRoomListRoomTileContextMenuLeaveItem,

        
        WebRoomListRoomTileContextMenuSettingsItem,

        
        WebRoomListRoomTileNotificationsMenu,

        
        WebRoomListRoomsSublistPlusMenuCreateRoomItem,

        
        WebRoomListRoomsSublistPlusMenuExploreRoomsItem,

        
        WebRoomSettingsLeaveButton,

        
        WebRoomSettingsSecurityTabCreateNewRoomButton,

        
        WebRoomTimelineThreadSummaryButton,

        
        WebSettingsAppearanceTabThemeSelector,

        
        WebSettingsSidebarTabSpacesCheckbox,

        
        WebSpaceContextMenuExploreRoomsItem,

        
        WebSpaceContextMenuHomeItem,

        
        WebSpaceContextMenuNewRoomItem,

        
        WebSpaceHomeCreateRoomButton,

        
        WebThreadViewBackButton,

        
        WebThreadsPanelThreadItem,

        
        WebUserMenuThemeToggleButton,
    }

    enum class InteractionType {
        Keyboard,
        Pointer,
        Touch,
    }

    override fun getName() = "Interaction"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            index?.let { put("index", it) }
            interactionType?.let { put("interactionType", it.name) }
            put("name", name.name)
        }.takeIf { it.isNotEmpty() }
    }
}
