

package im.vector.app.features.html

import android.content.Context
import android.text.Spannable
import android.text.Spanned
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.glide.GlideApp
import im.vector.app.features.home.AvatarRenderer
import io.noties.markwon.core.spans.LinkSpan
import org.matrix.android.sdk.api.session.permalinks.PermalinkData
import org.matrix.android.sdk.api.session.permalinks.PermalinkParser
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem

class PillsPostProcessor @AssistedInject constructor(@Assisted private val roomId: String?,
                                                     private val context: Context,
                                                     private val avatarRenderer: AvatarRenderer,
                                                     private val sessionHolder: ActiveSessionHolder) :
        EventHtmlRenderer.PostProcessor {

    

    @AssistedFactory
    interface Factory {
        fun create(roomId: String?): PillsPostProcessor
    }

    

    override fun afterRender(renderedText: Spannable) {
        addPillSpans(renderedText, roomId)
    }

    

    private fun addPillSpans(renderedText: Spannable, roomId: String?) {
        addLinkSpans(renderedText, roomId)
    }

    private fun addPillSpan(
            renderedText: Spannable,
            pillSpan: PillImageSpan,
            startSpan: Int,
            endSpan: Int
    ) {
        renderedText.setSpan(pillSpan, startSpan, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun addLinkSpans(renderedText: Spannable, roomId: String?) {
        
        val linkSpans = renderedText.getSpans(0, renderedText.length, LinkSpan::class.java)
        linkSpans.forEach { linkSpan ->
            val isGroup = sessionHolder.getSafeActiveSession()?.getRoomSummary(roomId ?: "")?.isGroup ?: false

            val pillSpan = linkSpan.createPillSpan(roomId, isGroup) ?: return@forEach

            val startSpan = renderedText.getSpanStart(linkSpan)
            val endSpan = renderedText.getSpanEnd(linkSpan)
            addPillSpan(renderedText, pillSpan, startSpan, endSpan)
        }
    }

    private fun createPillImageSpan(matrixItem: MatrixItem) =
            PillImageSpan(GlideApp.with(context), avatarRenderer, context, matrixItem)

    private fun LinkSpan.createPillSpan(roomId: String?, isGroup: Boolean = false): PillImageSpan? {
        val permalinkData = PermalinkParser.parse(url)
        val matrixItem = when (permalinkData) {

            is PermalinkData.UserLink  -> permalinkData.toMatrixItem(roomId, isGroup)
            is PermalinkData.RoomLink  -> permalinkData.toMatrixItem()
            is PermalinkData.GroupLink -> permalinkData.toMatrixItem()
            else                       -> null
        } ?: return null
        return createPillImageSpan(matrixItem)
    }

    private fun PermalinkData.UserLink.toMatrixItem(roomId: String?, isGroup: Boolean = false): MatrixItem? {
        val userItem = if (roomId == null) {
            sessionHolder.getSafeActiveSession()?.getUser(userId)?.toMatrixItem(isGroup)
        } else {
            sessionHolder.getSafeActiveSession()?.getRoomMember(userId, roomId)?.toMatrixItem(isGroup)
        }
        return userItem
    }

    private fun PermalinkData.RoomLink.toMatrixItem(): MatrixItem? =
            if (eventId == null) {
                val room: RoomSummary? = sessionHolder.getSafeActiveSession()?.getRoomSummary(roomIdOrAlias)
                when {
                    isRoomAlias -> MatrixItem.RoomAliasItem(roomIdOrAlias, room?.displayName, room?.avatarUrl)
                    else        -> MatrixItem.RoomItem(roomIdOrAlias, room?.displayName, room?.avatarUrl)
                }
            } else {
                
                null
            }

    private fun PermalinkData.GroupLink.toMatrixItem(): MatrixItem? {
        val group = sessionHolder.getSafeActiveSession()?.getGroupSummary(groupId)
        return MatrixItem.GroupItem(groupId, group?.displayName, group?.avatarUrl)
    }
}
