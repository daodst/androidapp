package im.vector.app.features.me

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.viewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wallet.ctc.ui.me.virtualphone.SMVirtualPhoneActivity
import common.app.utils.SpUtil
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.dialogs.GalleryOrCameraDialogActivityHelper
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.intent.getFilenameFromUri
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.toast
import im.vector.app.databinding.ActivityVectorMeBinding
import im.vector.app.features.createdirect.DirectRoomHelper
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.detail.RoomDetailPendingActionStore
import im.vector.app.features.roommemberprofile.RoomMemberProfileController
import im.vector.app.provide.ChatStatusProvide.getUserId
import im.vector.lib.ui.styles.dialogs.MaterialProgressDialog
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.call.getAddressByUid
import org.matrix.android.sdk.api.util.MatrixItem
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class VectorMeActivity : VectorBaseActivity<ActivityVectorMeBinding>(), GalleryOrCameraDialogActivityHelper.Listener {

    override fun getBinding() = ActivityVectorMeBinding.inflate(layoutInflater)

    @Inject lateinit var roomMemberProfileController: RoomMemberProfileController
    @Inject lateinit var avatarRenderer: AvatarRenderer
    @Inject lateinit var directRoomHelper: DirectRoomHelper
    @Inject lateinit var stringProvider: StringProvider
    @Inject lateinit var roomDetailPendingActionStore: RoomDetailPendingActionStore
    @Inject lateinit var session: Session
    @Inject lateinit var colorProvider: ColorProvider
    @Inject lateinit var errorFormatter: ErrorFormatter
    private lateinit var galleryOrCameraDialogHelper: GalleryOrCameraDialogActivityHelper

    private val viewModel: VectorMeViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        galleryOrCameraDialogHelper = GalleryOrCameraDialogActivityHelper(this, colorProvider)

        viewModel.observeViewEvents {
            when (it) {
                is VectorMeEvents.Loading -> showLoadingDialog(it.message)
                is VectorMeEvents.DisLoading -> dismissLoadingDialog()
                else -> {}
            }
        }
        viewModel.onEach { renderState(it) }

        views.vectorMeBack.setOnClickListener { finish() }
        views.vectorMeNameParent.setOnClickListener {
            
            NickNameDialog().showChoice(this, views.vectorMeName.text.toString()) {
                onDisplayNameChanged(it)
            }
        }
        views.vectorMeIco.setOnClickListener {
            
            galleryOrCameraDialogHelper.show()
        }

        views.vectorMePhoneParent.setOnClickListener {
            startActivity(SMVirtualPhoneActivity.getIntent(this, getAddressByUid(getUserId(this))))
        }
    }

    private fun renderState(it: VectorMeViewState) {
        it.user?.let {
            
            avatarRenderer.render(MatrixItem.UserItem(it.userId, it.displayName, it.avatarUrl), views.vectorMeIco)
            views.vectorMeName.text = it.displayName
            views.vectorMeAddress.text = getAddressByUid(it.userId)
            views.vectorMeAddress.setOnClickListener {
                val clipService = this.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                clipService?.setPrimaryClip(ClipData.newPlainText("", views.vectorMeAddress.text))
                this.toast(R.string.action_copy)
            }

            val phone = it.tel_numbers?.get(0) ?: ""
            val nowPhone = SpUtil.getNowPhone(getAddressByUid(it.userId))
            views.vectorMePhone.text = if (TextUtils.isEmpty(nowPhone)) phone else nowPhone

        }
        it.level?.let {
            views.vectorMeRank1.text = "LV.${it}"
            views.vectorMeRank.text = "LV.${it}"
            views.vectorMeRankLogo.visibility = View.VISIBLE
            views.vectorMeRankLogo.setImageResource(getLevelDrawableRes(it))
            if (it == 0) {
                views.vectorMeContent.text = getString(R.string.im_wallet_profile_rank_content_empty)
            } else {
                views.vectorMeContent.text = getString(R.string.im_wallet_profile_rank_content)
            }
        }
    }

    fun getLevelDrawableRes(level: Int): Int {
        val resName = "sm_pledge$level"
        val packageName = this.packageName
        return this.resources.getIdentifier(resName, "mipmap", packageName)
    }

    private var progress: AlertDialog? = null
    protected fun showLoadingDialog(message: CharSequence? = null) {
        progress?.dismiss()
        progress = MaterialProgressDialog(this)
                .show(message ?: getString(R.string.please_wait))
    }

    protected fun dismissLoadingDialog() {
        progress?.dismiss()
    }

    override fun onImageReady(uri: Uri?) {
        if (uri != null) {
            uploadAvatar(uri)
        } else {
            Toast.makeText(this, "Cannot retrieve cropped value", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadAvatar(uri: Uri) {
        showLoadingDialog()

        lifecycleScope.launch {
            val result = runCatching {
                session.updateAvatar(session.myUserId, uri, getFilenameFromUri(this@VectorMeActivity, uri) ?: UUID.randomUUID().toString())
            }

            result.fold(
                    onSuccess = {
                        dismissLoadingDialog()
                        viewModel.handle(VectorMeAction.RetryFetchingInfo)

                    },
                    onFailure = {
                        dismissLoadingDialog()
                        displayErrorDialog(it)
                    }
            )
        }
    }

    protected fun displayErrorDialog(throwable: Throwable) {
        displayErrorDialog(errorFormatter.toHumanReadable(throwable))
    }

    protected fun displayErrorDialog(errorMessage: String) {
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_error)
                .setMessage(errorMessage)
                .setPositiveButton(R.string.ok, null)
                .show()
    }

    
    private fun onDisplayNameChanged(value: String) {
        if (value.length > 16) {
            Toast.makeText(this, R.string.setting_modify_display_name, Toast.LENGTH_SHORT).show()
            return;
        }
        val currentDisplayName = session.getUser(session.myUserId)?.displayName ?: ""
        if (currentDisplayName != value) {

            showLoadingDialog()
            lifecycleScope.launch {
                val result = runCatching { session.setDisplayName(session.myUserId, value) }
                result.fold(
                        onSuccess = {
                            
                            dismissLoadingDialog()
                            viewModel.handle(VectorMeAction.RefershName(value))
                        },
                        onFailure = {
                            dismissLoadingDialog()
                            displayErrorDialog(it)
                        }
                )
            }
        }
    }
}
