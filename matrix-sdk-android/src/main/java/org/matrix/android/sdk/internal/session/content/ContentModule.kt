

package org.matrix.android.sdk.internal.session.content

import dagger.Binds
import dagger.Module
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.file.ContentDownloadStateTracker
import org.matrix.android.sdk.internal.session.download.DefaultContentDownloadStateTracker

@Module
internal abstract class ContentModule {

    @Binds
    abstract fun bindContentUploadStateTracker(tracker: DefaultContentUploadStateTracker): ContentUploadStateTracker

    @Binds
    abstract fun bindContentDownloadStateTracker(tracker: DefaultContentDownloadStateTracker): ContentDownloadStateTracker

    @Binds
    abstract fun bindContentUrlResolver(resolver: DefaultContentUrlResolver): ContentUrlResolver
}
