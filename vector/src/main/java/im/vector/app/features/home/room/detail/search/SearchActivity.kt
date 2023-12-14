

package im.vector.app.features.home.room.detail.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.airbnb.mvrx.Mavericks
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivitySearchBinding

@AndroidEntryPoint
class SearchActivity : VectorBaseActivity<ActivitySearchBinding>() {

    private val searchFragment: SearchFragment?
        get() {
            return supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as? SearchFragment
        }

    override fun getBinding() = ActivitySearchBinding.inflate(layoutInflater)

    override fun getCoordinatorLayout() = views.coordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar(views.searchToolbar)
                .allowBack()
    }

    override fun initUiAndData() {
        if (isFirstCreation()) {
            val fragmentArgs: SearchArgs = intent?.extras?.getParcelable(Mavericks.KEY_ARG) ?: return
            addFragment(views.searchFragmentContainer, SearchFragment::class.java, fragmentArgs, FRAGMENT_TAG)
        }
        views.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchFragment?.search(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        
        views.searchView.requestFocus()
    }

    companion object {
        private const val FRAGMENT_TAG = "SearchFragment"

        fun newIntent(context: Context, args: SearchArgs): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                
                
                putExtra(Mavericks.KEY_ARG, args)
            }
        }
    }
}
