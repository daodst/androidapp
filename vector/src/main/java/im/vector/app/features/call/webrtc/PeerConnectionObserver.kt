

package im.vector.app.features.call.webrtc

import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.call.CallState
import org.matrix.android.sdk.api.session.call.MxPeerConnectionState
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import timber.log.Timber

private val loggerTag = LoggerTag("PeerConnectionObserver", LoggerTag.VOIP)

class PeerConnectionObserver(private val webRtcCall: WebRtcCall) : PeerConnection.Observer {

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
        Timber.tag(loggerTag.value).v("StreamObserver onConnectionChange: $newState")
        when (newState) {
            
            PeerConnection.PeerConnectionState.CONNECTED    -> {
                webRtcCall.mxCall.state = CallState.Connected(MxPeerConnectionState.CONNECTED)
            }
            
            PeerConnection.PeerConnectionState.FAILED       -> {
                
                
                webRtcCall.mxCall.state = CallState.Connected(MxPeerConnectionState.FAILED)
            }
            
            PeerConnection.PeerConnectionState.NEW,
                
            PeerConnection.PeerConnectionState.CONNECTING   -> {
                webRtcCall.mxCall.state = CallState.Connected(MxPeerConnectionState.CONNECTING)
            }
            
            PeerConnection.PeerConnectionState.CLOSED       -> {
                webRtcCall.mxCall.state = CallState.Connected(MxPeerConnectionState.CLOSED)
            }
            
            PeerConnection.PeerConnectionState.DISCONNECTED -> {
                webRtcCall.mxCall.state = CallState.Connected(MxPeerConnectionState.DISCONNECTED)
            }
            null                                            -> {
            }
        }
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        Timber.tag(loggerTag.value).v("StreamObserver onIceCandidate: $iceCandidate")
        webRtcCall.onIceCandidate(iceCandidate)
    }

    override fun onDataChannel(dc: DataChannel) {
        Timber.tag(loggerTag.value).v("StreamObserver onDataChannel: ${dc.state()}")
    }

    override fun onIceConnectionReceivingChange(receiving: Boolean) {
        Timber.tag(loggerTag.value).v("StreamObserver onIceConnectionReceivingChange: $receiving")
    }

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {
        Timber.tag(loggerTag.value).v("StreamObserver onIceConnectionChange IceConnectionState:$newState")
        when (newState) {
            
            PeerConnection.IceConnectionState.NEW          -> {
            }
            
            PeerConnection.IceConnectionState.CHECKING     -> {
            }

            
            PeerConnection.IceConnectionState.CONNECTED    -> {
            }
            
            PeerConnection.IceConnectionState.DISCONNECTED -> {
            }
            
            PeerConnection.IceConnectionState.FAILED       -> {
                webRtcCall.onRenegotiationNeeded(restartIce = true)
            }
            
            PeerConnection.IceConnectionState.COMPLETED    -> {
            }
            
            PeerConnection.IceConnectionState.CLOSED       -> {
            }
        }
    }

    override fun onAddStream(stream: MediaStream) {
        Timber.tag(loggerTag.value).v("StreamObserver onAddStream: $stream")
        webRtcCall.onAddStream(stream)
    }

    override fun onRemoveStream(stream: MediaStream) {
        Timber.tag(loggerTag.value).v("StreamObserver onRemoveStream")
        webRtcCall.onRemoveStream()
    }

    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) {
        Timber.tag(loggerTag.value).v("StreamObserver onIceGatheringChange: $newState")
    }

    override fun onSignalingChange(newState: PeerConnection.SignalingState) {
        Timber.tag(loggerTag.value).v("StreamObserver onSignalingChange: $newState")
    }

    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {
        Timber.tag(loggerTag.value).v("StreamObserver onIceCandidatesRemoved: ${candidates.contentToString()}")
    }

    override fun onRenegotiationNeeded() {
        Timber.tag(loggerTag.value).v("StreamObserver onRenegotiationNeeded")
        webRtcCall.onRenegotiationNeeded(restartIce = false)
    }

    
    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Timber.tag(loggerTag.value).v("StreamObserver onAddTrack")
    }
}
