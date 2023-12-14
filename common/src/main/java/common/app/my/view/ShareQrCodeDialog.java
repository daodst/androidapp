

package common.app.my.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.R;
import common.app.R2;
import common.app.base.base.BaseActivity;
import common.app.base.base.PermissionListener;
import common.app.base.share.qr.QrCodeUtils;
import common.app.mall.util.ToastUtil;



public class ShareQrCodeDialog {
    private final String TAG = "ShareDialog";
    @BindView(R2.id.qrcode)
    ImageView qrcode;
    @BindView(R2.id.qrcode_dialog)
    LinearLayout qrcodeDialog;
    @BindView(R2.id.dialog_close)
    ImageView dialogClose;
    private Bitmap mQRBitmap;
    private Activity mContext;
    private Dialog mDialog;
    private int type = 0;
    private String content = "";
    private String[] MUST_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    
    public ShareQrCodeDialog(Activity context) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.share_sr_code, null);
        ButterKnife.bind(this, layout);

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(layout, layoutParams);

        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        mDialog = dialog;
        dialogClose.setOnClickListener(v -> {
            dismiss();
        });
        qrcodeDialog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((BaseActivity) mContext).requestRuntimePermisssions(MUST_PERMISSION, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        saveQr();
                    }

                    @Override
                    public void onDenied(List<String> deniedList) {
                    }
                });
                return false;
            }
        });
    }

    public void show(int type, String content) {
        this.type = type;
        this.content = content;
        mQRBitmap = QrCodeUtils.creatQRCodeImg(content, BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
        qrcode.setImageBitmap(mQRBitmap);
        mDialog.show();
    }

    private SendCom mSendCom;

    public void SetSendCom(SendCom mSendCom) {
        this.mSendCom = mSendCom;
    }

    public interface SendCom {
        void sendCom(String content);

        void sendReply(String id, String content);
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    
    private void saveQr() {

        doSave((success, filePath, fileName) -> {
            if (success) {
                try {
                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), filePath, fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
                ToastUtil.showToast("" + filePath);
            }
        });
    }

    private interface FileSaveResult {
        public void onSave(boolean success, String filePath, String fileName);
    }

    private void doSave(FileSaveResult result) {
        if (null == mQRBitmap) {
            result.onSave(false, null, null);
            return;
        }
        String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        String fileName = System.nanoTime() + "_" + Math.random();
        try {
            File f = new File(savePath, fileName + ".jpg");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            mQRBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            String filePath = f.getAbsolutePath();
            result.onSave(true, filePath, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ToastUtil.showToast("");
            result.onSave(false, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.showToast("");
            result.onSave(false, null, null);
        }
    }

}
