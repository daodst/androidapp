

package org.matrix.android.sdk.internal.auth.db

import io.realm.annotations.RealmModule


@RealmModule(library = true,
        classes = [
            SessionParamsEntity::class,
            PendingSessionEntity::class
        ])
internal class AuthRealmModule
