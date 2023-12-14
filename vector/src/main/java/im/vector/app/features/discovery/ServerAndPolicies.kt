

package im.vector.app.features.discovery

data class ServerAndPolicies(
        val serverUrl: String,
        val policies: List<ServerPolicy>
)

data class ServerPolicy(
        val name: String,
        val url: String
)
