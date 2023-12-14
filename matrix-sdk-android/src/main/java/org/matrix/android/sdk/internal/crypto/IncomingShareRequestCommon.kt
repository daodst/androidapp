

package org.matrix.android.sdk.internal.crypto

internal interface IncomingShareRequestCommon {
    
    val userId: String?

    
    val deviceId: String?

    
    val requestId: String?

    val localCreationTimestamp: Long?
}
