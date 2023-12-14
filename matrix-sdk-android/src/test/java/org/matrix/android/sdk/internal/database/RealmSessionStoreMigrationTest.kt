

package org.matrix.android.sdk.internal.database

import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class RealmSessionStoreMigrationTest {

    @Test
    fun `when creating multiple migration instances then they are equal`() {
        RealmSessionStoreMigration(normalizer = mockk()) shouldBeEqualTo RealmSessionStoreMigration(normalizer = mockk())
    }
}
