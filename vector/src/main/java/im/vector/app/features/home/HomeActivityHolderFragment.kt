package im.vector.app.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.FragmentHomeActivityHolderBinding


class HomeActivityHolderFragment : VectorBaseFragmentHost<FragmentHomeActivityHolderBinding>() {

    fun onNewIntent(intent: Intent?) {
        println("=====$intent=====")
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeActivityHolderBinding {
        return FragmentHomeActivityHolderBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
