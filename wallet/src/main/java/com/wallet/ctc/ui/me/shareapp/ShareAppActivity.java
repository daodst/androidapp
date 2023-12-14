

package com.wallet.ctc.ui.me.shareapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.view.share.ShareNoidDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.share.bean.ShareData;
import common.app.mall.util.ToastUtil;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class ShareAppActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.share_ad_list)
    RecyclerView shareAdList;
    @BindView(R2.id.share_content)
    EditText shareContent;
    private ShareAppAdapter mAdapter;
    private List<ShareAppBean> list=new ArrayList<>();
    private Gson gson=new Gson();
    private MeApi mApi=new MeApi();
    @Override
    public int initContentView() {
        return R.layout.activity_share_app;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText("APP");
        mAdapter=new ShareAppAdapter(this);
        mAdapter.bind(list);
        LinearLayoutManager layoutManage=new  LinearLayoutManager(this);
        layoutManage.setOrientation(LinearLayoutManager.HORIZONTAL);
        shareAdList.setLayoutManager(layoutManage);
        shareAdList.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        mLoadingDialog.show();
        Map<String, Object> params2 = new TreeMap();
        mApi.getPostrList(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            list=gson.fromJson(gson.toJson(baseEntity.getData()),new TypeToken<List<ShareAppBean>>() {
                            }.getType());
                            if(list.size()>0){
                                list.get(0).setChoose(1);
                            }
                            mAdapter.bind(list);
                            mAdapter.notifyDataSetChanged();
                            shareAdList.getAdapter().notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    String logo="";
    @OnClick({R2.id.tv_back, R2.id.share_submit})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();
        } else if (i == R.id.share_submit) {

            for(int num=0;num<list.size();num++){
                if(list.get(num).getChoose()==1){
                    logo=list.get(num).getImage();
                }
            }
            mLoadingDialog.show();
            ThreadManager.getDownloadPool().execute(new Runnable() {
                @Override
                public void run() {
                    String logos=saveBmp2Gallery(getBitMBitmap(logo),"shareapp");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShareData shareData=new ShareData();
                            shareData.setUrl("");
                            shareData.content=shareContent.getText().toString();
                            shareData.title="APP";
                            shareData.logo=logos;
                            shareData.type=1;
                            mLoadingDialog.dismiss();
                            ShareNoidDialog shareNoidDialog =new ShareNoidDialog(ShareAppActivity.this,shareData);
                            shareNoidDialog.shareSingleImage();
                        }
                    });
                }
            });

        }
    }

    public static String saveBmp2Gallery(Bitmap bmp, String picName) {

        String fileName = null;
        
        String galleryPath= Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                +File.separator+"Camera"+File.separator;


        
        File file = null;
        
        FileOutputStream outStream = null;

        try {
            
            file = new File(galleryPath, picName+ ".jpg");

            
            fileName = file.toString();
            
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            }

        } catch (Exception e) {
            e.getStackTrace();
        }finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(file.toString()+"  "+file.getPath());
        return file.getPath();

    }


    public static Bitmap getBitMBitmap(String urlpath) {

        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
