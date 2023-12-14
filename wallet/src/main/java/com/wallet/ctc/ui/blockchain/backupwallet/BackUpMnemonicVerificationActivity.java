

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.databinding.ItemMnemonicLayoutBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.BackUpMnemonicBean;
import com.wallet.ctc.ui.blockchain.mywallet.MyWalletActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.AppApplication;
import common.app.mall.util.ToastUtil;



public class BackUpMnemonicVerificationActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;

    @BindView(R2.id.yanzhengRv)
    RecyclerView jiaoyanRv;
    @BindView(R2.id.zhujiciRv)
    RecyclerView zhujiciRv;


    private List<BackUpMnemonicBean> list = new ArrayList<>();
    private List<String> click = new ArrayList<>();
    
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private String data;
    private WalletEntity mWallet;
    private int from;
    private List<WalletEntity> mWallName;

    MnemonicItemAdapter jiaoYanAdapter, zhujiciAdapter;

    @Override
    public int initContentView() {
        data = getIntent().getStringExtra("data");
        from = getIntent().getIntExtra("from", 0);
        mWallet = (WalletEntity) getIntent().getParcelableExtra("wallet");
        mWallName = walletDBUtil.getWallName();

        if (TextUtils.isEmpty(data)) {
            ToastUtil.showToast(getString(R.string.sys_error));
            finish();
            return 0;
        }

        list.clear();
        List<String> mnemonic = (gson.fromJson(data, new TypeToken<List<String>>() {
        }.getType()));
        for(int i=0;i<mnemonic.size();i++){
            list.add(new BackUpMnemonicBean(mnemonic.get(i)));
        }
        
        Collections.shuffle(list);
        if (list.size() == 0) {
            ToastUtil.showToast(getString(R.string.sys_error));
            finish();
            return 0;
        }
        return R.layout.activity_backup_mnemonic_ver;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.backup_mnemonic));
        jiaoYanAdapter = new MnemonicItemAdapter(list.size()) {
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
                if (data == null) {
                    return;
                }
                String text = data.getText();
                jiaoYanAdapter.removeItem(position);
                zhujiciAdapter.setUnChoosedAndShuffle(text);
            }
        };
        jiaoyanRv.setLayoutManager(new GridLayoutManager(this, 4));

        DividerItemDecoration verDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        verDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.rv_item_vertical_divider));
        DividerItemDecoration horDivider = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        horDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.rv_item_hor_divider));
        jiaoyanRv.addItemDecoration(verDivider);
        jiaoyanRv.addItemDecoration(horDivider);
        jiaoyanRv.setAdapter(jiaoYanAdapter);


        zhujiciAdapter = new MnemonicItemAdapter(list.size()) {
            @Override
            public void onBindView(ItemMnemonicLayoutBinding view, BackUpMnemonicBean data, int position) {
                view.indexTv.setVisibility(View.GONE);
                if (data != null) {
                    view.nameTv.setText(data.getText());
                    if (data.isChoose()) {
                        view.nameTv.setTextColor(Color.parseColor("#CCCCCC"));
                        view.itemLayout.setBackgroundResource(R.drawable.round_eee_c6);
                    } else {
                        view.nameTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.default_text_two_color));
                        view.itemLayout.setBackgroundResource(R.drawable.round_999_c6);
                    }
                } else {
                    view.nameTv.setText("");
                    view.itemLayout.setBackgroundResource(R.drawable.round_eee_c6);
                }
            }

            @Override
            public void onItemClick(ItemMnemonicLayoutBinding view, BackUpMnemonicBean data, int position) {
                if (null != data && !data.isChoose()) {
                    jiaoYanAdapter.addItem(new BackUpMnemonicBean(data.getText()));
                    zhujiciAdapter.changeChooseAndShuffle(true, position);
                }
            }
        };
        zhujiciAdapter.bindDatas(list);
        zhujiciRv.setLayoutManager(new GridLayoutManager(this, 4));
        zhujiciRv.addItemDecoration(verDivider);
        zhujiciRv.addItemDecoration(horDivider);
        zhujiciRv.setAdapter(zhujiciAdapter);
    }


    @Override
    public void initData() {
    }

    @OnClick({R2.id.tv_back, R2.id.submit})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            setResult(RESULT_OK, getIntent());
            finish();

        } else if (i == R.id.submit) {
            click.clear();
            click.addAll(jiaoYanAdapter.getResult());
            if (click.size() != list.size()) {
                return;
            }
            if (!gson.toJson(click).equals(data)) {
                ToastUtil.showToast(getString(R.string.mnemonic_error));
                return;
            }
            showNormalDialog();

        } else {
        }
    }

    private void showNormalDialog() {
        ToastUtil.showToast(getString(R.string.caozuo_success));
        next(1);
    }

    private void next(int type) {
        
        mWallet.setmBackup(type);
        mWallet.setMMnemonicBackup(type);
        walletDBUtil.updateWalletInfoByAddress(mWallet);
        if (mWallet.getLevel() == 1) {
            for (int i = 0; i < mWallName.size(); i++) {
                if (mWallName.get(i).getLevel() == 1) {
                    mWallName.get(i).setmBackup(type);
                    mWallName.get(i).setMMnemonicBackup(1);
                    walletDBUtil.updateWalletInfoByAddress(mWallName.get(i));
                }
            }
        }

        setResult(RESULT_OK, getIntent());
        if (from == 2) {
            AppApplication.finishActivity(BackUpMnemonicActivity.class);
        } else if (from == 0) {
            AppApplication.finishActivity(MyWalletActivity.class);
            finish();
        } else if (from == 1) {
            finish();
        }
    }
}
