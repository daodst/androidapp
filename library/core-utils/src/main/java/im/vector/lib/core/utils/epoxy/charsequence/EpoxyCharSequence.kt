

package im.vector.lib.core.utils.epoxy.charsequence


class EpoxyCharSequence(val charSequence: CharSequence) {
    private val hash = charSequence.toString().hashCode()

    override fun hashCode() = hash
    override fun equals(other: Any?) = other is EpoxyCharSequence && other.hash == hash
}
