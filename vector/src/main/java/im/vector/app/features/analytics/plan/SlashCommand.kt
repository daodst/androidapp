

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class SlashCommand(
        
        val command: Command,
) : VectorAnalyticsEvent {

    enum class Command {
        Invite,
        Part,
    }

    override fun getName() = "SlashCommand"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("command", command.name)
        }.takeIf { it.isNotEmpty() }
    }
}
