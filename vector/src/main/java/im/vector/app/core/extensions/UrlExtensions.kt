

package im.vector.app.core.extensions


fun String?.toReducedUrl(keepSchema: Boolean = false): String {
    return (this ?: "")
            .run { if (keepSchema) this else substringAfter("://") }
            .trim { it == '/' }
}
