

package im.vector.app.features.home

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.FragmentLoadingBinding
import javax.inject.Inject

class LoadingFragment @Inject constructor() : VectorBaseFragment<FragmentLoadingBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoadingBinding {
        return FragmentLoadingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val background = views.animatedLogoImageView.background
        if (background is AnimationDrawable) {
            background.start()
        }
    }
}
