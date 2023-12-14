

package org.matrix.android.sdk.internal.util.system

import android.os.Build
import javax.inject.Inject

internal class DefaultBuildVersionSdkIntProvider @Inject constructor() :
        BuildVersionSdkIntProvider {
    override fun get() = Build.VERSION.SDK_INT
}
