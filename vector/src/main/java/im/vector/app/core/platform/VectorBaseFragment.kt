

package im.vector.app.core.platform

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.MavericksView
import com.bumptech.glide.util.Util.assertMainThread
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.EntryPointAccessors
import im.vector.app.R
import im.vector.app.core.di.ActivityEntryPoint
import im.vector.app.core.dialogs.UnrecognizedCertificateDialog
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.extensions.toMvRxBundle
import im.vector.app.core.utils.ToolbarConfig
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.navigation.Navigator
import im.vector.lib.ui.styles.dialogs.MaterialProgressDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import timber.log.Timber

abstract class VectorBaseFragment<VB : ViewBinding> : Fragment(), MavericksView {
    

    protected var analyticsScreenName: MobileScreen.ScreenName? = null

    protected lateinit var analyticsTracker: AnalyticsTracker

    

    protected val vectorBaseActivity: VectorBaseActivity<*> by lazy {
        activity as VectorBaseActivity<*>
    }

    

    protected lateinit var navigator: Navigator
    protected lateinit var errorFormatter: ErrorFormatter
    protected lateinit var unrecognizedCertificateDialog: UnrecognizedCertificateDialog

    private var progress: AlertDialog? = null

    
    protected var toolbar: ToolbarConfig? = null
        get() = (activity as? VectorBaseActivity<*>)?.toolbar
        private set
    

    private lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val activityViewModelProvider
        get() = ViewModelProvider(requireActivity(), viewModelFactory)

    protected val fragmentViewModelProvider
        get() = ViewModelProvider(this, viewModelFactory)

    

    private var _binding: VB? = null

    
    protected val views: VB
        get() = _binding!!

    

    override fun onAttach(context: Context) {
        val singletonEntryPoint = context.singletonEntryPoint()
        val activityEntryPoint = EntryPointAccessors.fromActivity(vectorBaseActivity, ActivityEntryPoint::class.java)
        navigator = singletonEntryPoint.navigator()
        errorFormatter = singletonEntryPoint.errorFormatter()
        analyticsTracker = singletonEntryPoint.analyticsTracker()
        unrecognizedCertificateDialog = singletonEntryPoint.unrecognizedCertificateDialog()
        viewModelFactory = activityEntryPoint.viewModelFactory()
        childFragmentManager.fragmentFactory = activityEntryPoint.fragmentFactory()
        super.onAttach(context)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getMenuRes() != -1) {
            setHasOptionsMenu(true)
        }
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.i("onCreateView Fragment ${javaClass.simpleName}")
        _binding = getBinding(inflater, container)
        return views.root
    }

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    @CallSuper
    override fun onResume() {
        super.onResume()
        Timber.i("onResume Fragment ${javaClass.simpleName}")
        analyticsScreenName?.let {
            analyticsTracker.screen(MobileScreen(screenName = it))
        }
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        Timber.i("onPause Fragment ${javaClass.simpleName}")
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("onViewCreated Fragment ${javaClass.simpleName}")
    }

    open fun showLoading(message: CharSequence?) {
        showLoadingDialog(message)
    }

    open fun showFailure(throwable: Throwable) {
        displayErrorDialog(throwable)
    }

    @CallSuper
    override fun onDestroyView() {
        Timber.i("onDestroyView Fragment ${javaClass.simpleName}")
        _binding = null
        dismissLoadingDialog()
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        Timber.i("onDestroy Fragment ${javaClass.simpleName}")
        super.onDestroy()
    }

    

    private val restorables = ArrayList<Restorable>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        restorables.forEach { it.onSaveInstanceState(outState) }
        restorables.clear()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        restorables.forEach { it.onRestoreInstanceState(savedInstanceState) }
        super.onViewStateRestored(savedInstanceState)
    }

    override fun invalidate() {
        
        Timber.v("invalidate() method has not been implemented")
    }

    protected fun setArguments(args: Parcelable? = null) {
        arguments = args.toMvRxBundle()
    }

    @MainThread
    protected fun <T : Restorable> T.register(): T {
        assertMainThread()
        restorables.add(this)
        return this
    }

    protected fun showErrorInSnackbar(throwable: Throwable) {
        vectorBaseActivity.getCoordinatorLayout()?.showOptimizedSnackbar(errorFormatter.toHumanReadable(throwable))
    }

    protected fun showLoadingDialog(message: CharSequence? = null) {
        progress?.dismiss()
        progress = MaterialProgressDialog(requireContext())
                .show(message ?: getString(R.string.please_wait))
    }

    protected fun dismissLoadingDialog() {
        progress?.dismiss()
    }

    

    
    protected fun setupToolbar(toolbar: MaterialToolbar): ToolbarConfig {
        return vectorBaseActivity.setupToolbar(toolbar)
    }

    

    protected fun <T : VectorViewEvents> VectorViewModel<*, *, T>.observeViewEvents(observer: (T) -> Unit) {
        viewEvents
                .stream()
                .onEach {
                    dismissLoadingDialog()
                    observer(it)
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    

    protected fun View.debouncedClicks(onClicked: () -> Unit) {
        clicks()
                .onEach { onClicked() }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    

    open fun getMenuRes() = -1

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = getMenuRes()

        if (menuRes != -1) {
            inflater.inflate(menuRes, menu)
        }
    }

    
    protected fun invalidateOptionsMenu() = requireActivity().invalidateOptionsMenu()

    

    protected fun displayErrorDialog(throwable: Throwable) {
        MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.dialog_title_error)
                .setMessage(errorFormatter.toHumanReadable(throwable))
                .setPositiveButton(R.string.ok, null)
                .show()
    }
}
