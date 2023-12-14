

package common.app.im.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Set;

import common.app.BuildConfig;
import common.app.Injection;
import common.app.R;
import common.app.utils.DeviceUtils;


public class Pic {


    public void getMatisseLocal(int requestCode, int count) {
        Matisse matisse = null == getFragment() ? Matisse.from(getActivity()) : Matisse.from(getFragment());
        matisse.choose(MimeType.ofAll())
                .countable(true)
                
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
                .maxSelectable(count)
                
                .gridExpectedSize(Injection.provideContext().getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(requestCode);
    }

    public void getMatisseLocal(int requestCode, int count, Set<MimeType> type) {
        Matisse matisse = null == getFragment() ? Matisse.from(getActivity()) : Matisse.from(getFragment());
        matisse.choose(type)
                .countable(true)
                
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
                .maxSelectable(count)
                
                .gridExpectedSize(Injection.provideContext().getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(requestCode);
    }

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private Pic(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    private Pic(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }


    public static Pic from(Activity activity) {
        return new Pic(activity);
    }

    public static Pic from(Fragment fragment) {
        return new Pic(fragment.getActivity(), fragment);
    }

    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

    public void getLocalImgResult(int requestCode) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image
    protected static String handleImageBeforeKitKat(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = Injection.provideContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;
            if (picturePath == null || "null".equals(picturePath)) {
                String error = Injection.provideContext().getString(R.string.cant_find_pictures);
                throw new RuntimeException(error);
            }
            return picturePath;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                String error = Injection.provideContext().getString(R.string.cant_find_pictures);
                throw new RuntimeException(error);
            }
            return file.getAbsolutePath();
        }

    }

    
    @TargetApi(19)
    private static String handleImageOnKitKat(Uri selectedImage) {
        String imagePath = null;
        Log.d("TAG", "handleImageOnKitKat: uri is " + selectedImage);
        if (DocumentsContract.isDocumentUri(Injection.provideContext(), selectedImage)) {
            
            String docId = DocumentsContract.getDocumentId(selectedImage);
            if ("com.android.providers.media.documents".equals(selectedImage.getAuthority())) {
                String id = docId.split(":")[1]; 
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(selectedImage.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(selectedImage.getScheme())) {
            
            imagePath = getImagePath(selectedImage, null);
        } else if ("file".equalsIgnoreCase(selectedImage.getScheme())) {
            
            imagePath = selectedImage.getPath();
        }
        return imagePath;
    }


    
    private static String getImagePath(Uri uri, String selection) {
        String path = null;
        
        Cursor cursor = Injection.provideContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


}
