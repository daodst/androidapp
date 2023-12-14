

package im.vector.app.features.home.room.detail.timeline.url

import org.matrix.android.sdk.api.session.media.PreviewUrlData


sealed class PreviewUrlUiState {
    
    object Unknown : PreviewUrlUiState()

    
    object NoUrl : PreviewUrlUiState()

    
    object Loading : PreviewUrlUiState()

    
    data class Error(val throwable: Throwable) : PreviewUrlUiState()

    
    data class Data(val eventId: String,
                    val url: String,
                    val previewUrlData: PreviewUrlData) : PreviewUrlUiState()
}
