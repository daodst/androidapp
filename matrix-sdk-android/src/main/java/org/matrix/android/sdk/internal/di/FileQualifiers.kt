

package org.matrix.android.sdk.internal.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SessionFilesDirectory

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SessionDownloadsDirectory

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CacheDirectory

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ExternalFilesDirectory
