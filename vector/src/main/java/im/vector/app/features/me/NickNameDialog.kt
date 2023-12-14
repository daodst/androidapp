package im.vector.app.features.me

import android.app.Activity
import android.content.DialogInterface
import android.view.KeyEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.databinding.DialogEditRemarkBinding

class NickNameDialog(){

    fun showChoice(activity: Activity,
                   remark: String,
                   listener: (String) -> Unit) {
        val views = DialogEditRemarkBinding.inflate(activity.layoutInflater)
        views.etRemark.setText(remark)

        MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.vector_me_name)
                .setView(views.root)
                .setPositiveButton(R.string.action_save) { _, _ ->
                    listener(views.etRemark.text.toString())
                }
                .setNegativeButton(R.string.action_cancel, null)
                .setOnKeyListener(
                        DialogInterface.OnKeyListener
                { dialog, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.cancel()
                        return@OnKeyListener true
                    }
                    false
                })
                .setOnDismissListener {
                    views.root.hideKeyboard()
                }
                .create()
                .show()
    }
}
