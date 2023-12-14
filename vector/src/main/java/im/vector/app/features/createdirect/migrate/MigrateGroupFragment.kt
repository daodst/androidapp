package im.vector.app.features.createdirect.migrate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.request.RequestOptions
import im.vector.app.R
import im.vector.app.core.dialogs.GalleryOrCameraDialogHelper
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.glide.GlideApp
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.utils.toast
import im.vector.app.databinding.FragmentMigrateGroupBinding
import im.vector.app.features.home.room.detail.RoomDetailActivity
import im.vector.app.features.home.room.detail.arguments.TimelineArgs
import javax.inject.Inject

class MigrateGroupFragment @Inject constructor(val colorProvider: ColorProvider) : VectorBaseFragment<FragmentMigrateGroupBinding>(), GalleryOrCameraDialogHelper.Listener {

    private val viewModel: MigrateGroupViewModel by fragmentViewModel()

    private val galleryOrCameraDialogHelper = GalleryOrCameraDialogHelper(this, colorProvider)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMigrateGroupBinding {
        return FragmentMigrateGroupBinding.inflate(inflater, container, false)
    }

    private val args: MigrateGroupArgs by args()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWaitingView()

        views.migrateBack.onClick {
            vectorBaseActivity.finish()
        }
        views.migrateIvTake.onClick {
            galleryOrCameraDialogHelper.show()
        }
        views.migrateBt.onClick {
            val name = views.migrateName.text.toString()
            if (name.isEmpty()) {
                vectorBaseActivity.toast(views.migrateName.hint.toString())
                return@onClick
            }
            val topic = views.migrateTopic.text.toString() ?: ""

            viewModel.handle(MigrateGroupAction.Create(vectorBaseActivity, name = name, topic = topic))
        }
        views.migrateName.setText(args.groupName)
    }

    private fun setupWaitingView() {
        views.waitingView.waitingStatusText.isVisible = true
        views.waitingView.waitingStatusText.setText(
                R.string.migrate_group_update
        )
    }

    override fun invalidate() = withState(viewModel) { state ->
        super.invalidate()
        val async = state.asyncCreateRoomRequest
        views.waitingView.root.isVisible = async is Loading
        if (async is Success) {
            val args = TimelineArgs(async(), izCreate = true)
            val intent = RoomDetailActivity.newIntent(vectorBaseActivity, args).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            vectorBaseActivity.finish()
        } else if (async is Fail) {
            async.error.message?.let {
                if (!it.isEmpty()) {
                    vectorBaseActivity.toast(it)
                }
            }
        }
    }

    override fun onImageReady(uri: Uri?) {
        GlideApp.with(views.migrateIv)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(views.migrateIv)
        viewModel.handle(MigrateGroupAction.SetAvatar(uri))
    }
}
