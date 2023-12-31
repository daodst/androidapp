

package im.vector.app.features.home.room.detail.timeline.helper

import androidx.recyclerview.widget.DiffUtil
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class TimelineEventDiffUtilCallback(private val oldList: List<TimelineEvent>,
                                    private val newList: List<TimelineEvent>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.localId == newItem.localId && oldItem.senderInfo.remark == newItem.senderInfo.remark
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
