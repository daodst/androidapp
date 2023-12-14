

package im.vector.app.features.media

interface AttachmentInteractionListener {
    fun onDismiss()
    fun onShare()
    fun onDownload()
    fun onPlayPause(play: Boolean)
    fun videoSeekTo(percent: Int)
}
