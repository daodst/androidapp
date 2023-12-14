

package org.matrix.android.sdk.internal.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class AuthDatabase

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class GlobalDatabase

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SessionDatabase

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CryptoDatabase

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class IdentityDatabase

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ContentScannerDatabase
