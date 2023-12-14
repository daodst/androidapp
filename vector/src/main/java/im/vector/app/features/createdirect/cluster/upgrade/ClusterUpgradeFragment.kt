package im.vector.app.features.createdirect.cluster.upgrade

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.utils.toast
import im.vector.app.databinding.FragmentClusterUpgradeBinding
import im.vector.app.features.createdirect.cluster.ClusterArgs
import im.vector.app.features.createdirect.cluster.ClusterRoomAction
import im.vector.app.features.createdirect.cluster.ClusterViewModel
import im.vector.app.features.home.AvatarRenderer
import kotlinx.coroutines.launch
import me.gujun.android.span.span
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.util.MatrixItem
import java.math.BigDecimal
import javax.inject.Inject

class ClusterUpgradeFragment @Inject constructor(private val activeSessionHolder: ActiveSessionHolder, val avatarRenderer: AvatarRenderer, val colorProvider: ColorProvider) : VectorBaseFragment<FragmentClusterUpgradeBinding>() {

    private val args: ClusterArgs by args()

    private val viewModel: ClusterViewModel by fragmentViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentClusterUpgradeBinding {
        return FragmentClusterUpgradeBinding.inflate(inflater, container, false)
    }
    private fun setupWaitingView() {
        views.waitingView.waitingStatusText.isVisible = true
        views.waitingView.waitingStatusText.setText(
                R.string.migrate_group_update
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWaitingView()

        avatarRenderer.render(MatrixItem.RoomItem(args.roomId!!, args.name, args.avatarUri), views.clusterUpgradeIv)

        views.clusterUpgradeBack.onClick {
            vectorBaseActivity.finish()
        }

        views.clusterUpgradeBt.onClick {

            upgradeClickCheck()
        }


        views.clusterUpgradeNote1.text = span {
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
        views.clusterUpgradeNote2.text = span {
            span(getString(R.string.cluster_upgrade_note21))
            span(getString(R.string.cluster_upgrade_note22)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note23))
            span(getString(R.string.cluster_upgrade_note24)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
        }
        views.clusterUpgradeNote3.text = span {
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
        views.clusterUpgradeNote4.text = span {
            span(getString(R.string.cluster_upgrade_note41))
            span(getString(R.string.cluster_upgrade_note42)) {
                textColor = colorProvider.getColor(R.color.color_FF7F00)
            }
            span(getString(R.string.cluster_upgrade_note43))
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


        views.clusterUpgradeDestory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.trim()
                if (value.isNullOrEmpty()) {
                    views.clusterUpgradeDstGas.text = ""
                    return
                }
                getGasWithCheck(value)
            }
        })
    }

    private fun upgradeClickCheck() = withState(viewModel) { state ->
        if (state.currentRoomJoinRules == RoomJoinRules.INVITE) {
            upgradeClick()
            return@withState
        }
        val session = activeSessionHolder.getSafeActiveSession() ?: return@withState
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_title_warning)
                .setMessage(getString(R.string.dialog_title_cus_change_rule))
                .setPositiveButton(R.string.ok) { _, _ ->
                    lifecycleScope.launch {
                        session.getRoom(args.roomId!!)?.setJoinRuleInviteOnly()
                        viewModel.handle(ClusterRoomAction.ChangeRoomRule(RoomJoinRules.INVITE))
                        upgradeClick()
                    }
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()

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

    private fun upgradeClick() = withState(viewModel) { state ->
        val rate = views.clusterUpgradeCommissionRate.text.toString()
        if (rate.isEmpty()) {
            vectorBaseActivity.toast(views.clusterUpgradeCommissionRate.hint.toString())
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

        val owner = views.clusterUpgradeOwnerRate.text.toString()
        if (owner.isEmpty()) {
            vectorBaseActivity.toast(views.clusterUpgradeOwnerRate.hint.toString())
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

        val destroy = views.clusterUpgradeDestory.text.toString()
        if (destroy.isEmpty()) {
            vectorBaseActivity.toast(views.clusterUpgradeDestory.hint.toString())
            return@withState
        }

        val destroyDecimal = BigDecimal(destroy)
        if (destroyDecimal < BigDecimal(createClusterMinBurn)) {
            vectorBaseActivity.toast(getString(R.string.create_cluster_min_burn_fail) + createClusterMinBurn + "DST")
            return@withState
        }

        viewModel.handle(ClusterRoomAction.UpGrade(vectorBaseActivity, rate, owner, destroy, isLeftChecked))
    }

    private fun getGas(value: String) = withState(viewModel) { state ->
        if (isLeftChecked) {
            try {
                views.clusterUpgradeDstGas.text = BigDecimal(state.radio).multiply(BigDecimal(value)).toPlainString()
            } catch (e: Exception) {
                views.clusterUpgradeDstGas.text = "--"
            }
        } else {
            views.clusterUpgradeDstGas.text = value
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        val async = state.asyncCreateRoomRequest
        views.waitingView.root.isVisible = async is Loading
        if (async is Success) {
            vectorBaseActivity.setResult(Activity.RESULT_OK)
            vectorBaseActivity.finish()
        } else if (async is Fail) {
            async.error.message?.let {
                if (!it.isEmpty()) {
                    vectorBaseActivity.toast(it)
                }
            }
        }
        views.clusterUpgradeDestory.hint = getString(R.string.create_cluster_min_burn_fail) + state.create_cluster_min_burn + "DST"
        views.destoryCheckableViewNum.text = state.destoryBalance
        views.freezeCheckableViewNum.text = state.freezeBalance
    }

    var isLeftChecked = true

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
}
