

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.databinding.ItemMnemonicLayoutBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.BackUpMnemonicBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.view.dialog.ScreenshotsDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;



public class BackUpMnemonicActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.mnemonicRv)
    RecyclerView mnemonicRv;

    private ScreenshotsDialog mDialog;
    private String pwd;
    private Gson gson=new Gson();
    private String data;
    private int from;
    private WalletEntity mWallet;

    @Override
    public int initContentView() {
        pwd=getIntent().getStringExtra("pwd");
        from=getIntent().getIntExtra("from",0);
        mWallet=(WalletEntity)getIntent().getParcelableExtra("wallet");
        if(TextUtils.isEmpty(pwd)){
            ToastUtil.showToast(getString(R.string.sys_error));
            finish();
            return 0;
        }
        return R.layout.activity_backup_mnemonic;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.backup_mnemonic));
        mDialog = new ScreenshotsDialog(this,getString(R.string.transcription_mnemonic_safety));
        mDialog.show();
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(pwd)) {
            return;
        }
        data= DecriptUtil.Decrypt(mWallet.getmMnemonic(),pwd);
        if (TextUtils.isEmpty(data)) {
            ToastUtil.showToast(getString(R.string.sys_error));
            finish();
            return;
        }
        List<String> list= gson.fromJson(data, new TypeToken<List<String>>() {}.getType());
        if (null == list || list.size() == 0) {
            ToastUtil.showToast(getString(R.string.sys_error));
            finish();
            return;
        }

        List<BackUpMnemonicBean> mnemonics = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            mnemonics.add(new BackUpMnemonicBean(list.get(i)));
        }

        MnemonicItemAdapter jiaoYanAdapter = new MnemonicItemAdapter(list.size()) {
            @Override
            public void onBindView(ItemMnemonicLayoutBinding view, BackUpMnemonicBean data, int position) {
                view.itemLayout.setBackgroundResource(R.drawable.round_f1_c6);
                if (null != data) {
                    view.nameTv.setText(data.getText());
                    view.nameTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.default_theme_color));
                } else {
                    view.nameTv.setText("");
                }
                view.indexTv.setText((position+1)+"");
            }

            @Override
            public void onItemClick(ItemMnemonicLayoutBinding view, BackUpMnemonicBean data, int position) {
            }
        };
        jiaoYanAdapter.bindDatas(mnemonics);
        mnemonicRv.setLayoutManager(new GridLayoutManager(this, 4));

        DividerItemDecoration verDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        verDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.rv_item_vertical_divider));
        DividerItemDecoration horDivider = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        horDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.rv_item_hor_divider));
        mnemonicRv.addItemDecoration(verDivider);
        mnemonicRv.addItemDecoration(horDivider);
        mnemonicRv.setAdapter(jiaoYanAdapter);
    }

    @OnClick({R2.id.tv_back, R2.id.submit})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            setResult(RESULT_OK, getIntent());
            finish();

        } else if (i == R.id.submit) {
            if (TextUtils.isEmpty(data)) {
                return;
            }
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            intent.setClass(this, BackUpMnemonicVerificationActivity.class);
            intent.putExtra("data", data);
            intent.putExtra("from", from);
            intent.putExtra("wallet", mWallet);
            startActivity(intent);
            if (from != 2) {
                finish();
            }

        } else {
        }
    }
}
