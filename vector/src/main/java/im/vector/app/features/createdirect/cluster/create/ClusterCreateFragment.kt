package im.vector.app.features.createdirect.cluster.create

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
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
import im.vector.app.databinding.FragmentClusterCreateBinding
import im.vector.app.features.createdirect.cluster.ClusterRoomAction
import im.vector.app.features.createdirect.cluster.ClusterViewModel
import im.vector.app.features.home.room.detail.RoomDetailActivity
import im.vector.app.features.home.room.detail.arguments.TimelineArgs
import me.gujun.android.span.span
import java.math.BigDecimal
import javax.inject.Inject


class ClusterCreateFragment @Inject constructor(val colorProvider: ColorProvider) : VectorBaseFragment<FragmentClusterCreateBinding>(), GalleryOrCameraDialogHelper.Listener {

    private val viewModel: ClusterViewModel by fragmentViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentClusterCreateBinding {
        return FragmentClusterCreateBinding.inflate(inflater, container, false)
    }
    private fun setupWaitingView() {
        views.waitingView.waitingStatusText.isVisible = true
        views.waitingView.waitingStatusText.setText(
                R.string.migrate_group_update
        )
    }

    private val galleryOrCameraDialogHelper = GalleryOrCameraDialogHelper(this, colorProvider)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWaitingView()


