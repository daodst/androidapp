

package com.wallet.ctc.ui.blockchain.creattoken;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.db.CreatEthEntity;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.CreateTokenBean;
import com.wallet.ctc.model.blockchain.TxIdBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class CreatTokenTwo extends BaseActivity {


    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.create_token_jingdu)
    EditText createTokenJingdu;
    @BindView(R2.id.create_token_sum2)
    EditText createTokenSum2;
    @BindView(R2.id.create_token_wa)
    EditText createTokenWa;
    @BindView(R2.id.zidong)
    TextView zidong;
    @BindView(R2.id.create_token_email)
    EditText createTokenEmail;
    @BindView(R2.id.mineral)
    EditText mineralEdit;
    @BindView(R2.id.nianshouyi)
    LinearLayout nianshouyi;
    private BlockChainApi mApi = new BlockChainApi();
    private MeApi meApi = new MeApi();
    private Gson gson = new Gson();
    private String name;
    private String cc;
    private String url;
    private String img16;
    private int mortstatus=0;
    private InputPwdDialog mDialog;
    private CreateTokenBean createTokenBean = new CreateTokenBean();
    private int type = 0;

    @Override
    public int initContentView() {
        return R.layout.activity_creat_token_two;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        name = getIntent().getStringExtra("name");
        cc = getIntent().getStringExtra("cc");
        type = getIntent().getIntExtra("type", 0);
        url = getIntent().getStringExtra("url");
        img16 = getIntent().getStringExtra("logo");
        mortstatus=getIntent().getIntExtra("mortstatus", 0);
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                img16 = ImageToHex();
            }
        });
        tvTitle.setText(R.string.create_token);
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

    }

    private void getTxnid(String pwd) {
        mLoadingDialog.show();
        Map<String, Object> params = new TreeMap<>();
        params.put("cnt", "1");
        mApi.creatTxid(params, type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            TxIdBean txIdBean = gson.fromJson(gson.toJson(baseEntity.getData()), TxIdBean.class);
                            String sign = sign(pwd, txIdBean);
                            createTokenBean.setTxid(txIdBean.getTxId());
                            createTokenBean.setNonce(txIdBean.getNonce());
                            String arg = gson.toJson(createTokenBean);
                            Map<String, Object> params2 = new TreeMap();
                            params2.put("sign", sign);
                            params2.put("arg", arg);
                            params2.put("mortstatus",mortstatus+"");
                            creatB(params2);
                        } else {
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

    private String ImageToHex() {
        try {
            FileInputStream fis = new FileInputStream(img16);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = fis.read(buff)) != -1) {
                bos.write(buff, 0, len);
            }
            
            byte[] result = bos.toByteArray();
            
            String str = AllUtils.byte2HexStr(result);
            str = str.replaceAll(" ", "");
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @OnClick({R2.id.tv_back, R2.id.zidong, R2.id.next_step})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.zidong) {
        } else if (i == R.id.next_step) {
            String decimal = createTokenJingdu.getText().toString().trim();
            String total = createTokenSum2.getText().toString().trim();
            String award = createTokenWa.getText().toString().trim();
            String mineral = mineralEdit.getText().toString().trim();
            String email = createTokenEmail.getText().toString().trim();
            if (TextUtils.isEmpty(decimal)) {
                ToastUtil.showToast(getString(R.string.create_token_jingdu2));
                return;
            }
            if (TextUtils.isEmpty(total)) {
                ToastUtil.showToast(getString(R.string.create_token_sum3));
                return;
            }
            if (TextUtils.isEmpty(award)&&type!=1) {
                ToastUtil.showToast(getString(R.string.create_token_wa2));
                return;
            }
            if (TextUtils.isEmpty(mineral)&&type!=1) {
                ToastUtil.showToast(getString(R.string.create_token_shouyi2));
                return;
            }
            if (TextUtils.isEmpty(email)&&type!=1) {
                ToastUtil.showToast(getString(R.string.create_token_email_edit));
                return;
            }
            mDialog.show();
            createTokenBean.setCc(cc);
            createTokenBean.setName(name);
            createTokenBean.setAcc(walletDBUtil.getWalletInfo().getAllAddress());
            createTokenBean.setTotal(total);
            createTokenBean.setAward(award);
            createTokenBean.setMineral(mineral);
            createTokenBean.setEmail(email);
            createTokenBean.setUrl(url);
            createTokenBean.setDecimal(decimal);
            createTokenBean.setLogo(img16);
            createTokenBean.setMortstatus(mortstatus+"");
        } else {
        }
    }

    private void creatB(Map<String, Object> params2) {
        LogUtil.d(params2.toString());
        mApi.creatAssest(params2, type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            success(baseEntity.getData().toString());
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void success(String logo) {
        int time=(int) (System.currentTimeMillis()/1000);
        CreatEthEntity creatEthEntity=new CreatEthEntity(createTokenBean.getCc(),createTokenBean.getName(),createTokenBean.getTotal(),createTokenBean.getDecimal(), SettingPrefUtil.getWalletAddress(this),type,1, time);
        creatEthEntity.setImg_path(logo);
        DBManager.getInstance(this).insertCreatHistory(creatEthEntity);
        AssertBean assbean = new AssertBean(logo, createTokenBean.getCc().toLowerCase(), createTokenBean.getName(), "", "", createTokenBean.getDecimal(), type, 2);
        assbean.setTotal(createTokenBean.getTotal());
        assbean.setAward(createTokenBean.getAward());
        assbean.setUrl(createTokenBean.getUrl());
        assbean.setWalletAddress("");
        assbean.setMineral(createTokenBean.getMineral());
        walletDBUtil.addAssets(assbean);
        AssertBean assbean2 = new AssertBean(logo, createTokenBean.getCc().toLowerCase(), createTokenBean.getName(), "", "", createTokenBean.getDecimal(), type, 2);
        assbean2.setTotal(createTokenBean.getTotal());
        assbean2.setAward(createTokenBean.getAward());
        assbean2.setUrl(createTokenBean.getUrl());
        assbean2.setMineral(createTokenBean.getMineral());
        assbean2.setWalletAddress(SettingPrefUtil.getWalletAddress(this));
        walletDBUtil.addAssets(assbean2);
        ToastUtil.showToast(getString(R.string.caozuo_success));
        setResult(RESULT_OK);
        finish();
    }

    private String sign(String pwd, TxIdBean txIdBean) {
        try {
            
            String myinfo = (txIdBean.getTxId() + cc + walletDBUtil.getWalletInfo().getAllAddress()).toLowerCase();
            String hexValue=new WalletTransctionUtil(this).sign(myinfo,walletDBUtil.getWalletInfo(),pwd);
            return hexValue;
        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
        return "";
    }
}
