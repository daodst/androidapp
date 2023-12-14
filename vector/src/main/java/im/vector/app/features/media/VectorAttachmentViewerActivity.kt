
package im.vector.app.features.media

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.transition.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.transition.Transition
import com.airbnb.mvrx.viewModel
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.core.intent.getMimeTypeFromUri
import im.vector.app.core.platform.showOptimizedSnackbar
import im.vector.app.core.utils.PERMISSIONS_FOR_WRITING_FILES
import im.vector.app.core.utils.checkPermissions
import im.vector.app.core.utils.onPermissionDeniedDialog
import im.vector.app.core.utils.registerForPermissionsResult
import im.vector.app.core.utils.shareMedia
import im.vector.app.features.themes.ActivityOtherThemes
import im.vector.app.features.themes.ThemeUtils
import im.vector.lib.attachmentviewer.AttachmentCommands
import im.vector.lib.attachmentviewer.AttachmentViewerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class VectorAttachmentViewerActivity : AttachmentViewerActivity(), AttachmentInteractionListener {

    @Parcelize
    data class Args(
            val roomId: String?,
            val eventId: String,
            val sharedTransitionName: String?
    ) : Parcelable

    @Inject
    lateinit var sessionHolder: ActiveSessionHolder

    @Inject
    lateinit var dataSourceFactory: AttachmentProviderFactory

    @Inject
    lateinit var imageContentRenderer: ImageContentRenderer

    private val viewModel: VectorAttachmentViewerViewModel by viewModel()
    private val errorFormatter by lazy(LazyThreadSafetyMode.NONE) { singletonEntryPoint().errorFormatter() }
    private var initialIndex = 0
    private var isAnimatingOut = false
    private var currentSourceProvider: BaseAttachmentProvider<*>? = null
    private val downloadActionResultLauncher = registerForPermissionsResult { allGranted, deniedPermanently ->
        if (allGranted) {
            viewModel.pendingAction?.let {
                viewModel.handle(it)
            }
        } else if (deniedPermanently) {
            onPermissionDeniedDialog(R.string.denied_permission_generic)
        }
        viewModel.pendingAction = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate Activity ${javaClass.simpleName}")
        ThemeUtils.setActivityTheme(this, getOtherThemes())

        val args = args() ?: throw IllegalArgumentException("Missing arguments")

        if (savedInstanceState == null && addTransitionListener()) {
            args.sharedTransitionName?.let {
                ViewCompat.setTransitionName(imageTransitionView, it)
                transitionImageContainer.isVisible = true

                
                val mediaData: Parcelable? = intent.getParcelableExtra(EXTRA_IMAGE_DATA)
                if (mediaData is ImageContentRenderer.Data) {
                    
                    pager2.isInvisible = true
                    supportPostponeEnterTransition()
                    imageContentRenderer.renderForSharedElementTransition(mediaData, imageTransitionView) {
                        
                        scheduleStartPostponedTransition(imageTransitionView)
                    }
                } else if (mediaData is VideoContentRenderer.Data) {
                    
                    pager2.isInvisible = true
                    supportPostponeEnterTransition()
                    imageContentRenderer.renderForSharedElementTransition(mediaData.thumbnailMediaData, imageTransitionView) {
                        
                        scheduleStartPostponedTransition(imageTransitionView)
                    }
                }
            }
        }

        val session = sessionHolder.getSafeActiveSession() ?: return Unit.also { finish() }

        val room = args.roomId?.let { session.getRoom(it) }

        val inMemoryData = intent.getParcelableArrayListExtra<AttachmentData>(EXTRA_IN_MEMORY_DATA)
        val sourceProvider = if (inMemoryData != null) {
            initialIndex = inMemoryData.indexOfFirst { it.eventId == args.eventId }.coerceAtLeast(0)
            dataSourceFactory.createProvider(inMemoryData, room, lifecycleScope)
        } else {
            val events = room?.getAttachmentMessages().orEmpty()
            initialIndex = events.indexOfFirst { it.eventId == args.eventId }.coerceAtLeast(0)
            dataSourceFactory.createProvider(events, lifecycleScope)
        }
        sourceProvider.interactionListener = this
        setSourceProvider(sourceProvider)
        currentSourceProvider = sourceProvider
        if (savedInstanceState == null) {
            pager2.setCurrentItem(initialIndex, false)
            
            pager2.post {
                onSelectedPositionChanged(initialIndex)
            }
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.black_alpha)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black_alpha)

        observeViewEvents()
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume Activity ${javaClass.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause Activity ${javaClass.simpleName}")
    }

    override fun onBackPressed() {
        if (currentPosition == initialIndex) {
            
            
            transitionImageContainer.isVisible = true
        }
        isAnimatingOut = true
        super.onBackPressed()
    }

    override fun shouldAnimateDismiss(): Boolean {
        return currentPosition != initialIndex
    }

    override fun animateClose() {
        if (currentPosition == initialIndex) {
            
            
            transitionImageContainer.isVisible = true
        }
        isAnimatingOut = true
        ActivityCompat.finishAfterTransition(this)
    }

    private fun getOtherThemes() = ActivityOtherThemes.VectorAttachmentsPreview

    
    private fun addTransitionListener(): Boolean {
        val transition = window.sharedElementEnterTransition

        if (transition != null) {
            
            transition.addListener(
                    onEnd = {
                        
                        
                        if (!isAnimatingOut) {
                            transitionImageContainer.isVisible = false
                            pager2.isInvisible = false
                        }
                    },
                    onCancel = {
                        if (!isAnimatingOut) {
                            transitionImageContainer.isVisible = false
                            pager2.isInvisible = false
                        }
                    }
            )
            return true
        }

        
        return false
    }

    private fun args() = intent.getParcelableExtra<Args>(EXTRA_ARGS)

    private fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
                        supportStartPostponedEnterTransition()
                        return true
                    }
                })
    }

    private fun observeViewEvents() {
        viewModel.viewEvents
                .stream()
                .onEach(::handleViewEvents)
                .launchIn(lifecycleScope)
    }

    private fun handleViewEvents(event: VectorAttachmentViewerViewEvents) {
        when (event) {
            is VectorAttachmentViewerViewEvents.ErrorDownloadingMedia -> showSnackBarError(event.error)
        }
    }

    private fun showSnackBarError(error: Throwable) {
        rootView.showOptimizedSnackbar(errorFormatter.toHumanReadable(error))
    }

    private fun hasWritePermission() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                    checkPermissions(PERMISSIONS_FOR_WRITING_FILES, this, downloadActionResultLauncher)

    override fun onDismiss() {
        animateClose()
    }

    override fun onPlayPause(play: Boolean) {
        handle(if (play) AttachmentCommands.StartVideo else AttachmentCommands.PauseVideo)
    }

    override fun videoSeekTo(percent: Int) {
        handle(AttachmentCommands.SeekTo(percent))
    }

    override fun onShare() {
        lifecycleScope.launch(Dispatchers.IO) {
            val file = currentSourceProvider?.getFileForSharing(currentPosition) ?: return@launch

            withContext(Dispatchers.Main) {
                shareMedia(
                        this@VectorAttachmentViewerActivity,
                        file,
                        getMimeTypeFromUri(this@VectorAttachmentViewerActivity, file.toUri())
                )
            }
        }
    }

    override fun onDownload() {
        lifecycleScope.launch(Dispatchers.IO) {
            val hasWritePermission = withContext(Dispatchers.Main) {
                hasWritePermission()
            }

            val file = currentSourceProvider?.getFileForSharing(currentPosition) ?: return@launch
            if (hasWritePermission) {
                viewModel.handle(VectorAttachmentViewerAction.DownloadMedia(file))
            } else {
                viewModel.pendingAction = VectorAttachmentViewerAction.DownloadMedia(file)
            }
        }
    }

    companion object {
        private const val EXTRA_ARGS = "EXTRA_ARGS"
        private const val EXTRA_IMAGE_DATA = "EXTRA_IMAGE_DATA"
        private const val EXTRA_IN_MEMORY_DATA = "EXTRA_IN_MEMORY_DATA"

        fun newIntent(context: Context,
                      mediaData: AttachmentData,
                      roomId: String?,
                      eventId: String,
                      inMemoryData: List<AttachmentData>,
                      sharedTransitionName: String?) = Intent(context, VectorAttachmentViewerActivity::class.java).also {
            it.putExtra(EXTRA_ARGS, Args(roomId, eventId, sharedTransitionName))
            it.putExtra(EXTRA_IMAGE_DATA, mediaData)
            if (inMemoryData.isNotEmpty()) {
                it.putParcelableArrayListExtra(EXTRA_IN_MEMORY_DATA, ArrayList(inMemoryData))
            }
        }
    }
}
