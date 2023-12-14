

package org.matrix.android.sdk.internal.crypto.keysbackup

import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupCreationInfo

data class PrepareKeysBackupDataResult(val megolmBackupCreationInfo: MegolmBackupCreationInfo,
                                       val version: String)
