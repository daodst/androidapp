

package common.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import common.app.AppApplication;
import common.app.R;



public class GlideUtil {

    public static final String RES_SCHEME = "res://";
    public static final String FILE_SCHEME = "file://";

    private static final String TAG = "GlideUtil";
    public interface CbGetImg{
        public void onGet(Bitmap img);
    }

    
    public static void resumeShowImg(Context context){
        if(!isValidContext(context)){
            return;
        }
        Glide.with(context).pauseRequests();
    }

    
    public static void pauseShowImg(Context context){
        if(!isValidContext(context)){
            return;
        }
        Glide.with(context).resumeRequests();
    }

    public static boolean isValidContext(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    
    public static String getMyUrl(String url){
        
        return url;
    }

    public static String getHttpUrl(String url){
        
        return url;
    }

    
    private static String convertHttpImgUrl(String url) {
        if (TextUtils.isEmpty(url) || url.startsWith(FILE_SCHEME) || url.startsWith(RES_SCHEME)) {
            return url;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            
        }
        return url;
    }

    
    protected static void showResImg(Context context, String resPath, ImageView imageView) {
        if (!TextUtils.isEmpty(resPath) && resPath.startsWith(RES_SCHEME)) {
            Uri uri = Uri.parse(resPath);
            String host = uri.getHost(); 
            String path = uri.getPath(); 
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            int logo = AppApplication.getInstance().getApplicationContext().getResources().getIdentifier(path, host, AppApplication.getInstance().getApplicationContext().getPackageName());
            showImg(context, logo, imageView);
        } else {
            showAllImg(context, resPath, imageView);
        }
    }

    

    
    public static void showImg(Context context, String url, ImageView imageView, int errorResLogo, CbGetImg cbGetImg){
        if (!isValidContext(context) || null == imageView) {
            return;
        }
        if (!TextUtils.isEmpty(url) && url.startsWith(RES_SCHEME)) {
            showResImg(context, url, imageView);
            return;
        }
        url = convertHttpImgUrl(url);
        Target target = null;
        if (null != cbGetImg) {
            target = new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (null != imageView) {
                        imageView.setImageBitmap(resource);
                    }
                    cbGetImg.onGet(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            };
        }

        RequestBuilder reb = Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.plugin_camera_no_pictures)
                .error(errorResLogo)
                .listener(mRequestListener);
        if (target == null && null != imageView) {
            reb.into(imageView);
        } else {
            reb.into(target);
        }

    }

    
    public static void showImg(Context context, String url, ImageView imageView){
        showImg(context, url, imageView, R.mipmap.none, null);
    }


    
    public static void showImgSD(Context context, String url, ImageView imageView){
        if (isValidContext(context)) {
            Glide.with(context).load(url).placeholder(R.mipmap.plugin_camera_no_pictures).error(R.mipmap.none).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }
    
    public static void showAllImg(Context context, String url, ImageView imageView){
        if (isValidContext(context)) {
            Glide.with(context).load(url).placeholder(R.mipmap.plugin_camera_no_pictures).error(R.mipmap.none).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }
    

    
    public static void showImg(Context context, String url, ImageView imageView, CbGetImg cbGetImg){
        showImg(context, url, imageView, R.mipmap.none, cbGetImg);
    }

    
    public static void showImg(Context context, int res, ImageView imageView){
        if (isValidContext(context)) {
            Glide.with(context).load(res).error(R.mipmap.none).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    
    public static void showGif(Context context, int res, ImageView imageView){
        if (isValidContext(context)) {
            Glide.with(context).asGif().load(res).error(R.mipmap.none).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    
    public static void showImg(Context context, File file, ImageView imageView){
        if (isValidContext(context)) {
            Glide.with(context).load(file).error(R.mipmap.none).into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public  static RequestListener mRequestListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    };
}
