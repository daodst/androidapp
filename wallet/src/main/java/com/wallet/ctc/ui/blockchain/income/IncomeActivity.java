

package com.wallet.ctc.ui.blockchain.income;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.wallet.ctc.DMTransaction.DMTransactionEncoder;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.BaseIncomeBean;
import com.wallet.ctc.model.blockchain.IncomeBean;
import com.wallet.ctc.model.blockchain.TxIdBean;
import com.wallet.ctc.ui.blockchain.choosenode.ChooseNodeActivity;
import com.wallet.ctc.ui.blockchain.principal.PrincipalActivity;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.listview.NoScrollListView;

import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class IncomeActivity extends BaseActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.dailinshouyi)
    TextView dailinshouyi;
    @BindView(R2.id.creat_token_yilingqu)
    TextView creatTokenYilingqu;
    @BindView(R2.id.creat_token_pos2)
    TextView creatTokenPos2;
    @BindView(R2.id.shouyi_list)
    NoScrollListView shouyiList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.zhouqi)
    TextView zhouqi;
    @BindView(R2.id.endtime)
    TextView endtime;
    @BindView(R2.id.token_name)
    TextView tokenName;
    @BindView(R2.id.pos_tokenname)
    TextView posTokenname;

    private String token;
    private Intent intent;
    private IncomeAdapter mAdapter;
    private List<IncomeBean> list = new ArrayList<IncomeBean>();
    private InputPwdDialog mDialog;
    private BlockChainApi mApi = new BlockChainApi();
    private Gson gson = new Gson();
    int shouyi = 0;
    private int type;

    @Override
    public int initContentView() {
        token = getIntent().getStringExtra("tokenName");
        type=getIntent().getIntExtra("type",0);
        return R.layout.activity_income;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(R.string.wakuangshouyi);
        tvAction.setText(R.string.creat_token_shouyi);
        tokenName.setText(getString(R.string.creat_token_daishouyi)+"("+token.toUpperCase()+")");
        posTokenname.setText(getString(R.string.creat_token_posjilu)+"("+token.toUpperCase()+")");
        Drawable top = ContextCompat.getDrawable(this,R.mipmap.shouyijilu);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        tvAction.setCompoundDrawables(top, null, null, null);
        tvAction.setVisibility(View.VISIBLE);
        mAdapter = new IncomeAdapter(this);
        mAdapter.bindData(list);
        shouyiList.setAdapter(mAdapter);
        mDialog = new InputPwdDialog(this, getString(R.string.place_edit_password));
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog.dismiss();
                if (!walletDBUtil.getWalletInfo().getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(getString(R.string.password_error2));
                    return;
                }
                getTxnid(pwd);
            }

            @Override
            public void No() {
                mDialog.dismiss();
            }
        });

    }

    @Override
    public void initData() {
        loadHistory();
    }

    @OnClick({R2.id.tv_back, R2.id.tv_action, R2.id.lingqushouyi, R2.id.linqubenjin})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.tv_action) {
            intent = new Intent(this, IncomeListActivity.class);
            intent.putExtra("tokenName", token);
            intent.putExtra("type", type);
            startActivity(intent);

        } else if (i == R.id.linqubenjin) {
            intent = new Intent(this, PrincipalActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);

        } else if (i == R.id.lingqushouyi) {
            if (shouyi == 0) {
                ToastUtil.showToast("");
                return;
            }
            if (SettingPrefUtil.getNodeType(this) == 1) {
                mDialog.show();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setNegativeButton("", null).setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent(IncomeActivity.this, ChooseNodeActivity.class);
                                intent.putExtra("type", 1);
                                startActivity(intent);
                            }
                        }).setMessage("，，。，。").create();
                dialog.show();
            }
        }
    }

    private void loadHistory() {
        mLoadingDialog.show();
        Map<String, Object> params = new TreeMap();
        params.put("token", token);
        params.put("acc", (walletDBUtil.getWalletInfo().getAllAddress()).toLowerCase());
        mApi.getAwardList(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            String datas = gson.toJson(baseEntity.getData());
                            if (TextUtils.isEmpty(datas) || datas.length() < 4 || datas.equals("null")) {
                                return;
                            }
                            BaseIncomeBean baseIncomeBean=gson.fromJson(datas,BaseIncomeBean.class);
                            dailinshouyi.setText(baseIncomeBean.getLeft_earnings().toPlainString());
                            creatTokenYilingqu.setText(baseIncomeBean.getDraw_earnings().toPlainString());
                            creatTokenPos2.setText(baseIncomeBean.getMineral() + "%");
                            double d = baseIncomeBean.getLeft_earnings().doubleValue();
                            if (d > 0) {
                                shouyi = 1;
                            }
                            list=baseIncomeBean.getAwardlist();
                            if(list==null||list.size()<1){
                                nodata.setVisibility(View.VISIBLE);
                                shouyiList.setVisibility(View.GONE);
                            }else {
                                nodata.setVisibility(View.GONE);
                                shouyiList.setVisibility(View.VISIBLE);
                            }
                            mAdapter.bindData(list);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoadingDialog.dismiss();
                    }
                });
    }

    private void loadDetail() {
        Map<String, Object> params = new TreeMap();
        params.put("token", token);
        params.put("acc", walletDBUtil.getWalletInfo().getAllAddress());
        mApi.getDiyaDetail(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            String datas = gson.toJson(baseEntity.getData());
                            if (TextUtils.isEmpty(datas) || datas.length() < 4 || datas.equals("null")) {
                                dailinshouyi.setText("0.0");
                                creatTokenYilingqu.setText("0.0");
                                creatTokenPos2.setText(0 + "%");
                                nodata.setVisibility(View.VISIBLE);
                                shouyiList.setVisibility(View.GONE);
                                mLoadingDialog.dismiss();
                                return;
                            }
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    
    private void getMinner(Map<String, Object> params) {
        mApi.getAward(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            ToastUtil.showToast(getString(R.string.caozuo_success));
                            initData();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    private void getTxnid(String pwd) {
        mLoadingDialog.show();
        Map<String, Object> params = new TreeMap<>();
        params.put("cnt", "1");
        mLoadingDialog.show();
        mApi.creatTxid(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            TxIdBean txIdBean = gson.fromJson(gson.toJson(baseEntity.getData()), TxIdBean.class);
                            String sign = sign(pwd, txIdBean);
                            Map<String, Object> params2 = new TreeMap();
                            params2.put("sign", sign);
                            params2.put("txid", txIdBean.getTxId());
                            params2.put("nonce", txIdBean.getNonce());
                            params2.put("cc", token);
                            params2.put("acc", walletDBUtil.getWalletInfo().getAllAddress().toLowerCase());
                            getMinner(params2);
                        } else {
                            mLoadingDialog.dismiss();
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoadingDialog.dismiss();
                        LogUtil.d(e.toString());
                    }
                });
    }


    private String sign(String pwd, TxIdBean txIdBean) {
        try {
            
            String myinfo = (txIdBean.getTxId() + token + walletDBUtil.getWalletInfo().getAllAddress()).toLowerCase();
            ECKeyPair pair = ECKeyPair.create(new BigInteger(WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfo().getmPrivateKey(), pwd), 16));
            LogUtil.d(pair.getPrivateKey().toString(16));
            byte[] s = Numeric.toHexStringNoPrefix(myinfo.getBytes()).getBytes();
            String hexValue = DMTransactionEncoder.signMessage(new String(s), pair);
            if (hexValue.startsWith("0x")) {
                hexValue = hexValue.substring(2, hexValue.length());
            }
            LogUtil.d("" + hexValue.toLowerCase());
            return hexValue;
        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
        return "";
    }

}
