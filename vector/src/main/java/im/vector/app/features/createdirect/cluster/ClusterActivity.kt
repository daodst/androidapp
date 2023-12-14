package im.vector.app.features.createdirect.cluster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.airbnb.mvrx.Mavericks
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.SimpleFragmentActivity
import im.vector.app.features.createdirect.cluster.create.ClusterCreateFragment
import im.vector.app.features.createdirect.cluster.upgrade.ClusterUpgradeFragment
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Parcelize data class ClusterArgs(
        val mode: Int = 0,
        val roomId: String? = null,
        val name: String? = null,
        val avatarUri: String? = null,
) : Parcelable


@AndroidEntryPoint
class ClusterActivity : SimpleFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        views.toolbar.visibility = View.GONE

        val args: ClusterArgs = intent.getParcelableExtra(Mavericks.KEY_ARG) ?: return

        Timber.i("-----------ClusterActivity--------------------${args}-------")
        if (isFirstCreation()) {
            if (args.mode == 0) {
                replaceFragment(
                        views.container,
                        ClusterUpgradeFragment::class.java, args
                )
            } else {
                replaceFragment(
                        views.container,
                        ClusterCreateFragment::class.java, args
                )
            }
        }
    }

    companion object {

        fun newIntent(context: Context, mode: Int = 0, roomId: String? = null, name: String? = null,avatarUri: String? = null): Intent {
            return Intent(context, ClusterActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, ClusterArgs(mode, roomId,name,avatarUri))
            }
        }
    }
}
