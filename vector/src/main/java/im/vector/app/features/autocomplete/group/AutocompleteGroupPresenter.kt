

package im.vector.app.features.autocomplete.group

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import im.vector.app.features.autocomplete.AutocompleteClickListener
import im.vector.app.features.autocomplete.RecyclerViewPresenter
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.group.groupSummaryQueryParams
import org.matrix.android.sdk.api.session.group.model.GroupSummary
import javax.inject.Inject

class AutocompleteGroupPresenter @Inject constructor(context: Context,
                                                     private val controller: AutocompleteGroupController,
                                                     private val session: Session
) : RecyclerViewPresenter<GroupSummary>(context), AutocompleteClickListener<GroupSummary> {

    init {
        controller.listener = this
    }

    fun clear() {
        controller.listener = null
    }

    override fun instantiateAdapter(): RecyclerView.Adapter<*> {
        return controller.adapter
    }

    override fun onItemClick(t: GroupSummary) {
        dispatchClick(t)
    }

    override fun onQuery(query: CharSequence?) {
        val queryParams = groupSummaryQueryParams {
            displayName = if (query.isNullOrBlank()) {
                QueryStringValue.IsNotEmpty
            } else {
                QueryStringValue.Contains(query.toString(), QueryStringValue.Case.INSENSITIVE)
            }
        }
        val groups = session.getGroupSummaries(queryParams)
                .asSequence()
                .sortedBy { it.displayName }
        controller.setData(groups.toList())
    }
}
