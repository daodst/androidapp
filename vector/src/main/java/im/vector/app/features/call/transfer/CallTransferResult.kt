

package im.vector.app.features.call.transfer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class CallTransferResult : Parcelable {
    @Parcelize data class ConnectWithUserId(val consultFirst: Boolean, val selectedUserId: String) : CallTransferResult()
    @Parcelize data class ConnectWithPhoneNumber(val consultFirst: Boolean, val phoneNumber: String) : CallTransferResult()
}
