

package im.vector.app.core.ui.views

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.text.italic
import im.vector.app.R
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.error.ResourceLimitErrorFormatter
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.databinding.ViewNotificationAreaBinding
import im.vector.app.features.themes.ThemeUtils
import me.gujun.android.span.span
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.events.model.Event
import timber.log.Timber


class NotificationAreaView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var delegate: Delegate? = null
    private var state: State = State.Initial

    private lateinit var views: ViewNotificationAreaBinding

    init {
        setupView()
    }

    
    fun render(newState: State) {
        if (newState == state) {
            Timber.d("State unchanged")
            return
        }
        Timber.d("Rendering $newState")
        cleanUp()
        state = newState
        when (newState) {
            State.Initial                       -> Unit
            is State.Default                    -> renderDefault()
            is State.Hidden                     -> renderHidden()
            is State.NoPermissionToPost         -> renderNoPermissionToPost()
            is State.UnsupportedAlgorithm       -> renderUnsupportedAlgorithm(newState)
            is State.Tombstone                  -> renderTombstone()
            is State.ResourceLimitExceededError -> renderResourceLimitExceededError(newState)
        }
    }

    

    private fun setupView() {
        inflate(context, R.layout.view_notification_area, this)
        views = ViewNotificationAreaBinding.bind(this)
        minimumHeight = DimensionConverter(resources).dpToPx(48)
    }

    private fun cleanUp() {
        views.roomNotificationMessage.setOnClickListener(null)
        views.roomNotificationIcon.setOnClickListener(null)
        setBackgroundColor(Color.TRANSPARENT)
        views.roomNotificationMessage.text = null
        views.roomNotificationIcon.setImageResource(0)
    }

    private fun renderNoPermissionToPost() {
        visibility = View.VISIBLE
        views.roomNotificationIcon.setImageDrawable(null)
        val message = span {
            italic {
                +resources.getString(R.string.room_do_not_have_permission_to_post)
            }
        }
        views.roomNotificationMessage.text = message
        views.roomNotificationMessage.setTextColor(ThemeUtils.getColor(context, R.attr.vctr_content_secondary))
    }

    private fun renderUnsupportedAlgorithm(e2eState: State.UnsupportedAlgorithm) {
        visibility = View.VISIBLE
        views.roomNotificationIcon.setImageResource(R.drawable.ic_warning_badge)
        val text = if (e2eState.canRestore) {
            R.string.room_unsupported_e2e_algorithm_as_admin
        } else R.string.room_unsupported_e2e_algorithm
        val message = span {
            italic {
                +resources.getString(text)
            }
        }
        views.roomNotificationMessage.onClick {
            delegate?.onMisconfiguredEncryptionClicked()
        }
        views.roomNotificationMessage.text = message
        views.roomNotificationMessage.setTextColor(ThemeUtils.getColor(context, R.attr.vctr_content_secondary))
    }

    private fun renderResourceLimitExceededError(state: State.ResourceLimitExceededError) {
        visibility = View.VISIBLE
        val resourceLimitErrorFormatter = ResourceLimitErrorFormatter(context)
        val formatterMode: ResourceLimitErrorFormatter.Mode
        val backgroundColor: Int
        if (state.isSoft) {
            backgroundColor = R.color.soft_resource_limit_exceeded
            formatterMode = ResourceLimitErrorFormatter.Mode.Soft
        } else {
            backgroundColor = R.color.hard_resource_limit_exceeded
            formatterMode = ResourceLimitErrorFormatter.Mode.Hard
        }
        val message = resourceLimitErrorFormatter.format(state.matrixError, formatterMode, clickable = true)
        views.roomNotificationMessage.setTextColor(Color.WHITE)
        views.roomNotificationMessage.text = message
        views.roomNotificationMessage.movementMethod = LinkMovementMethod.getInstance()
        views.roomNotificationMessage.setLinkTextColor(Color.WHITE)
        setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
    }

    private fun renderTombstone() {
        visibility = View.VISIBLE
        views.roomNotificationIcon.setImageResource(R.drawable.ic_warning_badge)
        val message = span {
            +resources.getString(R.string.room_tombstone_versioned_description)
            +"\n"
            span(resources.getString(R.string.room_tombstone_continuation_link)) {
                textDecorationLine = "underline"
                onClick = { delegate?.onTombstoneEventClicked() }
            }
        }
        views.roomNotificationMessage.movementMethod = BetterLinkMovementMethod.getInstance()
        views.roomNotificationMessage.text = message
    }

    private fun renderDefault() {
        visibility = View.GONE
    }

    private fun renderHidden() {
        visibility = View.GONE
    }

    
    sealed class State {

        
        object Initial : State()

        
        object Default : State()

        
        object NoPermissionToPost : State()
        data class UnsupportedAlgorithm(val canRestore: Boolean) : State()

        
        object Hidden : State()

        
        data class Tombstone(val tombstoneEvent: Event) : State()

        
        data class ResourceLimitExceededError(val isSoft: Boolean, val matrixError: MatrixError) : State()
    }

    
    interface Delegate {
        fun onTombstoneEventClicked()
        fun onMisconfiguredEncryptionClicked()
    }
}
