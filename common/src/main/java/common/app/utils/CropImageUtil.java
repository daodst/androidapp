

package common.app.utils;

import static android.os.Build.VERSION_CODES.Q;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;



public class CropImageUtil {
    public static Uri cropImageUri(Activity activity, Uri uri, int outputX, int outputY, int requestCode, boolean isCamera) {
        if (uri.toString().startsWith("file://") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = UriUtil.getFileUri(activity, new File(uri.getPath()));
        }

        Uri cropImageUri = FileUtils.createImageFile(activity);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Q) {
            if(isCamera){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }else {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        LogUtil.d("zzz",cropImageUri.toString()+"   "+cropImageUri.getPath());
        intent.setDataAndType(uri, "image/*");
        
        intent.putExtra("crop", "true");
        
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        
        
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        
        intent.putExtra("noFaceDetection", true); 
        
        try {
            
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cropImageUri;
    }

}
