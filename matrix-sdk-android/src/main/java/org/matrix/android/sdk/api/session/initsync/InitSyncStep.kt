

package org.matrix.android.sdk.api.session.initsync

enum class InitSyncStep {
    ServerComputing,
    Downloading,
    ImportingAccount,
    ImportingAccountCrypto,
    ImportingAccountRoom,
    ImportingAccountGroups,
    ImportingAccountData,
    ImportingAccountJoinedRooms,
    ImportingAccountInvitedRooms,
    ImportingAccountLeftRooms
}
