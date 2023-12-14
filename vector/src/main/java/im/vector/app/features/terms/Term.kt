

package im.vector.app.features.terms

data class Term(
        val url: String,
        val name: String,
        val version: String? = null,
        val accepted: Boolean = false
)
