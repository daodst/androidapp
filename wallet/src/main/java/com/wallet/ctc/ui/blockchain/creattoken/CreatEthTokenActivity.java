

package com.wallet.ctc.ui.blockchain.creattoken;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.CreatEthEntity;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.CreateTokenBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.PermissionUtils;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.dialog.CreatEthTokenDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.view.RoundImageView;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.CropImageUtil;
import common.app.utils.FileUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class CreatEthTokenActivity extends BaseActivity {


    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.token_name)
    EditText tokenName;
    @BindView(R2.id.token_shortname)
    EditText tokenShortname;
    @BindView(R2.id.token_jingdu)
    EditText tokenJingdu;
    @BindView(R2.id.token_sum)
    EditText tokenSum;
    @BindView(R2.id.token_img)
    RoundImageView tokenImg;
    @BindView(R2.id.website)
    EditText website;
    @BindView(R2.id.email)
    EditText email;
    @BindView(R2.id.whitepaper)
    EditText whitepaper;
    @BindView(R2.id.zhongwen)
    EditText zhongwen;
    @BindView(R2.id.yingwen)
    EditText yingwen;
    @BindView(R2.id.twitter)
    EditText twitter;
    @BindView(R2.id.telegram)
    EditText telegram;
    @BindView(R2.id.github)
    EditText github;
    @BindView(R2.id.facebook)
    EditText facebook;
    @BindView(R2.id.reddit)
    EditText reddit;
    @BindView(R2.id.slack)
    EditText slack;
    @BindView(R2.id.medium)
    EditText medium;
    @BindView(R2.id.eth)
    EditText eth;
    @BindView(R2.id.usd)
    EditText usd;
    @BindView(R2.id.btc)
    EditText btc;
    private MeApi meApi = new MeApi();
    private Gson gson = new Gson();
    private InputPwdDialog mDialog;
    private CreatEthTokenDialog cDialog;
    private CreateTokenBean createTokenBean = new CreateTokenBean();
    private BigDecimal gasPrice;
    private BigDecimal gasCount;
    private String fromAddress;
    private String data;
    private final int CHOOSE_PICTURE = 0x000002;
    private final int CROP_SMALL_PICTURE = 0x000003;
    private String img16;
    private com.wallet.ctc.db.WalletEntity WalletEntity;
    private int walletType;
    private String coinName="ETH";

    @Override
    public int initContentView() {
        walletType=getIntent().getIntExtra("type", WalletUtil.ETH_COIN);
        return R.layout.activtiy_creat_eth;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(R.string.create_token);
        WalletEntity= walletDBUtil.getWalletInfo();
        fromAddress =WalletEntity.getAllAddress();
        if(WalletEntity.getType()!=walletType){
           List<WalletEntity> list= walletDBUtil.getWalletList(walletType);
            if(null!=list){
                WalletEntity=list.get(0);
                SettingPrefUtil.setWalletTypeAddress(this, WalletEntity.getType(), WalletEntity.getAllAddress());
                ToastUtil.showLongToast(getString(R.string.creat_token_tishi));
            }else {
                ToastUtil.showToast(getString(R.string.creat_token_nowallet));
                finish();
                return;
            }
        }
        List<AssertBean> assertBeanList=walletDBUtil.getMustWallet(walletType);
        coinName=assertBeanList.get(0).getShort_name().toUpperCase();
        mDialog = new InputPwdDialog(this, getString(R.string.place_edit_password));
        cDialog = new CreatEthTokenDialog(this);
        cDialog.setonclick(new CreatEthTokenDialog.Onclick() {
            @Override
            public void Yes() {
                cDialog.dismiss();
                mDialog.show();
            }

            @Override
            public void No() {
                cDialog.dismiss();
            }
        });
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog.dismiss();
                if (!WalletEntity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(getString(R.string.password_error2));
                    return;
                }
                getNonce(pwd, data);
            }

            @Override
            public void No() {
                mDialog.dismiss();
            }
        });

        tokenJingdu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = tokenJingdu.getText().toString();
                if (data.startsWith("-")) {
                    tokenJingdu.setText("");
                    return;
                }
                if (null != data && !TextUtils.isEmpty(data)) {
                    int n = Integer.parseInt(data);
                    if (n > 18) {
                        tokenJingdu.setText("18");
                    } else if (n < 0) {
                        tokenJingdu.setText("1");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getDefGasprice();
    }

    @Override
    public void initData() {

    }

    public void getDefGasprice() {
        mLoadingDialog.show();
        Map<String, Object> params = new TreeMap();
        meApi.getGasDefPrice2(params,WalletEntity.getType()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            String p = baseEntity.getMessage().toString();
                            if (p.startsWith("0x")) {
                                p = p.substring(2, p.length());
                                p = new BigInteger(p, 16).toString(10);
                            }
                            LogUtil.d("gasprice" + p);
                            gasPrice = new BigDecimal(p).multiply(new BigDecimal("1000000000"));
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    
    private void getKGPrice(String data) {
        Map<String, Object> params = new TreeMap();
        params.put("to_addr", "");
        params.put("from_addr", fromAddress);
        params.put("value", "0x0");
        params.put("contract_addr", "");
        params.put("data", data);
        mLoadingDialog.show();
        meApi.getGas(params,WalletEntity.getType()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            gasCount = new BigDecimal(baseEntity.getMessage().toString());
                            String bigDecimal = gasPrice.multiply(gasCount).toString();
                            BigDecimal sum = new BigDecimal(bigDecimal);
                            BigDecimal jinzhi = new BigDecimal("1000000000000000000");
                            sum = sum.divide(jinzhi).setScale(8, BigDecimal.ROUND_HALF_UP);
                            if (Double.parseDouble(WalletEntity.getmBalance()) >= sum.doubleValue()) {
                                cDialog.show(sum.toPlainString() + coinName);
                            } else {
                                cDialog.setYesText(getString(R.string.insufficient_balance));
                                cDialog.show(sum.toPlainString() + coinName);
                            }
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(getString(R.string.http_out_time));
                    }
                });
    }

    @Override
    public void getPermission(int requestCode) {
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_PICK);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image
    private void signTransfer(String pwd, BigInteger nonce, String data) {
          String hexValue =new WalletTransctionUtil(CreatEthTokenActivity.this).getCreateEthTokenSign(WalletEntity,gasCount.intValue(),nonce.intValue(),gasPrice.toPlainString(),pwd,data);
         jiaoyiHttp(hexValue);
    }

    private void jiaoyiHttp(String hexValue) {
        try {
            mLoadingDialog.show();
            Map<String, Object> params = new TreeMap();
            params.put("data", hexValue);
            params.put("symbol", createTokenBean.getCc());
            params.put("website", website.getText().toString().trim());
            params.put("email", email.getText().toString().trim());
            params.put("whitepaper", whitepaper.getText().toString().trim());
            params.put("state", "");
            params.put("published_on", AllUtils.getTime());
            params.put("en", yingwen.getText().toString().trim());
            params.put("zh", zhongwen.getText().toString().trim());
            params.put("twitter", twitter.getText().toString().trim());
            params.put("telegram", telegram.getText().toString().trim());
            params.put("github", github.getText().toString().trim());
            params.put("facebook", facebook.getText().toString().trim());
            params.put("reddit", reddit.getText().toString().trim());
            params.put("slack", slack.getText().toString().trim());
            params.put("medium", medium.getText().toString().trim());
            params.put("eth", eth.getText().toString().trim());
            params.put("usd", usd.getText().toString().trim());
            params.put("btc", btc.getText().toString().trim());
            params.put("logo", img16);
            meApi.getTranfer(params,WalletEntity.getType()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<BaseEntity>(CreatEthTokenActivity.this) {
                        @Override
                        public void onNexts(BaseEntity baseEntity) {
                            mLoadingDialog.dismiss();
                            String data = gson.toJson(baseEntity.getData());
                            if (baseEntity.getStatus() == 1 && !data.equals("false")) {
                                ToastUtil.showToast(getString(R.string.caozuo_success));
                                CreatEthEntity creatEthEntity = new CreatEthEntity(createTokenBean.getCc(), createTokenBean.getName(), createTokenBean.getTotal(), createTokenBean.getDecimal(), fromAddress, walletType, 0, (int) (System.currentTimeMillis() / 1000));
                                creatEthEntity.setHexValue(baseEntity.getData().toString());
                                DBManager.getInstance(CreatEthTokenActivity.this).insertCreatHistory(creatEthEntity);
                                Intent intent = new Intent(CreatEthTokenActivity.this, CreatTokenHistoryActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.showToast(getString(R.string.caozuo_fail));
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_PICTURE:
                    
                    cropImageUri(data.getData(), CROP_IMG_WIDTH, CROP_IMG_HEIGHT, CROP_SMALL_PICTURE,false);
                    break;
                case CROP_SMALL_PICTURE:
                    setImageToView(data);
                    break;
                default:
                    break;
            }
        }
    }

    
    private Uri cropImageUri;
    private final int CROP_IMG_WIDTH = 60;
    private final int CROP_IMG_HEIGHT = 60;

    
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode,boolean isCamera) {
        FileUtils.deleteUri(this,cropImageUri);
        cropImageUri= CropImageUtil.cropImageUri(this,uri,outputX,outputY,requestCode,isCamera);
    }

    protected void setImageToView(Intent data) {
        String path=cropImageUri.getPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path=FileUtils.getCropFile(this,cropImageUri).toString();
        }
        GlideUtil.showImgSD(this,path,tokenImg);
        img16 = path;
        img16 = ImageToHex();
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
}
