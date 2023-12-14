package com.app.store.detail;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityDappStoreDetailBinding;
import com.app.store.DAppStoreEntity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;
import common.app.utils.DownLoadFileTask;
import common.app.utils.FileUtils;
import common.app.utils.GlideUtil;
import common.app.utils.LogUtil;
import common.app.utils.UriUtil;


public class DAppStoreDetailActivity extends BaseActivity<DAppStoreDetailVM> {
    private ActivityDappStoreDetailBinding mBinding;
    private DAppStoreDetailAdapter mAdapter;
    private DAppStoreEntity entity;
    private boolean canInstall = false;
    private static File mDownloadFile;

    private ActivityResultLauncher<Intent> mInstallLauncher;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityDappStoreDetailBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
        String info = getIntent().getStringExtra("info");
        if (TextUtils.isEmpty(info)) {
            showToast(R.string.app_store_app_not_find);
            finish();
        }
        getViewModel().appInfo = info;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        registerForResult();
        mBinding.titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        
        mAdapter = new DAppStoreDetailAdapter(new ArrayList<>());
        mBinding.inclueAppAbout.recyclerView.setAdapter(mAdapter);

        mBinding.btnProgressBar.setOnClickListener(v -> {
            if (canInstall && null != mDownloadFile) {
                checkInstallPermission();
                return;
            }

            if (entity == null || TextUtils.isEmpty(entity.appUrl)) return;
            
            mBinding.btnProgressBar.setVisibility(View.GONE);
            mBinding.downloadProgress.setVisibility(View.VISIBLE);
            mBinding.downloadProgress.setOnProgressUpdateListener(progress -> {
                Log.d("：", "progress = " + progress);
            });
            new Thread(() -> download(entity)).start();
            showToast(R.string.app_store_started_download);
        });
    }

    @Override
    public void initData() {
        super.initData();
        getViewModel().getDAppDetail();
        getViewModel().observe(viewModel.mLiveData, entity -> {
            this.entity = entity;
            
            GlideUtil.showImg(this, entity.appLogo, mBinding.appLogo);
            mBinding.appName.setText(entity.appName);
            mBinding.appDesc.setText(entity.appDeveloper);

            
            String size = "0";
            try {
                DecimalFormat df = new DecimalFormat("#.00");
                size = df.format(entity.appSize / 1024 / 1024);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mBinding.inclueAppInfo.tvAppSize.setText(size);
            
            mBinding.inclueAppInfo.tvAppGrading.setText(entity.appGrading);

            
            mBinding.inclueAppAbout.tvAppAboutInfo.setText(entity.appDesc);
            mAdapter.setNewData(entity.appImages);

            
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = null;
            try {
                packageInfo = packageManager.getPackageInfo(entity.appPackagename, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (null != packageInfo) {
                
                mBinding.btnProgressBar.setText(R.string.app_store_installed);
                mBinding.btnProgressBar.setEnabled(false);
                return;
            }

            
            String apkName = "app_" + entity.appPackagename + ".apk";
            final String savePath = getApplication().getExternalFilesDir(null) + "/" + apkName;
            File file = new File(savePath);
            
            String md5 = FileUtils.md5(file);
            if (!TextUtils.equals(md5, entity.appMd5)) return;
            if (file.exists()) {
                canInstall = true;
                mDownloadFile = file;
                mBinding.downloadProgress.setVisibility(View.GONE);
                mBinding.btnProgressBar.setVisibility(View.VISIBLE);
                mBinding.btnProgressBar.setText(R.string.app_store_to_install);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownLoadFileTask.candown = false;
    }

    private void registerForResult() {
        mInstallLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper(), msg -> {
        if (msg.what == 0) {
            Bundle bundle = msg.getData();
            int progressNum = bundle.getInt("progress");
            mBinding.downloadProgress.setProgress(progressNum);
        } else if (msg.what == 2) {
            
            DownLoadFileTask.candown = false;
            mBinding.btnProgressBar.setVisibility(View.VISIBLE);
            mBinding.downloadProgress.setVisibility(View.GONE);
            showToast(R.string.app_store_detail_download_Failed);
        } else if (msg.what == 10) {
            canInstall = true;
            mBinding.downloadProgress.setProgress(100);
            mBinding.downloadProgress.setVisibility(View.GONE);
            mBinding.btnProgressBar.setVisibility(View.VISIBLE);
            mBinding.btnProgressBar.setText(R.string.app_store_to_install);

            checkInstallPermission();
        }
        return true;
    });

    private void download(DAppStoreEntity entity) {
        String apkName = "app_" + entity.appPackagename + ".apk";
        final String savePath = getApplication().getExternalFilesDir(null) + "/" + apkName;
        File file = new File(savePath);
        try {
            
            if (file.isFile() && file.exists()) file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int appSize = (int) entity.appSize;
            DownLoadFileTask.candown = true;
            DownLoadFileTask mDownLoadFileTask = new DownLoadFileTask(mHandler);
            mDownloadFile = mDownLoadFileTask.getFile(entity.appUrl, file, appSize);

            if (mDownloadFile != null) {
                Message message = new Message();
                message.what = 10;
                mHandler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("DAppStoreDetailActivity", "App");
        }
    }

    private void install(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = UriUtil.getFileUri(this, file);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }

    private void checkInstallPermission() {
        
        String md5 = FileUtils.md5(mDownloadFile);
        if (!TextUtils.isEmpty(entity.appMd5) && !TextUtils.equals(md5, entity.appMd5)) {
            showToast(R.string.app_store_file_error);
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            boolean isGranted = getPackageManager().canRequestPackageInstalls();
            
            if (isGranted) install(mDownloadFile);
            else toInstallPermission();
        } else install(mDownloadFile);
    }

    private void toInstallPermission() {
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            showToast(R.string.app_store_open_install_permission);
            return;
        }
        
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        }
        mInstallLauncher.launch(intent);
    }
}
