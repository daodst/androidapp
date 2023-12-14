

package im.vector.app.core.platform


data class WaitingViewData(
        val message: String,
        val progress: Int? = null,
        val progressTotal: Int? = null,
        val isIndeterminate: Boolean = false
)
