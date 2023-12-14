
package org.matrix.android.sdk.internal.session.room.send

import androidx.work.Data
import androidx.work.InputMerger


internal class NoMerger : InputMerger() {
    override fun merge(inputs: MutableList<Data>): Data {
        return inputs.first()
    }
}
