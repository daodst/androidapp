

package common.app.im.ui.fragment.qrcode;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huawei.hms.hmsscankit.OnLightVisibleCallBack;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.ActivityRouter;
import common.app.R;
import common.app.R2;
import common.app.base.base.BaseFragment;
import common.app.base.base.PermissionListener;
import common.app.base.share.qr.QrCodeUtils;
import common.app.base.them.Eyes;


public class QRCodeFragment extends BaseFragment {


    @BindView(R2.id.rim)
    FrameLayout mRim;
    @BindView(R2.id.flush_btn)
    ImageView mFlushBtn;
    @BindView(R2.id.back_img)
    ImageView mBackImg;
    @BindView(R2.id.img_btn)
    ImageView mImgBtn;
    @BindView(R2.id.title_bar)
    RelativeLayout mTitleBar;

    private RemoteView remoteView;

    int mScreenWidth;
    int mScreenHeight;
    
    final int SCAN_FRAME_SIZE = 240;

    private int[] img = {R.drawable.flashlight_on, R.drawable.flashlight_off};
    private static final String TAG = "QRCodeFragment";

    
    public static final int REQUEST_CODE_PHOTO = 0X1113;

    
    private String mDirectReturnFlag = "";
    public static final String KEY_CONTENT = "content";

    public static final Intent getIntent(Context context) {
        return getEmptyIntent(context, QRCodeFragment.class.getName());
    }

    @Override
    protected void getBundleData(boolean needCheck) {
        super.getBundleData(needCheck);
        if (mParamData instanceof String) {
            mDirectReturnFlag = (String) mParamData;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hw_qrcode, container, false);
        Eyes.setTranslucent(getActivity());
        mUnbinder = ButterKnife.bind(this, view);


        
        mActivity.requestRuntimePermisssions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, }, new PermissionListener() {
                    
            public void onGranted() {
                initViews(savedInstanceState);
            }

            @Override
            public void onDenied(List<String> deniedList) {
                showMsg(R.string.camera_deny);
            }
        });
        return view;
    }

    
    protected void onAnalyzeSuccess(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (!TextUtils.isEmpty(mDirectReturnFlag)) {
                if(mDirectReturnFlag.equals("toDapp")) {
                    if (result.startsWith("http://") || result.startsWith("https://")) {
                        
                        Intent intent = ActivityRouter.getIntent(mActivity, ActivityRouter.Wallet.A_DappWebViewActivity);
                        intent.putExtra("url", result);
                        mActivity.startActivity(intent);
                    } else {
                        QrCodeUtils.createInstance(mActivity).parseQrCode(result);
                    }
                    mActivity.finish();
                } else {
                    
                    Intent intent = new Intent();
                    intent.putExtra(KEY_CONTENT, result);
                    mActivity.setResult(Activity.RESULT_OK, intent);
                    mActivity.finish();
                }
                return;
            }
            
            QrCodeUtils.createInstance(mActivity).parseQrCode(result);
            mActivity.finish();
        }
    }


    
    protected void initViews(Bundle savedInstanceState) {
        
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);

        
        
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;


        
        remoteView = new RemoteView.Builder().setContext(getActivity()).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build();
        
        remoteView.setOnLightVisibleCallback(new OnLightVisibleCallBack() {
            @Override
            public void onVisibleChanged(boolean visible) {
                if(visible){
                    mFlushBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        
        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                
                if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                    onAnalyzeSuccess(result[0].getOriginalValue());
                }
            }
        });
        
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mRim.addView(remoteView, params);
        
        setBackOperation();
        setPictureScanOperation();
        setFlashOperation();

        if (Build.VERSION.SDK_INT >= 19) {
            Window var4 = getActivity().getWindow();
            if (var4 != null) {
                var4.addFlags(201326592);
                if (mTitleBar.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams var5 = new FrameLayout.LayoutParams(mTitleBar.getLayoutParams().width, mTitleBar.getLayoutParams().height);
                    var5.setMargins(0, getStatusBarHeight(), 0, 0);
                    mTitleBar.setLayoutParams(var5);
                }
            }
        }
    }


    
    private void setPictureScanOperation() {
        mImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image
    private void setFlashOperation() {
        mFlushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remoteView.getLightStatus()) {
                    remoteView.switchLight();
                    mFlushBtn.setImageResource(img[1]);
                } else {
                    remoteView.switchLight();
                    mFlushBtn.setImageResource(img[0]);
                }
            }
        });
    }

    
    private void setBackOperation() {
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(getContext(), bitmap, new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
                if (hmsScans != null && hmsScans.length > 0 && hmsScans[0] != null && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
                    onAnalyzeSuccess(hmsScans[0].getOriginalValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    protected int getStatusBarHeight() {
        int var1 = 0;
        if (this.getResources() != null) {
            int var2 = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (var2 > 0) {
                var1 = this.getResources().getDimensionPixelSize(var2);
            }
        }

        return var1;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (null != remoteView) {
            remoteView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != remoteView) {
            remoteView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != remoteView) {
            remoteView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != remoteView) {
            remoteView.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != remoteView) {
            remoteView.onDestroy();
        }
    }
}
