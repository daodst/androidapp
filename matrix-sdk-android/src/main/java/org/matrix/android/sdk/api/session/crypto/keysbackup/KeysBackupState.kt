

package org.matrix.android.sdk.api.session.crypto.keysbackup


enum class KeysBackupState {
    
    Unknown,

    
    CheckingBackUpOnHomeserver,

    
    WrongBackUpVersion,

    
    Disabled,

    
    
    
    
    NotTrusted,

    
    Enabling,

    
    ReadyToBackUp,

    
    WillBackUp,

    
    BackingUp
}
