

package im.vector.app.features.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.args
import com.mapbox.mapboxsdk.maps.MapView
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.openLocation
import im.vector.app.databinding.FragmentLocationPreviewBinding
import im.vector.app.features.home.room.detail.timeline.helper.LocationPinProvider
import java.lang.ref.WeakReference
import javax.inject.Inject


class LocationPreviewFragment @Inject constructor(
        private val urlMapProvider: UrlMapProvider,
        private val locationPinProvider: LocationPinProvider
) : VectorBaseFragment<FragmentLocationPreviewBinding>() {

    private val args: LocationSharingArgs by args()

    
    private var mapView: WeakReference<MapView>? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLocationPreviewBinding {
        return FragmentLocationPreviewBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = WeakReference(views.mapView)
        views.mapView.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            views.mapView.initialize(urlMapProvider.getMapUrl())
            loadPinDrawable()
        }
    }

    override fun onResume() {
        super.onResume()
        views.mapView.onResume()
    }

    override fun onPause() {
        views.mapView.onPause()
        super.onPause()
    }

    override fun onLowMemory() {
        views.mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        views.mapView.onStart()
    }

    override fun onStop() {
        views.mapView.onStop()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        views.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        mapView?.get()?.onDestroy()
        mapView?.clear()
        super.onDestroy()
    }

    override fun getMenuRes() = R.menu.menu_location_preview

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_external -> {
                onShareLocationExternal()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onShareLocationExternal() {
        val location = args.initialLocationData ?: return
        openLocation(requireActivity(), location.latitude, location.longitude)
    }

    private fun loadPinDrawable() {
        val location = args.initialLocationData ?: return
        val userId = args.locationOwnerId

        locationPinProvider.create(userId) { pinDrawable ->
            lifecycleScope.launchWhenResumed {
                views.mapView.render(
                        MapState(
                                zoomOnlyOnce = true,
                                userLocationData = location,
                                pinId = args.locationOwnerId ?: DEFAULT_PIN_ID,
                                pinDrawable = pinDrawable
                        )
                )
            }
        }
    }
}