        views.clusterCreateNote1.text = span {
            span(getString(R.string.cluster_upgrade_note_tips11))
            span(getString(R.string.cluster_upgrade_note_tips12)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note_tips13))
            span(getString(R.string.cluster_upgrade_note_tips14)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note_tips15))
            span(getString(R.string.cluster_upgrade_note_tips16)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note_tips17))
            span(getString(R.string.cluster_upgrade_note_tips18)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }

        }
        views.clusterCreateNote2.text = span {
            span(getString(R.string.cluster_upgrade_note21))
            span(getString(R.string.cluster_upgrade_note22)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note23))
            span(getString(R.string.cluster_upgrade_note24)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
        }
        views.clusterCreateNote3.text = span {
            span(getString(R.string.cluster_upgrade_note31))
            span(getString(R.string.cluster_upgrade_note32)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note33))
            span(getString(R.string.cluster_upgrade_note34)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note35))
        }
        views.clusterCreateNote4.text = span {
            span(getString(R.string.cluster_upgrade_note41))
            span(getString(R.string.cluster_upgrade_note42)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note43))
        }

        views.clusterCreateBack.onClick {
            vectorBaseActivity.finish()
        }

        views.clusterCreateIvTake.onClick {
            galleryOrCameraDialogHelper.show()
        }

        views.clusterCreateBt.onClick {
            create()
        }

        setCkStatus()
        views.destoryCheckableViewParent.onClick {
            isLeftChecked = true
            setCkStatus()
        }
        views.freezeCheckableViewParent.onClick {
            isLeftChecked = false
            setCkStatus()
        }

        views.clusterCreateDestory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.trim()
                if (value.isNullOrEmpty()) {
                    views.clusterCreateDstGas.text = ""
                    return
                }
                getGasWithCheck(value)
            }
        })
    }

    private fun create() = withState(viewModel) { state ->
        val name = views.clusterCreateName.text.toString()
        if (name.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateName.hint.toString())
            return@withState
        }
        val topic = views.clusterCreateTopic.text.toString()?:""
        val rate = views.clusterCreateCommissionRate.text.toString()
        if (rate.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateCommissionRate.hint.toString())
            return@withState
        }
        val deviceMax = state.device_range?.max
        val deviceMin = state.device_range?.min
        if (deviceMax.isNullOrEmpty() || deviceMin.isNullOrEmpty()) {
            vectorBaseActivity.toast(getString(R.string.device_param_fail))
            return@withState
        }
        val rateDecimal = BigDecimal(rate)
        if (rateDecimal > BigDecimal(deviceMax)) {
            vectorBaseActivity.toast(getString(R.string.device_param_max_fail) + deviceMax)
            return@withState
        }
        if (rateDecimal < BigDecimal(deviceMin)) {
            vectorBaseActivity.toast(getString(R.string.device_param_small_fail) + deviceMin)
            return@withState
        }
        val owner = views.clusterCreateOwnerRate.text.toString()
        if (owner.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateOwnerRate.hint.toString())
            return@withState
        }
        val ownerMax = state.salary_range?.max
        val ownerMin = state.salary_range?.min
        if (ownerMax.isNullOrEmpty() || ownerMin.isNullOrEmpty()) {
            vectorBaseActivity.toast(getString(R.string.own_param_fail))
            return@withState
        }
        val ownerDecimal = BigDecimal(owner)
        if (ownerDecimal > BigDecimal(ownerMax)) {
            vectorBaseActivity.toast(getString(R.string.owner_param_max_fail) + deviceMax)
            return@withState
        }
        if (ownerDecimal < BigDecimal(ownerMin)) {
            vectorBaseActivity.toast(getString(R.string.owner_param_small_fail) + deviceMin)
            return@withState
        }
        
        val createClusterMinBurn = state.create_cluster_min_burn
        if (createClusterMinBurn.isNullOrEmpty()) {
            vectorBaseActivity.toast(getString(R.string.create_cluster_min_burn_fail_get))
            return@withState
        }
        val destroy = views.clusterCreateDestory.text.toString()
        if (destroy.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateDestory.hint.toString())
            return@withState
        }

        val destroyDecimal = BigDecimal(destroy)
        if (destroyDecimal < BigDecimal(createClusterMinBurn)) {
            vectorBaseActivity.toast(getString(R.string.create_cluster_min_burn_fail) + createClusterMinBurn + "DST")
            return@withState
        }
        viewModel.handle(ClusterRoomAction.Create(name, topic, rate, owner, destroy, isLeftChecked))
    }

    private fun getGasWithCheck(value: String) = withState(viewModel) { state ->
        val valueDecimal = BigDecimal(value)
        try {
            if (isLeftChecked) {
                if (BigDecimal(state.destoryBalance) < valueDecimal) {
                    vectorBaseActivity.toast(getString(R.string.destory_balance_more) + state.destoryBalance)
                    return@withState
                }
            } else {
                if (BigDecimal(state.freezeBalance) < valueDecimal) {
                    vectorBaseActivity.toast(getString(R.string.destory_balance_small) + state.freezeBalance)
                    return@withState
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getGas(value)
    }

    private fun getGas(value: String) = withState(viewModel) { state ->
        if (isLeftChecked) {
            views.clusterCreateDstGas.text = BigDecimal(state.radio).multiply(BigDecimal(value)).toPlainString()
        } else {
            views.clusterCreateDstGas.text = value
        }
    }

    private fun setCkStatus() {
        if (isLeftChecked) {
            views.destoryCheckableView.isChecked = true
            views.freezeCheckableView.isChecked = false
            views.destoryCheckableViewNum.setTextColor(colorProvider.getColor(R.color.white))
            views.destoryCheckableViewTips.setTextColor(colorProvider.getColor(R.color.white))

            views.freezeCheckableViewNum.setTextColor(colorProvider.getColor(R.color.default_text_color))
            views.freezeCheckableViewTips.setTextColor(colorProvider.getColor(R.color.default_text_color))
        } else {
            views.destoryCheckableView.isChecked = false
            views.freezeCheckableView.isChecked = true
            views.destoryCheckableViewNum.setTextColor(colorProvider.getColor(R.color.default_text_color))
            views.destoryCheckableViewTips.setTextColor(colorProvider.getColor(R.color.default_text_color))

            views.freezeCheckableViewNum.setTextColor(colorProvider.getColor(R.color.white))
            views.freezeCheckableViewTips.setTextColor(colorProvider.getColor(R.color.white))
        }
    }

    var isLeftChecked = true

    override fun invalidate() = withState(viewModel) { state ->
        val async = state.asyncCreateRoomRequest
        views.waitingView.root.isVisible = async is Loading
        if (async is Success) {
            if (state.isUpdate) {
                
                val args = TimelineArgs(state.roomId!!, izCreate = true)
                val intent = RoomDetailActivity.newIntent(vectorBaseActivity, args).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
                vectorBaseActivity.finish()
            } else if (state.isCreated) {
                
                update()
            }
        } else if (async is Fail) {
            async.error.message?.let {
                if (!it.isEmpty()) {
                    vectorBaseActivity.toast(it)
                }
            }
        }
        views.clusterCreateDestory.hint = getString(R.string.create_cluster_min_burn_fail) + state.create_cluster_min_burn + "DST"
        views.destoryCheckableViewNum.text = state.destoryBalance
        views.freezeCheckableViewNum.text = state.freezeBalance
    }

    private fun update() {
        val name = views.clusterCreateName.text.toString()
        if (name.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateName.hint.toString())
            return
        }
        val topic = views.clusterCreateTopic.text.toString()
        if (topic.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateTopic.hint.toString())
            return
        }
        val rate = views.clusterCreateCommissionRate.text.toString()
        if (rate.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateCommissionRate.hint.toString())
            return
        }
        val owner = views.clusterCreateOwnerRate.text.toString()
        if (owner.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateOwnerRate.hint.toString())
            return
        }

        val destroy = views.clusterCreateDestory.text.toString()
        if (destroy.isEmpty()) {
            vectorBaseActivity.toast(views.clusterCreateDestory.hint.toString())
            return
        }

        viewModel.handle(ClusterRoomAction.UpGrade(vectorBaseActivity, rate, owner, destroy, isLeftChecked))
    }

    override fun onImageReady(uri: Uri?) {
        GlideApp.with(views.clusterCreateIv)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(views.clusterCreateIv)
        viewModel.handle(ClusterRoomAction.SetAvatar(uri))
    }
}
