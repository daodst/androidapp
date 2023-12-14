

package org.matrix.android.sdk.internal.session.securestorage

import org.matrix.android.sdk.internal.util.system.BuildVersionSdkIntProvider

class TestBuildVersionSdkIntProvider : BuildVersionSdkIntProvider {
    var value: Int = 0

    override fun get() = value
}
