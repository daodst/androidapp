
package im.vector.app.core.ui.bottomsheet

import com.airbnb.epoxy.TypedEpoxyController


abstract class BottomSheetGenericController<State : BottomSheetGenericState, Action : BottomSheetGenericRadioAction> :
        TypedEpoxyController<State>() {

    var listener: Listener<Action>? = null

    abstract fun getTitle(): String?

    open fun getSubTitle(): String? = null

    abstract fun getActions(state: State): List<Action>

    override fun buildModels(state: State?) {
        state ?: return
        val host = this
        
        getTitle()?.let { title ->
            bottomSheetTitleItem {
                id("title")
                title(title)
                subTitle(host.getSubTitle())
            }

        }
        
        val actions = getActions(state)
        actions.forEach { action ->
            action.toRadioBottomSheetItem()
                    .listener { listener?.didSelectAction(action) }
                    .addTo(this)
        }
    }

    interface Listener<Action> {
        fun didSelectAction(action: Action)
    }
}
