

package org.matrix.android.sdk.internal.crypto.store.db

import io.realm.annotations.RealmModule
import org.matrix.android.sdk.internal.crypto.store.db.model.CrossSigningInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoMetadataEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoRoomEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.DeviceInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.GossipingEventEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.IncomingGossipingRequestEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.KeyInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.KeysBackupDataEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.MyDeviceLastSeenInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.OlmInboundGroupSessionEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.OlmSessionEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.OutboundGroupSessionInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.OutgoingGossipingRequestEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.SharedSessionEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.TrustLevelEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.UserEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.WithHeldSessionEntity


@RealmModule(library = true,
        classes = [
            CryptoMetadataEntity::class,
            CryptoRoomEntity::class,
            DeviceInfoEntity::class,
            KeysBackupDataEntity::class,
            OlmInboundGroupSessionEntity::class,
            OlmSessionEntity::class,
            UserEntity::class,
            KeyInfoEntity::class,
            CrossSigningInfoEntity::class,
            TrustLevelEntity::class,
            GossipingEventEntity::class,
            IncomingGossipingRequestEntity::class,
            OutgoingGossipingRequestEntity::class,
            MyDeviceLastSeenInfoEntity::class,
            WithHeldSessionEntity::class,
            SharedSessionEntity::class,
            OutboundGroupSessionInfoEntity::class
        ])
internal class RealmCryptoStoreModule
