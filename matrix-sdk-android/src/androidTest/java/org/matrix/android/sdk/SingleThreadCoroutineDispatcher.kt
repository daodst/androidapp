

package org.matrix.android.sdk

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.asCoroutineDispatcher
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import java.util.concurrent.Executors

internal val testCoroutineDispatchers = MatrixCoroutineDispatchers(Main, Main, Main, Main,
        Executors.newSingleThreadExecutor().asCoroutineDispatcher())
