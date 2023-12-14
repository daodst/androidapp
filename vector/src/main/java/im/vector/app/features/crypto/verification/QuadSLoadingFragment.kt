

package im.vector.app.features.crypto.verification

import android.view.LayoutInflater
import android.view.ViewGroup
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.FragmentProgressBinding
import javax.inject.Inject

class QuadSLoadingFragment @Inject constructor() : VectorBaseFragmentHost<FragmentProgressBinding>() {
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentProgressBinding {
        return FragmentProgressBinding.inflate(inflater, container, false)
    }
}
