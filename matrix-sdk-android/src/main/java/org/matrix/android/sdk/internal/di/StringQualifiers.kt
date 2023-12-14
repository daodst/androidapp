

package org.matrix.android.sdk.internal.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class UserId


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DeviceId


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class UserMd5


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SessionId
