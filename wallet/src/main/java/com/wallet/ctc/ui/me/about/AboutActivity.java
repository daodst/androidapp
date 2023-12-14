

package com.wallet.ctc.ui.me.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.util.NetUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.utils.AppVerUtil;



public class AboutActivity extends BaseActivity {

    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.div_view)
    View divView;
    @BindView(R2.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R2.id.current_version2)
    TextView currentVersion;
    @BindView(R2.id.use_agreement2)
    TextView useAgreement;
    @BindView(R2.id.privacy_policy2)
    TextView privacyPolicy;
    @BindView(R2.id.version_log2)
    TextView versionLog;
    @BindView(R2.id.product_guide2)
    TextView productGuide;
    @BindView(R2.id.new_version2)
    TextView newVersion;
    @BindView(R2.id.menu_body)
    ImageView menuBody;
    private MeApi mApi = new MeApi();
    private Gson gson = new GsonBuilder()
            
            
            
            
            .disableHtmlEscaping() 
            .create();
    private int n;
    private long clickTime;

    @Override
    public int initContentView() {
        return R.layout.activity_about_wallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.about));
        menuBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (System.currentTimeMillis() - clickTime < 1000) {
                    n += 1;
                    if (n >= 9) {
                       try{
                           Class clazz = Class.forName("com.app.ApiSettingActivity");
                           Intent intent = new Intent(AboutActivity.this, clazz);
                           startActivity(intent);
                       }catch (Exception e){

                       }
                    }
                } else {
                    n = 0;
                }
                clickTime = System.currentTimeMillis();
            }
        });
    }

    @Override
    public void initData() {
        String banben = getPackageInfo().versionName;
        currentVersion.setText(getString(R.string.current_version) + banben);
    }

    @OnClick({R2.id.tv_back, R2.id.use_agreement2, R2.id.privacy_policy2, R2.id.version_log2, R2.id.product_guide2, R2.id.new_version2})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.use_agreement2) {
            getUrl("use_agreement", getString(R.string.use_agreement));
        } else if (i == R.id.privacy_policy2) {
            getUrl("privacy_policy", getString(R.string.privacy_policy));

        } else if (i == R.id.version_log2) {
            getUrl("version_log", getString(R.string.version_log));

        } else if (i == R.id.product_guide2) {
        } else if (i == R.id.new_version2) {
            if (!NetUtils.isNetworkConnected(this)) {
                ToastUtil.showToast(getString(R.string.connect_failuer_toast));
                return;
            } else {
                new AppVerUtil(this).checkVer(null);
            }

        } else {
        }
    }

    private void getUrl(String type, String title) {
        Intent intent = new Intent(AboutActivity.this, BaseWebViewActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("sysName", type);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    
    public PackageInfo getPackageInfo() {
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo;
    }

}
