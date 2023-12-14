

package common.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

import common.app.AppApplication;
import common.app.R;
import common.app.base.fragment.mall.api.VerApi;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.fragment.mall.model.BaseEntity;
import common.app.base.fragment.mall.model.NewVersionBean;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.update.UpdatingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;



public class AppVerUtil {

    private Context mContext;
    private UpdatingDialog updatingDialog;
    private NewVersionBean version = new NewVersionBean();
    private Thread download;
    private CompositeDisposable mDisposable;
    private DownLoadFileThreadTask mDownloadTask;
    private String TAG = "AppVerUtil";


    public AppVerUtil(Context context) {
        this.mContext = context;
        this.mDisposable = new CompositeDisposable();
    }

    
    public interface VerCheckListener {
        default void needUpdate() {
        }

        default void success() {
        }

        void onCancel();

    }


    private VerCheckListener mVerCheckListener;

    
    public void checkVer(VerCheckListener listener) {
        if (null == mContext) {
            return;
        }
        
        if (!NetUtils.isNetworkConnected(mContext)) {
            if (null != listener) {
                listener.onCancel();
            }
            return;
        }
        mVerCheckListener = listener;
        VerApi mApi = new VerApi();
        mApi.getVersion2().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BaseEntity baseEntity) throws Exception {
                        Gson gson = new Gson();
                        if (baseEntity.getStatus() == 1) {
                            version = gson.fromJson(gson.toJson(baseEntity.getData()), NewVersionBean.class);
                            if (null != version) {
                                if (null != listener) {
                                    listener.success();
                                }
                                String newVesion = version.getNewVersion();
                                String appVesion = getPackageInfo().versionName.trim();
                                if (newVesion.length() < appVesion.length()) {
                                    newVesion = banbe(newVesion, appVesion.length() - newVesion.length());
                                }
                                if (appVesion.length() < newVesion.length()) {
                                    appVesion = banbe(appVesion, newVesion.length() - appVesion.length());
                                }
                                if (!appVesion.equals(newVesion) && appVesion.compareTo(newVesion) < 0) {
                                    
                                    if (null != listener) {
                                        listener.needUpdate();
                                    }
                                    updatingDialog = new UpdatingDialog(mContext, version);
                                    updatingDialog.show();
                                    updatingDialog.setChangeStateListener(new UpdatingDialog.changestateListener() {
                                        @Override
                                        public void change(int position) {
                                            if (position == 0) {
                                                NetWorkUtils netWorkUtils = new NetWorkUtils();
                                                int type = netWorkUtils.getNetType();
                                                if (type != NetWorkUtils.NET_WORK_STATE_WIFI) {
                                                    showNetAlert(version);
                                                } else {
                                                    upload(version);
                                                    updatingDialog.setUI(0);
                                                }

                                            } else if (position == 1) {
                                                if (null != listener) {
                                                    listener.onCancel();
                                                }
                                            } else {
                                                DownLoadFileTask.candown = false;
                                                if (version.getClientForceUpdate(mContext.getPackageName())) {
                                                    updatingDialog.setUI(1);
                                                } else {
                                                    updatingDialog.dismiss();
                                                    if (null != listener) {
                                                        listener.onCancel();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    if (null != listener) {
                                        listener.onCancel();
                                    } else {
                                        
                                        ToastUtil.showToast(mContext.getResources().getString(R.string.was_latest_version));
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(mContext, baseEntity.getInfo(), Toast.LENGTH_SHORT).show();
                            if (null != listener) {
                                listener.onCancel();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        e.printStackTrace();
                        if (null != listener) {
                            listener.onCancel();
                        }
                    }
                });
    }

    
    private void showNetAlert(NewVersionBean newVersion) {
        MyAlertDialog dialog = new MyAlertDialog(mContext, mContext.getString(R.string.download_tips_network));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                upload(newVersion);
                updatingDialog.setUI(0);
                dialog.dismiss();
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private String banbe(String data, int len) {
        for (int i = 0; i < len; i++) {
            data = data + "0";
        }
        return data;
    }


    
    private void upload(NewVersionBean newVersion) {
        String apkUrl = GlideUtil.getHttpUrl(newVersion.getDownloadUrl());

        if (!TextUtils.isEmpty(apkUrl) && !apkUrl.endsWith(".apk")) {
            try {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(apkUrl);
                intent.setData(content_url);
                mContext.startActivity(intent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PackageInfo mPackageInfo = getPackageInfo();
        String apkName = "app_" + mPackageInfo.packageName + ".apk";
        final String savePath = AppApplication.getContext().getExternalFilesDir(null) + "/" + apkName;

        File file = new File(savePath);
        try {
            if (file.isFile() && file.exists()) {
                
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != mDownloadTask) {
            mDownloadTask.cancelAll();
        }
        mDownloadTask = new DownLoadFileThreadTask(
                apkUrl,
                file, newVersion.package_size, newVersion.apk_md_5_hash);

        new Thread(mDownloadTask).start();
    }


    
    public class DownLoadFileThreadTask implements Runnable {
        private String path; 
        private File files; 
        private int size;
        private String hash;

        
        public DownLoadFileThreadTask(String path, File file, int size, String hash) {
            
            this.path = path;
            this.files = file;
            this.size = size;
            this.hash = hash;
        }

        public void cancelAll() {
            if (null != handler) {
                DownLoadFileTask.candown = false;
                handler.removeMessages(0);
                handler.removeMessages(102);
            }
        }

        @SuppressWarnings("all")
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Bundle bunlde = msg.getData();
                        int progressNum = bunlde.getInt("progress");
                        if (updatingDialog != null) {
                            updatingDialog.setProgress(progressNum);
                        }
                        break;
                    default: {
                        break;
                    }
                }
            }
        };

        
        @Override
        public void run() {
            try {
                DownLoadFileTask.candown = true;
                DownLoadFileTask mDownLoadFileTask = new DownLoadFileTask(
                        handler);
                File file = mDownLoadFileTask.getFile(path, files, size);
                if (file == null) {
                    return;
                }

                String md5 = FileUtils.md5(file);
                if (TextUtils.equals(md5, hash)) {
                    install(files);
                } else {
                    ToastUtil.showToast("");
                }
            } catch (Exception e) {
                handler.obtainMessage(102).sendToTarget();
                e.printStackTrace();
            }
        }
    }

    
    private void install(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = UriUtil.getFileUri(mContext, file);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        updatingDialog.dismiss();
        mContext.startActivity(intent);
    }

    
    private PackageInfo getPackageInfo() {
        PackageInfo pinfo = null;
        try {
            pinfo = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo;
    }

    public void cancel() {
        
        if (null != mDisposable) {
            mDisposable.dispose();
        }
        
        if (mDownloadTask != null) {
            mDownloadTask.cancelAll();
        }
    }
}
