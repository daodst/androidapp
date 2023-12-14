

package im.vector.app.features.analytics.plan



data class UserProperties(
        
        val WebMetaSpaceFavouritesEnabled: Boolean? = null,
        
        val WebMetaSpaceHomeAllRooms: Boolean? = null,
        
        val WebMetaSpaceHomeEnabled: Boolean? = null,
        
        val WebMetaSpaceOrphansEnabled: Boolean? = null,
        
        val WebMetaSpacePeopleEnabled: Boolean? = null,
        
        val ftueUseCaseSelection: FtueUseCaseSelection? = null,
        
        val numFavouriteRooms: Int? = null,
        
        val numSpaces: Int? = null,
) {

    enum class FtueUseCaseSelection {
        
        CommunityMessaging,

        
        PersonalMessaging,

        
        Skip,

        
        WorkMessaging,
    }

    fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            WebMetaSpaceFavouritesEnabled?.let { put("WebMetaSpaceFavouritesEnabled", it) }
            WebMetaSpaceHomeAllRooms?.let { put("WebMetaSpaceHomeAllRooms", it) }
            WebMetaSpaceHomeEnabled?.let { put("WebMetaSpaceHomeEnabled", it) }
            WebMetaSpaceOrphansEnabled?.let { put("WebMetaSpaceOrphansEnabled", it) }
            WebMetaSpacePeopleEnabled?.let { put("WebMetaSpacePeopleEnabled", it) }
            ftueUseCaseSelection?.let { put("ftueUseCaseSelection", it.name) }
            numFavouriteRooms?.let { put("numFavouriteRooms", it) }
            numSpaces?.let { put("numSpaces", it) }
        }.takeIf { it.isNotEmpty() }
    }
}
