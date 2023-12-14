package im.vector.app.features.pay4invite

import android.content.Context
import android.content.Intent
import com.airbnb.mvrx.Mavericks
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivitySimpleBinding
import org.matrix.android.sdk.api.session.utils.bean.UserByPhone


@AndroidEntryPoint
class Pay4InviteActivity : VectorBaseActivity<ActivitySimpleBinding>() {
    override fun getBinding() = ActivitySimpleBinding.inflate(layoutInflater)

    override fun initUiAndData() {
        val args = parseArgs()
        if (null == args || args.displayMode == Pay4InviteDisplayMode.NONE || args.userByPhones.isEmpty()) {
            finish()
            return
        }
        addFragment(views.simpleFragmentContainer, Pay4InviteFragment::class.java, args)
    }

    private fun parseArgs(): Pay4InviteArgs? {
        return intent.getParcelableExtra(Mavericks.KEY_ARG)
    }

    
    companion object {
        fun newIntent(context: Context,
                      displayMode: Pay4InviteDisplayMode = Pay4InviteDisplayMode.NONE,
                      userByPhones: List<UserByPhone> = ArrayList()): Intent {
            val args = Pay4InviteArgs(displayMode, userByPhones)
            return Intent(context, Pay4InviteActivity::class.java).apply {
                putExtra(Mavericks.KEY_ARG, args)
            }
        }
    }
}
