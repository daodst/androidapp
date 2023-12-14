

package org.matrix.android.sdk.internal.session.identity.db

import io.realm.annotations.RealmModule


@RealmModule(library = true,
        classes = [
            IdentityDataEntity::class,
            IdentityPendingBindingEntity::class
        ])
internal class IdentityRealmModule
