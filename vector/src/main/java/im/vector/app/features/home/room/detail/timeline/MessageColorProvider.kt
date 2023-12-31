

package im.vector.app.features.home.room.detail.timeline

import androidx.annotation.ColorInt
import im.vector.app.R
import im.vector.app.core.resources.ColorProvider
import im.vector.app.features.home.room.detail.timeline.helper.MatrixItemColorProvider
import im.vector.app.features.settings.VectorPreferences
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject

class MessageColorProvider @Inject constructor(
        private val colorProvider: ColorProvider,
        private val matrixItemColorProvider: MatrixItemColorProvider,
        private val vectorPreferences: VectorPreferences) {

    @ColorInt
    fun getMemberNameTextColor(matrixItem: MatrixItem): Int {
        return matrixItemColorProvider.getColor(matrixItem)
    }

    @ColorInt
    fun getMessageTextColor(sendState: SendState): Int {
        return if (vectorPreferences.developerMode()) {
            when (sendState) {
                
                SendState.UNKNOWN,
                SendState.UNSENT                 -> colorProvider.getColorFromAttribute(R.attr.vctr_sending_message_text_color)
                SendState.ENCRYPTING             -> colorProvider.getColorFromAttribute(R.attr.vctr_encrypting_message_text_color)
                SendState.SENDING                -> colorProvider.getColorFromAttribute(R.attr.vctr_sending_message_text_color)
                SendState.SENT,
                SendState.SYNCED                 -> colorProvider.getColorFromAttribute(R.attr.vctr_message_text_color)
                SendState.UNDELIVERED,
                SendState.FAILED_UNKNOWN_DEVICES -> colorProvider.getColorFromAttribute(R.attr.vctr_unsent_message_text_color)
            }
        } else {
            
            colorProvider.getColorFromAttribute(R.attr.vctr_message_text_color)
        }
    }
}
