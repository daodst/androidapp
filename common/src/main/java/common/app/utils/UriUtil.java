

package common.app.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;



public class UriUtil {

    
    public static Uri getFileUri(Context context, File file){
        Uri contentUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String applicationId = context.getPackageName();
            contentUri = FileProvider.getUriForFile(context, applicationId+ ".fileProvider", file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        return contentUri;
    }
}
