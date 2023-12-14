

package com.wallet.ctc.ui.me.setting;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.EOS_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.me.DefWalletBean;
import com.wallet.ctc.util.ACache;
import com.wallet.ctc.util.WalletSpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class SettingDefWalletActivity extends BaseActivity {


    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.llq_eth_status)
    TextView llqEthStatus;
    @BindView(R2.id.llq_eth_lin)
    LinearLayout llqEthLin;
    @BindView(R2.id.llq_eth_view)
    View llqEthView;
    @BindView(R2.id.llq_mcc_status)
    TextView llqMccStatus;
    @BindView(R2.id.llq_mcc_lin)
    LinearLayout llqMccLin;
    @BindView(R2.id.llq_mcc_view)
    View llqMccView;
    @BindView(R2.id.llq_dm_status)
    TextView llqDmStatus;
    @BindView(R2.id.llq_dm_lin)
    LinearLayout llqDmLin;
    @BindView(R2.id.llq_dm_view)
    View llqDmView;
    @BindView(R2.id.llq_btc_status)
    TextView llqBtcStatus;
    @BindView(R2.id.llq_btc_lin)
    LinearLayout llqBtcLin;
    @BindView(R2.id.llq_btc_view)
    View llqBtcView;
    @BindView(R2.id.llq_eos_status)
    TextView llqEosStatus;
    @BindView(R2.id.llq_eos_lin)
    LinearLayout llqEosLin;
    @BindView(R2.id.llq_eos_view)
    View llqEosView;
    @BindView(R2.id.llq_eth)
    TextView llqEth;
    @BindView(R2.id.llq_mcc)
    TextView llqMcc;
    @BindView(R2.id.llq_dm)
    TextView llqDm;
    @BindView(R2.id.llq_btc)
    TextView llqBtc;
    @BindView(R2.id.llq_eos)
    TextView llqEos;
    @BindView(R2.id.llq_other)
    TextView llqOther;
    @BindView(R2.id.llq_other_status)
    TextView llqOtherStatus;
    @BindView(R2.id.llq_other_lin)
    LinearLayout llqOtherLin;
    @BindView(R2.id.llq_other_view)
    View llqOtherView;
    @BindView(R2.id.llq_trx)
    TextView llqtrx;
    @BindView(R2.id.llq_trx_status)
    TextView llqtrxStatus;
    @BindView(R2.id.llq_trx_lin)
    LinearLayout llqtrxLin;
    @BindView(R2.id.llq_trx_view)
    View llqtrxView;
    @BindView(R2.id.llq_dmf)
    TextView llqDmf;
    @BindView(R2.id.llq_dmf_status)
    TextView llqDmfStatus;
    @BindView(R2.id.llq_dmf_lin)
    LinearLayout llqDmfLin;
    @BindView(R2.id.llq_dmf_view)
    View llqDmfView;
    @BindView(R2.id.llq_dmf_ba)
    TextView llqDmfBa;
    @BindView(R2.id.llq_dmf_ba_status)
    TextView llqDmfBaStatus;
    @BindView(R2.id.llq_dmf_ba_lin)
    LinearLayout llqDmfBaLin;
    @BindView(R2.id.llq_dmf_ba_view)
    View llqDmfBaView;
    @BindView(R2.id.llq_etf)
    TextView llqEtf;
    @BindView(R2.id.llq_etf_status)
    TextView llqEtfStatus;
    @BindView(R2.id.llq_etf_lin)
    LinearLayout llqEtfLin;
    @BindView(R2.id.llq_etf_view)
    View llqEtfView;
    @BindView(R2.id.llq_huobi)
    TextView llqHuobi;
    @BindView(R2.id.llq_huobi_status)
    TextView llqHuobiStatus;
    @BindView(R2.id.llq_huobi_lin)
    LinearLayout llqHuobiLin;
    @BindView(R2.id.llq_huobi_view)
    View llqHuobiView;
    @BindView(R2.id.llq_bnb)
    TextView llqBnb;
    @BindView(R2.id.llq_bnb_status)
    TextView llqBnbStatus;
    @BindView(R2.id.llq_bnb_lin)
    LinearLayout llqBnbLin;
    @BindView(R2.id.llq_bnb_view)
    View llqBnbView;
    private Intent intent;
    private Gson gson = new Gson();
    private MeApi mApi = new MeApi();
    private ACache mAcache;
    private List<DefWalletBean> list = new ArrayList<>();

    @Override
    public int initContentView() {
        return R.layout.activity_setting_def_wallet;
    }

    @Override
    public void initUiAndListener() {
        tvTitle.setText(getString(R.string.set_default_wallet));
        llqMcc.setText(getString(R.string.default_token_name).toUpperCase() + " " + getString(R.string.wallet));
        llqDm.setText("DM" + " " + getString(R.string.wallet));
        llqEth.setText("ETH" + " " + getString(R.string.wallet));
        llqBtc.setText("BTC" + " " + getString(R.string.wallet));
        llqEos.setText("EOS" + " " + getString(R.string.wallet));
        llqOther.setText(getString(R.string.default_other_token_name).toUpperCase() + " " + getString(R.string.wallet));
        llqtrx.setText("TRX" + " " + getString(R.string.wallet));
        llqEtf.setText(getString(R.string.default_etf).toUpperCase() + " " + getString(R.string.wallet));
        llqDmf.setText(getString(R.string.default_dmf_hb).toUpperCase() + " " + getString(R.string.wallet));
        llqDmfBa.setText(getString(R.string.default_dmf_ba).toUpperCase() + " " + getString(R.string.wallet));
        llqHuobi.setText("HT" + " " + getString(R.string.wallet));
        llqBnb.setText("BNB" + " " + getString(R.string.wallet));
        mAcache = ACache.get(this);
        if (WalletSpUtil.getEnableMcc() == 0) {
            llqMccLin.setVisibility(View.GONE);
            llqMccView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableDm() == 0) {
            llqDmLin.setVisibility(View.GONE);
            llqDmView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableEth() == 0) {
            llqEthLin.setVisibility(View.GONE);
            llqEthView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableBtc() == 0) {
            llqBtcLin.setVisibility(View.GONE);
            llqBtcView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableEos() == 0) {
            llqEosLin.setVisibility(View.GONE);
            llqEosView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableOther() == 0) {
            llqOtherLin.setVisibility(View.GONE);
            llqOtherView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableTrx() == 0) {
            llqtrxLin.setVisibility(View.GONE);
            llqtrxView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableEtf() == 0) {
            llqEtfLin.setVisibility(View.GONE);
            llqEtfView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableDmf() == 0) {
            llqDmfLin.setVisibility(View.GONE);
            llqDmfView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableDmfBa() == 0) {
            llqDmfBaLin.setVisibility(View.GONE);
            llqDmfBaView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableHt() == 0) {
            llqHuobiLin.setVisibility(View.GONE);
            llqHuobiView.setVisibility(View.GONE);
        }
        if (WalletSpUtil.getEnableBnb() == 0) {
            llqBnbLin.setVisibility(View.GONE);
            llqBnbView.setVisibility(View.GONE);
        }
        String data = "";

        if (!TextUtils.isEmpty(data)) {
            list = gson.fromJson(data, new TypeToken<List<DefWalletBean>>() {
            }.getType());
        }

    }

    @Override
    public void initData() {

        for (int i = 0; i < list.size(); i++) {
            String status = getString(R.string.yishezhi);
            int color = 0xff999999;
            if (null == getWallet(list.get(i).getTureType(), list.get(i).getAddress())) {
                status = getString(R.string.weidaoru);
                color = 0xffE85252;
            }
            if (list.get(i).getTureType() == DM_COIN) {
                llqDmStatus.setText(status);
                llqDmStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == ETH_COIN) {
                llqEthStatus.setText(status);
                llqEthStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == BTC_COIN) {
                llqBtcStatus.setText(status);
                llqBtcStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == EOS_COIN) {
                llqEosStatus.setText(status);
                llqEosStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == MCC_COIN) {
                llqMccStatus.setText(status);
                llqMccStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == OTHER_COIN) {
                llqOtherStatus.setText(status);
                llqOtherStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == ETF_COIN) {
                llqEtfStatus.setText(status);
                llqEtfStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == DMF_COIN) {
                llqDmfStatus.setText(status);
                llqDmfStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == DMF_BA_COIN) {
                llqDmfBaStatus.setText(status);
                llqDmfBaStatus.setTextColor(color);
            } else if (list.get(i).getTureType() == HT_COIN) {
                llqHuobiStatus.setText(status);
                llqHuobiStatus.setTextColor(color);
            }else if (list.get(i).getTureType() == BNB_COIN) {
                llqBnbStatus.setText(status);
                llqBnbStatus.setTextColor(color);
            }else if (list.get(i).getTureType() == TRX_COIN) {
                llqtrxStatus.setText(status);
                llqtrxStatus.setTextColor(color);
            }
        }
    }

    @OnClick({R2.id.tv_back,R2.id.llq_trx_lin, R2.id.llq_mcc_lin,R2.id.llq_bnb_lin, R2.id.llq_huobi_lin, R2.id.llq_dm_lin, R2.id.llq_etf_lin, R2.id.llq_dmf_lin, R2.id.llq_dmf_ba_lin, R2.id.llq_other_lin, R2.id.llq_eth_lin, R2.id.llq_btc_lin, R2.id.llq_eos_lin, R2.id.tv_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();
            return;
        }

        if (i == R.id.llq_mcc_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", MCC_COIN);
            intent.putExtra("address", getDefAddress(MCC_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_dm_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", DM_COIN);
            intent.putExtra("address", getDefAddress(DM_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_eth_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", ETH_COIN);
            intent.putExtra("address", getDefAddress(ETH_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_btc_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", BTC_COIN);
            intent.putExtra("address", getDefAddress(BTC_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_eos_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", EOS_COIN);
            intent.putExtra("address", getDefAddress(EOS_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_other_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", OTHER_COIN);
            intent.putExtra("address", getDefAddress(OTHER_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_etf_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", ETF_COIN);
            intent.putExtra("address", getDefAddress(ETF_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_dmf_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", DMF_COIN);
            intent.putExtra("address", getDefAddress(DMF_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_dmf_ba_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", DMF_BA_COIN);
            intent.putExtra("address", getDefAddress(DMF_BA_COIN));
            startActivityForResult(intent, 1000);
        } else if (i == R.id.llq_huobi_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", HT_COIN);
            intent.putExtra("address", getDefAddress(HT_COIN));
            startActivityForResult(intent, 1000);
        }else if (i == R.id.llq_bnb_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", BNB_COIN);
            intent.putExtra("address", getDefAddress(BNB_COIN));
            startActivityForResult(intent, 1000);
        }else if (i == R.id.llq_trx_lin) {
            intent = new Intent(this, SettingDefWalletTypeActivity.class);
            intent.putExtra("type", TRX_COIN);
            intent.putExtra("address", getDefAddress(TRX_COIN));
            startActivityForResult(intent, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int type = data.getIntExtra("type", 0);
            String address = data.getStringExtra("address");
            changeDef(address, type);
        }
    }

    private void changeDef(String address, int type) {
        String name = "dm";
        if (type == DM_COIN) {
            name = "dm";
        } else if (type == ETH_COIN) {
            name = "eth";
        } else if (type == BTC_COIN) {
            name = "btc";
        } else if (type == EOS_COIN) {
            name = "eos";
        } else if (type == MCC_COIN) {
            name = getString(R.string.default_token_name).toLowerCase();
        } else if (type == OTHER_COIN) {
            name = getString(R.string.default_other_token_name).toLowerCase();
        } else if (type == ETF_COIN) {
            name = getString(R.string.default_etf).toLowerCase();
        } else if (type == DMF_COIN) {
            name = getString(R.string.default_dmf_hb).toLowerCase();
        } else if (type == DMF_BA_COIN) {
            name = getString(R.string.default_dmf_ba).toLowerCase();
        } else if (type == HT_COIN) {
            name = "ht";
        }else if (type == BNB_COIN) {
            name = "bnb";
        }else if (type == TRX_COIN) {
            name = "trx";
        }

        Map<String, Object> params2 = new TreeMap();
        params2.put("address", address);
        params2.put("type", name);
        mLoadingDialog.show();
        mApi.setAddress(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            setDefAddress(type, address);
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    private WalletEntity getWallet(int type, String address) {
        WalletEntity WalletEntity = null;
        WalletEntity = WalletDBUtil.getInstent(this).getWalletInfoByAddress(address, type);
        return WalletEntity;
    }

    private String getDefAddress(int type) {
        String address = "";
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTureType() == type) {
                return list.get(i).getAddress();
            }
        }
        return address;
    }

    private void setDefAddress(int type, String address) {
        String def = getDefAddress(type);
        if (TextUtils.isEmpty(def)) {
            list.add(new DefWalletBean(type, address));
            initData();
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTureType() == type) {
                    list.get(i).setAddress(address);
                }
            }
        }

    }
}
