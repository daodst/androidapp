

package common.app.base.share;
import android.content.Context;
import android.graphics.Bitmap;



public interface ShareInstance {

    void shareText(int platform, String text, Context activity, ShareListener listener);

    void shareMedia(int platform, String title, String targetUrl, String summary,
                    ShareImageObject shareImageObject, Context activity, ShareListener listener);

    void shareImage(int platform, ShareImageObject shareImageObject, Context activity,
                    ShareListener listener);

    
    void shareWeb(int platform, String title, String content, String url, Bitmap bitmap, Context activity,
                  ShareListener listener);

}
