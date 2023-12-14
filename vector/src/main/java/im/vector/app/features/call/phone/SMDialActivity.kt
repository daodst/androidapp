package im.vector.app.features.call.phone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.SimpleFragmentActivity

@AndroidEntryPoint
class SMDialActivity : SimpleFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        views.toolbar.visibility = View.GONE


        if (isFirstCreation()) {
            replaceFragment(
                    views.container,
                    SMDialFragment::class.java
            )
        }
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, SMDialActivity::class.java)
        }
    }
}
