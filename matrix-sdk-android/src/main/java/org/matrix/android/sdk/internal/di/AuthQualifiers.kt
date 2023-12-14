

package org.matrix.android.sdk.internal.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Authenticated

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class AuthenticatedIdentity

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Unauthenticated

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class UnauthenticatedWithCertificate

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class UnauthenticatedWithCertificateWithProgress
