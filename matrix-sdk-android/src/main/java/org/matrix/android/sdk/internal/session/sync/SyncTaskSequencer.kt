

package org.matrix.android.sdk.internal.session.sync

import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.task.SemaphoreCoroutineSequencer
import javax.inject.Inject

@SessionScope
internal class SyncTaskSequencer @Inject constructor() : SemaphoreCoroutineSequencer()
