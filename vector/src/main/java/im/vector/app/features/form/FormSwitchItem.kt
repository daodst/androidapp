

package im.vector.app.features.form

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.google.android.material.switchmaterial.SwitchMaterial
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.setValueOnce
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_form_switch)
abstract class FormSwitchItem : VectorEpoxyModel<FormSwitchItem.Holder>() {

    @EpoxyAttribute
    var listener: ((Boolean) -> Unit)? = null

    @EpoxyAttribute
    var enabled: Boolean = true

    @EpoxyAttribute
    var switchChecked: Boolean = false

    @EpoxyAttribute
    var title: String? = null

    @EpoxyAttribute
    var summary: String? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.setOnClickListener {
            if (enabled) {
                holder.switchView.toggle()
            }
        }

        holder.titleView.text = title
        holder.summaryView.setTextOrHide(summary)

        holder.switchView.isEnabled = enabled

        holder.setValueOnce(holder.switchView, switchChecked) { _, isChecked ->
            listener?.invoke(isChecked)
        }
    }

    override fun shouldSaveViewState(): Boolean {
        return false
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)

        holder.switchView.setOnCheckedChangeListener(null)
    }

    class Holder : VectorEpoxyHolder() {
        val titleView by bind<TextView>(R.id.formSwitchTitle)
        val summaryView by bind<TextView>(R.id.formSwitchSummary)
        val switchView by bind<SwitchMaterial>(R.id.formSwitchSwitch)
    }
}
