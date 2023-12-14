

package com.wallet.ctc.ui.blockchain.managewallet;

import static com.wallet.ctc.crypto.WalletDBUtil.USER_ID;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.WalletSpUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class AddWalletTypeActivity extends BaseActivity {

    protected Unbinder mUnbinder;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.listview)
    ListView listview;
    private AddWalletTypeAdapter mAdapter;
    private List<WalletLogoBean> list = new ArrayList<>();
    private InputPwdDialog mDialog;
    private Intent intent;
    private Gson gson=new Gson();

    @Override
    public int initContentView() {
        return R.layout.activity_add_wallet_type;
    }

    @Override
    public void initUiAndListener() {
        mUnbinder = ButterKnife.bind(this);
        mDialog = new InputPwdDialog(this,getString(R.string.place_edit_password));
        mAdapter = new AddWalletTypeAdapter(this);
        mAdapter.bindData(list);
        listview.setAdapter(mAdapter);
        tvTitle.setText(getString(R.string.select_wallet_type));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).getIsdef() != 1) {
                    int choose = list.get(position).getChoose();
                    list.get(position).setChoose(1 - choose);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        List<Integer> deflist = walletDBUtil.getDefWalletType();
        if (WalletSpUtil.getEnableMcc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.tt_logo, getString(R.string.default_token_name2).toUpperCase() + getString(R.string.wallet), 0, WalletUtil.MCC_COIN, 0));
        }
        if (WalletSpUtil.getEnableDm() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dm_logo, "DM" + " " + getString(R.string.wallet), 0, WalletUtil.DM_COIN, 0));
        }
        if (WalletSpUtil.getEnableEth() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eth_logo, "ETH" + getString(R.string.wallet), 0, WalletUtil.ETH_COIN, 0));
        }
        if (WalletSpUtil.getEnableBtc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.btc_logo, "BTC" + getString(R.string.wallet), 0, WalletUtil.BTC_COIN, 0));
        }
        if (WalletSpUtil.getEnableEos() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eos_logo, "EOS" + getString(R.string.wallet), 0, WalletUtil.EOS_COIN, 0));
        }
        if (WalletSpUtil.getEnableOther() == 1) {
            list.add(new WalletLogoBean(R.mipmap.llq_other, getString(R.string.default_other_token_name).toUpperCase() + getString(R.string.wallet), 0, WalletUtil.OTHER_COIN, 0));
        }
        if (WalletSpUtil.getEnableXrp() == 1) {
            list.add(new WalletLogoBean(R.mipmap.xrp_logo, "XRP" + getString(R.string.wallet), 0, WalletUtil.XRP_COIN, 0));
        }
        if (WalletSpUtil.getEnableTrx() == 1) {
            list.add(new WalletLogoBean(R.mipmap.trx_logo, "TRX" + getString(R.string.wallet), 0, WalletUtil.TRX_COIN, 0));
        }
        if (WalletSpUtil.getEnableEtf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.etf_logo, getString(R.string.default_etf).toUpperCase() + getString(R.string.wallet), 0, WalletUtil.ETF_COIN, 0));
        }
        if (WalletSpUtil.getEnableDmf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.hb_dmf_logo, getString(R.string.default_dmf_hb).toUpperCase() + getString(R.string.wallet), 0, WalletUtil.DMF_COIN, 0));
        }
        if (WalletSpUtil.getEnableDmfBa() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bian_dmf_logo, getString(R.string.default_dmf_ba).toUpperCase() + getString(R.string.wallet), 0, WalletUtil.DMF_BA_COIN, 0));
        }
        if (WalletSpUtil.getEnableHt() == 1) {
            list.add(new WalletLogoBean(R.mipmap.huobi_logo, "HECO" + getString(R.string.wallet), 0, WalletUtil.HT_COIN, 0));
        }
        if (WalletSpUtil.getEnableBnb() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bnb_logo, "BSC" + getString(R.string.wallet), 0, WalletUtil.BNB_COIN, 0));
        }
        if (WalletSpUtil.getEnableFIL() == 1) {
            list.add(new WalletLogoBean(R.mipmap.fil_logo, "FIL" + getString(R.string.wallet), 0, WalletUtil.FIL_COIN, 0));
        }
        if (WalletSpUtil.getEnableDOGE() == 1) {
            list.add(new WalletLogoBean(R.mipmap.doge_logo, "DOGE" + getString(R.string.wallet), 0, WalletUtil.DOGE_COIN, 0));
        }
        if (WalletSpUtil.getEnableDOT() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dot_logo, "DOT" + getString(R.string.wallet), 0, WalletUtil.DOT_COIN, 0));
        }
        if (WalletSpUtil.getEnableLTC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.ltc_logo, "LTC" + getString(R.string.wallet), 0, WalletUtil.LTC_COIN, 0));
        }
        if (WalletSpUtil.getEnableBCH() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bch_logo, "BCH" + getString(R.string.wallet), 0, WalletUtil.BCH_COIN, 0));
        }
        if (WalletSpUtil.getEnableZEC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.zec_logo, "ZEC" + getString(R.string.wallet), 0, WalletUtil.ZEC_COIN, 0));
        }
        if (WalletSpUtil.getEnableADA() == 1) {
            list.add(new WalletLogoBean(R.mipmap.ada_logo, "ADA" + getString(R.string.wallet), 0, WalletUtil.ADA_COIN, 0));
        }
        if (WalletSpUtil.getEnableETC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.etc_logo, "ETC" + getString(R.string.wallet), 0, WalletUtil.ETC_COIN, 0));
        }
        if (WalletSpUtil.getEnableSGB() == 1) {
            list.add(new WalletLogoBean(R.mipmap.sgb_logo, "SGB" + getString(R.string.wallet), 0, WalletUtil.SGB_COIN, 0));
        }
        if (WalletSpUtil.getEnableSOL() == 1) {
            list.add(new WalletLogoBean(R.mipmap.sol_logo, "SOL" + getString(R.string.wallet), 0, WalletUtil.SOL_COIN, 0));
        }
        if (WalletSpUtil.getEnableMATIC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.matic_logo, "POLYGON" + getString(R.string.wallet), 0, WalletUtil.MATIC_COIN, 0));
        }

        for (int i = 0; i < deflist.size(); i++) {
            int choosetype = deflist.get(i);
            for (int num = 0; num < list.size(); num++) {
                if (list.get(num).getWalletType() == choosetype) {
                    list.get(num).setIsdef(1);
                    break;
                }
            }
        }

        
        Comparator<WalletLogoBean> comparator = new Comparator<WalletLogoBean>() {
            public int compare(WalletLogoBean s1, WalletLogoBean s2) {
                return s2.getIsdef() - s1.getIsdef();
            }
        };
        
        Collections.sort(list, comparator);
        List<WalletEntity> walletEntities=walletDBUtil.getWalletByIdentity();
        mAdapter.notifyDataSetChanged();
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog.dismiss();
                if(null==walletEntities||walletEntities.size()<1){
                    return;
                }
                if (!walletEntities.get(0).getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(getString(R.string.password_error2));
                    return;
                }
                List<String> list = gson.fromJson(WalletUtil.getDecryptionKey(walletEntities.get(0).getMMnemonic(), pwd), new TypeToken<List<String>>() {
                }.getType());
                for(int i=0;i<chooseType.size();i++){
                    importWallet(list,pwd,walletEntities.get(0).getmPasswordHint(),walletEntities.get(0).getName(),chooseType.get(i),walletEntities.get(0).getMMnemonicBackup());
                }
                finish();
            }

            @Override
            public void No() {
                mDialog.dismiss();
            }
        });

    }

    @Override
    public void initData() {

    }

    List<Integer> chooseType=new ArrayList<>();
    @OnClick({R2.id.tv_back, R2.id.submit})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_back) {
            finish();
        } else if (id == R.id.submit) {
            getChooseType();
            if(chooseType.size()>0){
                mDialog.show();
            }
        }
    }

    private void getChooseType(){
        chooseType.clear();
        for(int i=0;i<list.size();i++){
            if(list.get(i).getChoose()==1){
                chooseType.add(list.get(i).getWalletType());
            }
        }
    }

    private void importWallet(List<String> list, String pwd, String pwdkey, String names, int type,int backup) {
        WalletEntity mWallet;
        mWallet = WalletUtil.ImportWalletByMnemonic(list, pwd,type);
        if(null==mWallet){
            return;
        }
        mWallet.setmPasswordHint(pwdkey);
        mWallet.setName(names);
        mWallet.setmPassword(DecriptUtil.MD5(pwd));
        mWallet.setmBackup(1);
        mWallet.setMMnemonicBackup(backup);
        mWallet.setType(type);
        if (type == WalletUtil.XRP_COIN) {
            addXrpAddress(mWallet.getAllAddress());
        } else if (type == WalletUtil.BTC_COIN) {
            addBtcAddress(mWallet.getAllAddress());
        } else if (type == WalletUtil.TRX_COIN) {
        } else if (type == WalletUtil.ETH_COIN) {
            addAddress(mWallet.getAllAddress(),type);
        }

        Random rand = new Random();
        int i = rand.nextInt(5);
        mWallet.setLogo(i);
        mWallet.setUserName(USER_ID);
        mWallet.setType(type);
        mWallet.setLevel(1);
        creatOrInsertWallet(type, mWallet.getAllAddress());
        DBManager.getInstance(AddWalletTypeActivity.this).insertWallet(mWallet);

    }
    private MeApi mApi = new MeApi();
    private void addBtcAddress(String address) {
        Map<String, Object> params2 = new TreeMap();
        params2.put("addr", address);
        mApi.addBtcAddress(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseEntity>() {
                    @Override
                    public void accept(BaseEntity baseEntity) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void addXrpAddress(String address) {
        Map<String, Object> params2 = new TreeMap();
        params2.put("addr", address);
        mApi.addXrpAddress(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseEntity>() {
                    @Override
                    public void accept(BaseEntity baseEntity) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void addAddress(String address,int type) {
        Map<String, Object> params2 = new TreeMap();
        params2.put("address", address);
        mApi.addAddress(params2, type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseEntity>() {
                    @Override
                    public void accept(BaseEntity baseEntity) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
