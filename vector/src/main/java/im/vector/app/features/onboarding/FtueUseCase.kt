

package im.vector.app.features.onboarding

enum class FtueUseCase(val persistableValue: String) {
    FRIENDS_FAMILY("friends_family"),
    TEAMS("teams"),
    COMMUNITIES("communities"),
    SKIP("skip");

    companion object {
        fun from(persistedValue: String) = values().first { it.persistableValue == persistedValue }
    }
}
