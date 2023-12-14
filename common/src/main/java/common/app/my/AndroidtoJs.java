

package common.app.my;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.app.utils.Base64Utils;



public class AndroidtoJs extends Object {

    public static final String METHOD_NAME = "androidapp";

    Context mContext;
    private String from;
    private Bitmap bitmap = null;

    private Handler mHandler = new Handler();

    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();

    public AndroidtoJs(Context mContext, String from) {
        this.mContext = mContext;
        this.from = from;
    }


    @JavascriptInterface
    public void toCall(String mobile) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));
        mContext.startActivity(dialIntent);
    }

    @JavascriptInterface
    public void getContent(String content) {
        String html= new String(Base64Utils.decode(content));
        Intent intent = new Intent();
        intent.putExtra("html",html);
        ((Activity)mContext).setResult(Activity.RESULT_OK, intent);
        ((Activity) mContext).finish();
    }



}
