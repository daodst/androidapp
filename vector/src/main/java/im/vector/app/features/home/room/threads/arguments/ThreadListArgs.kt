

package im.vector.app.features.home.room.threads.arguments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel

@Parcelize
data class ThreadListArgs(
        val roomId: String,
        val displayName: String?,
        val avatarUrl: String?,
        val roomEncryptionTrustLevel: RoomEncryptionTrustLevel?
) : Parcelable
