

package com.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.app.base.activity.WelcomActivity;
import com.wallet.ctc.util.WalletSpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.AppApplication;
import common.app.base.view.TopBackBar;
import common.app.utils.SpUtil;

public class ApiSettingActivity extends AppCompatActivity {
    @BindView(R.id.eth_code)
    EditText ethCode;
    @BindView(R.id.mcc_code)
    EditText mccCode;
    @BindView(R.id.dm_code)
    EditText dmCode;
    @BindView(R.id.appid)
    EditText appid;
    @BindView(R.id.zdyClose)
    RadioButton zdyClose;
    @BindView(R.id.im_ip)
    EditText imIp;
    @BindView(R.id.im_duankou)
    EditText imDuankou;
    @BindView(R.id.im_oss)
    EditText imOss;
    @BindView(R.id.exit)
    Button exit;
    private EditText etHost, etJshost, etWs;
    private Button btSave;
    private RadioButton rbOpen, rbClose;
    private TopBackBar title;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_setting);
        ButterKnife.bind(this);
        title = (TopBackBar) findViewById(R.id.titleBar);
        etHost = (EditText) findViewById(R.id.etHost);
        etJshost = (EditText) findViewById(R.id.etJsHost);
        etWs = (EditText) findViewById(R.id.etWs);
        btSave = (Button) findViewById(R.id.btSave);
        rbOpen = (RadioButton) findViewById(R.id.rbOpen);
        rbClose = (RadioButton) findViewById(R.id.rbClose);
        type = SpUtil.getType();
        rbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 0;
                    setData(0);
                }
            }
        });
        rbClose.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                type = 1;
                setData(1);
            }
        });
        zdyClose.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                type = 2;
                setData(2);
            }
        });
        if (type == 0) {
            rbOpen.setChecked(true);
        } else if (type == 1) {
            rbClose.setChecked(true);
        } else if (type == 2) {
            zdyClose.setChecked(true);
        }
        rbOpen.setChecked(SpUtil.isDebug());
        rbClose.setChecked(!SpUtil.isDebug());
        title.setLeftTv(new TopBackBar.LeftClickListener() {
            @Override
            public void OnClick(View v) {
                finish();
            }
        });
        title.setMiddleTv("", R.color.default_titlebar_title_color);
        exit.setOnClickListener(v -> {
            SpUtil.setDebug(false);
            SpUtil.saveHostApi(BuildConfig.HOST);
            WalletSpUtil.saveEthHostApi(com.wallet.ctc.BuildConfig.ETH_HOST);
            WalletSpUtil.saveDMHostApi(com.wallet.ctc.BuildConfig.HOST_DM_QUOTES);
            WalletSpUtil.saveMCCHostApi(com.wallet.ctc.BuildConfig.HOST_QUOTES);
            SpUtil.setType(0);
            System.exit(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ApiSettingActivity.this, WelcomActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
                    startActivity(intent);
                }
            }, 500);
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpUtil.setDebug(true);
                SpUtil.saveHostApi(etHost.getText().toString().trim());
                WalletSpUtil.saveEthHostApi(ethCode.getText().toString().trim());
                WalletSpUtil.saveDMHostApi(dmCode.getText().toString().trim());
                WalletSpUtil.saveMCCHostApi(mccCode.getText().toString().trim());
                SpUtil.saveAppid(appid.getText().toString().trim());
                SpUtil.setType(type);

                System.exit(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ApiSettingActivity.this, WelcomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
                        startActivity(intent);
                        AppApplication.finishAllActivity();
                    }
                }, 500);
            }
        });
    }

    private void setData(int type) {
        if (type == 1 || type == 0) {
            etHost.setFocusable(false);
            etJshost.setFocusable(false);
            etWs.setFocusable(false);
            ethCode.setFocusable(false);
            mccCode.setFocusable(false);
            dmCode.setFocusable(false);
            appid.setFocusable(false);
            imDuankou.setFocusable(false);
            imIp.setFocusable(false);
            imOss.setFocusable(false);
        } else {
            etHost.setFocusable(true);
            etHost.setFocusableInTouchMode(true);
            etJshost.setFocusable(true);
            etJshost.setFocusableInTouchMode(true);
            etWs.setFocusable(true);
            etWs.setFocusableInTouchMode(true);
            ethCode.setFocusable(true);
            ethCode.setFocusableInTouchMode(true);
            mccCode.setFocusable(true);
            mccCode.setFocusableInTouchMode(true);
            dmCode.setFocusable(true);
            dmCode.setFocusableInTouchMode(true);
            appid.setFocusable(true);
            appid.setFocusableInTouchMode(true);
            imDuankou.setFocusable(true);
            imDuankou.setFocusableInTouchMode(true);
            imIp.setFocusable(true);
            imIp.setFocusableInTouchMode(true);
            imOss.setFocusable(true);
            imOss.setFocusableInTouchMode(true);
        }

        if (type == 0) {
            etHost.setText(BuildConfig.HOST);
            ethCode.setText(com.wallet.ctc.BuildConfig.ETH_HOST);
            mccCode.setText(com.wallet.ctc.BuildConfig.HOST_QUOTES);
            dmCode.setText(com.wallet.ctc.BuildConfig.HOST_DM_QUOTES);
        } else if (type == 2) {
            etHost.setText(SpUtil.getHostApi());
            ethCode.setText(WalletSpUtil.getEthHostApi());
            mccCode.setText(WalletSpUtil.getMCCHostApi());
            dmCode.setText(WalletSpUtil.getDMHostApi());
            appid.setText(SpUtil.getAppid());
        }
    }
}
