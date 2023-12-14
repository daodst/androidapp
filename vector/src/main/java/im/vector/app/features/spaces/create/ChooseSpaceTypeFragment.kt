

package im.vector.app.features.spaces.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.FragmentSpaceCreateChooseTypeBinding
import javax.inject.Inject

class ChooseSpaceTypeFragment @Inject constructor() : VectorBaseFragment<FragmentSpaceCreateChooseTypeBinding>() {

    private val sharedViewModel: CreateSpaceViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentSpaceCreateChooseTypeBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.publicButton.onClick {
            sharedViewModel.handle(CreateSpaceAction.SetRoomType(SpaceType.Public))
        }

        views.privateButton.onClick {
            sharedViewModel.handle(CreateSpaceAction.SetRoomType(SpaceType.Private))
        }
    }
}
