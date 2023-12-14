package im.vector.app.features.createdirect.migrate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.airbnb.mvrx.Mavericks
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.SimpleFragmentActivity
import kotlinx.parcelize.Parcelize

@Parcelize data class MigrateGroupArgs(
        val roomId: String,
        val groupName: String,
) : Parcelable

@AndroidEntryPoint
class MigrateGroupActivity : SimpleFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views.toolbar.visibility = View.GONE
        val args: MigrateGroupArgs = intent.getParcelableExtra(Mavericks.KEY_ARG) ?: return


        replaceFragment(
                views.container,
                MigrateGroupFragment::class.java, args
        )
    }

    companion object {
        fun newIntent(context: Context, roomId: String, groupName: String): Intent {
            return Intent(context, MigrateGroupActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, MigrateGroupArgs(roomId, groupName))
            }
        }
    }
}
