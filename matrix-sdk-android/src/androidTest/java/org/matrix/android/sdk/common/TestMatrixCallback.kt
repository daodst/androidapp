

package org.matrix.android.sdk.common

import androidx.annotation.CallSuper
import org.junit.Assert.fail
import org.matrix.android.sdk.api.MatrixCallback
import timber.log.Timber
import java.util.concurrent.CountDownLatch


open class TestMatrixCallback<T>(private val countDownLatch: CountDownLatch,
                                 private val onlySuccessful: Boolean = true) : MatrixCallback<T> {

    @CallSuper
    override fun onSuccess(data: T) {
        countDownLatch.countDown()
    }

    @CallSuper
    override fun onFailure(failure: Throwable) {
        Timber.e(failure, "TestApiCallback")

        if (onlySuccessful) {
            fail("onFailure " + failure.localizedMessage)
        }

        countDownLatch.countDown()
    }
}
