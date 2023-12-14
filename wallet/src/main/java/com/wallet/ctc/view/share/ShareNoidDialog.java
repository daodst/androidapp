

package com.wallet.ctc.view.share;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import common.app.base.share.bean.ShareData;
import common.app.im.pojo.Share2Con;



public class ShareNoidDialog implements View.OnClickListener {
    private final String TAG = "ShareDialog";
    private Context mContext;
    private Intent intent;
    private Dialog mDialog;
    private String title;
    private String url;
    private String content;
    private String imgUrl;
    private String qrCode;
    private Bitmap bitmap;
    private int type;


    
    public ShareNoidDialog(Context context, ShareData data) {
        this.mContext = context;
        this.content = data.content;
        this.url = data.getUrl();
        this.title = data.title;
        this.imgUrl = data.logo;
        this.qrCode = data.qrcode;
        this.type=data.type;
        if (TextUtils.isEmpty(title)) {
            this.title = mContext.getString(R.string.app_name);
        }
        if (TextUtils.isEmpty(content)) {
            this.content = mContext.getString(R.string.app_name);
        }
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.fenxiang_wallet, null);
        layout.findViewById(R.id.share_weixin).setOnClickListener(this);
        layout.findViewById(R.id.cancle).setOnClickListener(this);
        layout.findViewById(R.id.share_my_friends).setOnClickListener(this);
        layout.findViewById(R.id.share_my_circle).setOnClickListener(this);

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
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        mDialog = dialog;
    }

    public void show() {

        mDialog .show();

    }

    private void showResult(String info) {
        Toast.makeText(mContext, info, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.share_weixin) {
            if(type==0) {
                share();
            }else {
                shareSingleImage();
            }
        } else if (i == R.id.share_my_friends) {
            shareMyFriends();

        } else if (i == R.id.share_my_circle) {
            shareMyCircles();

        } else if (i == R.id.cancle) {
            mDialog.dismiss();
        }
    }


    
    public void shareMyFriends() {
        try {
            startPContentActivity(mContext, new Share2Con(imgUrl, url+"/inapp", title, content));
        } catch (Exception e) {

        }

    }

    String TYPE = "TYPE";
    String CLASS = "CLASS";
    int NONO = 0;

    public static void startPContentActivity(Context from, Parcelable paramData) {
        try {

            Class clazz = Class.forName("common.app.base.activity.ContentActivity");
            Intent intent = new Intent(from, clazz);
            Bundle bundle = new Bundle();
            bundle.putInt("TYPE", 2);
            bundle.putString("DATA", new Gson().toJson(paramData));
            bundle.putString("CLASS", "messager.app.im.ui.fragment.conversion.forward.ForwardFragment");
            intent.putExtras(bundle);
            from.startActivity(intent);
        } catch (Exception e) {

        }
    }

    
    public void shareMyCircles() {
        String shareText = title;
        ArrayList<String> imgs = new ArrayList<>();
        if (!TextUtils.isEmpty(imgUrl)) {
            imgs.add(imgUrl);
        }
        try {
            Class clazz = Class.forName("messager.app.im.ui.fragment.new_chat.GroupChat");
            Intent intent = new Intent(mContext, clazz);
            intent.putExtra("sharetext", shareText.trim());
            intent.putExtra("shareurl", url.trim()+"/inapp");
            intent.putStringArrayListExtra("imglist", imgs);
            mContext.startActivity(intent);
        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
    }

    
    public void shareText() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);
        intent.putExtra(Intent.EXTRA_TITLE, content);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(Intent.createChooser(intent, ""));
    }

    
    public void shareSingleImage() {
        
        Uri imageUri = Uri.fromFile(new File(imgUrl));
        Log.d("share", "uri:" + imageUri);  

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image
    public static File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=new File(filepath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
