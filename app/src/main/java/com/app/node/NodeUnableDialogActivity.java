

package com.app.node;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;

import common.app.base.BaseActivity;
import common.app.my.view.MyAlertDialog;
import common.app.utils.SpUtil;


public class NodeUnableDialogActivity extends BaseActivity<NodeCheckVM> {


    private boolean isGateWay = false;

    @Override
    public void initView(@Nullable View view) {
        Intent intent = getIntent();
        if (null != intent) {
            isGateWay = intent.getBooleanExtra("isgateway", false);
        }
        String tips = "";
        if (isGateWay) {
            tips = getString(R.string.now_node_unable_alert2);
        } else {
            tips = getString(R.string.now_node_unable_alert);
        }
        MyAlertDialog dialog = new MyAlertDialog(this,tips );
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                startActivity(new Intent(NodeUnableDialogActivity.this, NodeListsActivity.class));
                dialog.dismiss();
                finish();
            }

            @Override
            public void No() {
                dialog.dismiss();
                SpUtil.lastShowNodeAlertTime = System.currentTimeMillis();
                finish();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        dialog.setYesText(getString(R.string.go_switch_node));
        dialog.setNoText(getString(R.string.no_show_alert));
        dialog.show();
    }
}
