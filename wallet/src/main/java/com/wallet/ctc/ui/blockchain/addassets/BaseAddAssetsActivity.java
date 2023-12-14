

package com.wallet.ctc.ui.blockchain.addassets;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockViewModel;
import com.wallet.ctc.api.blockchain.TrxApi;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.EthAssertBean;
import com.wallet.ctc.model.blockchain.NewAssertBean;
import com.wallet.ctc.model.blockchain.TrxSeachBean;
import com.wallet.ctc.model.blockchain.XrpAssertBean;
import com.wallet.ctc.ui.blockchain.seach.SeachActivity;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.viewpager.ViewPageAdpater;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.BaseActivity;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class BaseAddAssetsActivity extends BaseActivity<BlockViewModel> {

    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.main_viewpager)
    ViewPager mViewPager;
    @BindView(R2.id.seach_edit)
    EditText seachEdit;
    @BindView(R2.id.seact_btn)
    TextView seactBtn;
    private ArrayList<BaseFragment> fragmentList = new ArrayList<>();
    private ViewPageAdpater madapter;
    private int type= WalletUtil.ETH_COIN;
    private AddAssetsFragment coinF;
    private Gson gson = new Gson();
    private TrxApi mApi = new TrxApi();

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_base_addassert;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        ButterKnife.bind(this);
        type= SettingPrefUtil.getWalletType(this);
        coinF= new AddAssetsFragment();
        coinF.setType(type);
        fragmentList.add(coinF);
        madapter = new ViewPageAdpater(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(madapter);
        if(type== WalletUtil.MCC_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,getString(R.string.default_token_name2)));
        }else if(type== WalletUtil.DM_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"DM"));
        }else if(type== WalletUtil.ETH_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"ETH"));
        }else if(type== WalletUtil.OTHER_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,getString(R.string.default_other_token_name)));
        }else if(type== WalletUtil.XRP_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"XRP"));
        }else if(type== WalletUtil.TRX_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"TRX"));
        }else if(type== WalletUtil.ETF_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,getString(R.string.default_etf)));
        }else if(type== WalletUtil.DMF_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,getString(R.string.default_dmf_hb)));
        }else if(type== WalletUtil.DMF_BA_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,getString(R.string.default_dmf_ba)));
        }else if(type== WalletUtil.HT_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"HT"));
        }else if(type== WalletUtil.BNB_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"BNB"));
        }else if(type== WalletUtil.EOS_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"EOS"));
        }else if(type== WalletUtil.FIL_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"FIL"));
        }else if(type== WalletUtil.DOGE_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"DOGE"));
        }else if(type== WalletUtil.DOT_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"DOT"));
        }else if(type== WalletUtil.LTC_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"LTC"));
        }else if(type== WalletUtil.BCH_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"BCH"));
        }else if(type== WalletUtil.ZEC_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"ZEC"));
        }else if(type== WalletUtil.ADA_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"ADA"));
        }else if(type== WalletUtil.ETC_COIN) {
            seachEdit.setHint(getString(R.string.seach_assert_hint,"ETC"));
        }
        seactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = seachEdit.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    return;
                }
                if(type== WalletUtil.TRX_COIN){
                    seachTrx(key);
                }else {
                    viewModel.seachAssets(type, key);
                }
            }
        });
        getViewModel().observe(getViewModel().seachAssets, new Observer<List<NewAssertBean>>() {
            @Override
            public void onChanged(@Nullable List<NewAssertBean> data) {
                if(null==data){
                    coinF.setSeachList(new ArrayList(),type);
                    return;
                }
                coinF.setSeachList(data,type);
            }
        });
        getViewModel().observe(getViewModel().seachEthAssets, new Observer<List<EthAssertBean>>() {
            @Override
            public void onChanged(@Nullable List<EthAssertBean> data) {
                if(null==data){
                    coinF.setSeachEthList(new ArrayList(),1);
                    return;
                }
                coinF.setSeachEthList(data,1);
            }
        });

        getViewModel().observe(getViewModel().showToastLD, str->{
            ToastUtil.showToast(str);
        });
        getViewModel().observe(getViewModel().seachXrpAssets, new Observer<List<XrpAssertBean>>() {
            @Override
            public void onChanged(@Nullable List<XrpAssertBean> data) {
                if(null==data){
                    coinF.setSeachXrpList(new ArrayList(),type);
                    return;
                }
                coinF.setSeachXrpList(data,1);
            }
        });
    }

    private void seachTrx(String key){
        Map<String, Object> params = new TreeMap();
        params.put("term", key);
        mApi.seachCoin(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<TrxSeachBean>>(this) {
                    @Override
                    public void onNexts(List<TrxSeachBean> baseEntity) {
                        if(baseEntity.size()>0){
                            List<TrxSeachBean> list=new ArrayList<>();
                            
                            for(int i=0;i<baseEntity.size();i++){
                                if(baseEntity.get(i).getDesc().equals("Token-TRC20")){
                                    list.add(baseEntity.get(i));
                                }
                            }
                            coinF.setSeachTrxList(list);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }



    @Override
    public void initData() {
        mViewPager.setOffscreenPageLimit(fragmentList.size());
    }

    @OnClick({R2.id.tv_back, R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
            Intent intent = new Intent(this, SeachActivity.class);
            startActivity(intent);

        } else {
        }
    }
}
