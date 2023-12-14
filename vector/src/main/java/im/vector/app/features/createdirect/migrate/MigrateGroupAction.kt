package im.vector.app.features.createdirect.migrate

import android.app.Activity
import android.net.Uri
import im.vector.app.core.platform.VectorViewModelAction

sealed class MigrateGroupAction : VectorViewModelAction {


    data class SetAvatar(val imageUri: Uri?) : MigrateGroupAction()


    data class Create(val activity: Activity, val name: String,
                      val topic: String,
                     ) : MigrateGroupAction()
}
