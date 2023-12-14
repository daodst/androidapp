

package com.wallet.ctc.ui.blockchain.creattoken;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.PermissionUtils;
import com.wallet.ctc.util.SettingPrefUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.view.RoundImageView;
import common.app.mall.util.ToastUtil;
import common.app.utils.CropImageUtil;
import common.app.utils.FileUtils;
import common.app.utils.UriUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class CreatTokenOne extends BaseActivity {


    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.token_name)
    EditText tokenName;
    @BindView(R2.id.token_suo)
    EditText tokenSuo;
    @BindView(R2.id.guanwang)
    EditText guanwang;
    @BindView(R2.id.token_img)
    RoundImageView tokenImg;
    @BindView(R2.id.tishi)
    TextView tishi;
    @BindView(R2.id.check)
    CheckBox check;
    @BindView(R2.id.check2)
    CheckBox check2;
    private final int CHOOSE_PICTURE = 0x000002;
    private final int CROP_SMALL_PICTURE = 0x000003;
    @BindView(R2.id.guanwang_view)
    View guanwangView;
    @BindView(R2.id.token_logo_view)
    View tokenLogoView;
    private String img16;
    private String yue;
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private BlockChainApi mApi = new BlockChainApi();
    private MeApi meApi = new MeApi();
    private int type;
    private int mortstatus=0;
    private com.wallet.ctc.db.WalletEntity WalletEntity;
    @Override
    public int initContentView() {
        return R.layout.activity_creat_token_one;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        tvTitle.setText(R.string.create_token);
        WalletEntity = walletDBUtil.getWalletInfo();
        LogUtil.d(gson.toJson(WalletEntity));
        if(WalletEntity.getType()!=type){
            List<WalletEntity> list= walletDBUtil.getWalletList(type);
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
        if (type == WalletUtil.DM_COIN) {
            guanwang.setHint(R.string.create_token_guanwang);
            if (Double.parseDouble(WalletEntity.getmBalance()) >= 300) {
                tishi.setText(getString(R.string.create_token_tishi, "DM", WalletEntity.getmBalance(), "" + 300, "DM"));
            } else {
                tishi.setText(getString(R.string.create_token_tishi2, "DM", WalletEntity.getmBalance()));
                tishi.setTextColor(0xffFF0000);
                findViewById(R.id.next_step).setEnabled(false);
                findViewById(R.id.next_step).setBackgroundResource(R.drawable.lin_huise_bg);
            }
            findViewById(R.id.diya).setVisibility(View.VISIBLE);
        } else if (type == WalletUtil.MCC_COIN) {
            yue = WalletEntity.getmBalance();
            String tokenDefName = getString(R.string.default_token_name).toUpperCase();
            guanwang.setHint(R.string.create_token_mccguanwang);
            if (Double.parseDouble(yue) >= BuildConfig.RELEASE_FEE) {
                tishi.setText(getString(R.string.create_token_tishi, tokenDefName, yue, "" + BuildConfig.RELEASE_FEE, tokenDefName));
            } else {
                tishi.setText(getString(R.string.create_token_tishi2, tokenDefName, yue));
                tishi.setTextColor(0xffFF0000);
                findViewById(R.id.next_step).setEnabled(false);
                findViewById(R.id.next_step).setBackgroundResource(R.drawable.lin_huise_bg);
            }
        }else if (type == WalletUtil.OTHER_COIN) {
            yue = WalletEntity.getmBalance();
            String tokenDefName = getString(R.string.default_other_token_name).toUpperCase();
            guanwang.setHint(R.string.create_token_otherguanwang);
            if (Double.parseDouble(yue) >= BuildConfig.RELEASE_OTHER_FEE) {
                tishi.setText(getString(R.string.create_token_tishi, tokenDefName, yue, "" + BuildConfig.RELEASE_OTHER_FEE, tokenDefName));
            } else {
                tishi.setText(getString(R.string.create_token_tishi2, tokenDefName, yue));
                tishi.setTextColor(0xffFF0000);
                findViewById(R.id.next_step).setEnabled(false);
                findViewById(R.id.next_step).setBackgroundResource(R.drawable.lin_huise_bg);
            }
        } else if (type == WalletUtil.ETH_COIN) {
            guanwang.setVisibility(View.GONE);
            findViewById(R.id.token_logo_lin).setVisibility(View.GONE);
            guanwangView.setVisibility(View.GONE);
            tokenLogoView.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void getPermission(int requestCode) {
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_PICK);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image
    private Uri cropImageUri;
    private Uri takePicUri;
    private final int CROP_IMG_WIDTH = 60;
    private final int CROP_IMG_HEIGHT = 60;

    
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode,boolean isCamera) {
        cropImageUri= CropImageUtil.cropImageUri(this,uri,outputX,outputY,requestCode,isCamera);
    }

    
    private Uri getSomeUri() {
        String fileName = FileUtils.SDPATH + String.valueOf(System.currentTimeMillis()) + ".jpg";
        String local = FileUtils.SDPATH;
        File folder = new File(local);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return UriUtil.getFileUri(CreatTokenOne.this, new File(fileName));
    }
    

    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
