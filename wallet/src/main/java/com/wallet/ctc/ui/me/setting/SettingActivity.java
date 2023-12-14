

package com.wallet.ctc.ui.me.setting;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.utils.LanguageUtil;



public class SettingActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;

    @Override
    public int initContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.setting));
    }

    @Override
    public void initData() {

    }


    @OnClick({R2.id.tv_back, R2.id.change_language, R2.id.currency_unit, R2.id.gesture_password})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.change_language) {
            LanguageUtil.showSettingDialog(this, ()->{
                Intent intent = ActivityRouter.getEmptyContentIntent(this, ActivityRouter.Wallet.A_NewHomeFragment);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });

        } else if (i == R.id.currency_unit) {
        } else if (i == R.id.gesture_password) {
        } else {
        }
    }
}
